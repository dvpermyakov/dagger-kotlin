package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.BindsInstance
import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
import com.dvpermyakov.dagger.spec.func.ComponentEmptyFunSpecFactory
import com.dvpermyakov.dagger.spec.property.ComponentProviderProperty
import com.dvpermyakov.dagger.utils.ContainerProvider
import com.dvpermyakov.dagger.utils.ParameterData
import com.dvpermyakov.dagger.utils.element.*
import com.dvpermyakov.dagger.utils.toClassName
import com.dvpermyakov.dagger.utils.toProviderParameterData
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.lang.model.element.Element

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    private val componentClassName: ClassName = componentElement.toClassName(processingEnv)
    private val graph = ComponentGraphTraversing(processingEnv)

    override fun create(): TypeSpec {
        val typeSpecBuilder = TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)

        val factoryInterfaceElement = componentElement
            .getNestedInterfaces()
            .firstOrNull { interfaceElement ->
                interfaceElement.hasAnnotation(processingEnv, Component.Factory::class.java)
            }
        val factoryCreateFunction = factoryInterfaceElement
            ?.getMethodElements()
            ?.firstOrNull { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                returnTypeElement == componentElement
            }
        val bindsInstanceElements = factoryCreateFunction
            ?.parameters
            ?.filter { element ->
                element.hasAnnotation(processingEnv, BindsInstance::class.java)
            }
            ?.map { variableElement ->
                variableElement.asType().toElement(processingEnv)
            } ?: emptyList()
        val bindsInstanceClassNames = bindsInstanceElements.toClassNames(processingEnv)

        val moduleElements = componentElement.getAnnotationElements(processingEnv, Component::class.java, 0)
        val moduleClassNamesExcludeInterfaces = moduleElements
            .excludeInterfaces()
            .toClassNames(processingEnv)
        val dependencyElements = componentElement.getAnnotationElements(processingEnv, Component::class.java, 1)

        val constructorParameters = (
            moduleClassNamesExcludeInterfaces +
                dependencyElements.toClassNames(processingEnv) +
                bindsInstanceClassNames
            ).map { className ->
                ParameterData(className, className.simpleName.decapitalize())
            }

        typeSpecBuilder
            .primaryConstructor(ConstructorSpecFactory(constructorParameters).create())
            .setFactoryCompanionObjectSpec(
                factoryInterfaceElement = factoryInterfaceElement,
                factoryMethodElement = factoryCreateFunction,
                moduleClassNames = moduleClassNamesExcludeInterfaces,
                dependencyElements = dependencyElements,
                bindsInstanceClassNames = bindsInstanceClassNames
            )
            .addSuperinterface(componentClassName)

        graph.initWithModules(moduleElements)

        moduleElements
            .excludeInterfaces()
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    graph.addElementInModule(methodElement, moduleElement)
                }
            }

        moduleElements
            .interfacesOnly()
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    graph.addElementWithBinds(methodElement, moduleElement)
                }
            }

        componentElement
            .getMethodElements()
            .map { methodElement ->
                val count = methodElement.getParametersClassName(processingEnv).count()
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                if (count == 0 && returnTypeElement != null) {
                    typeSpecBuilder.addFunction(
                        ComponentEmptyFunSpecFactory(
                            methodName = methodElement.simpleName.toString(),
                            returnTypeClassName = returnTypeElement.toClassName(processingEnv)
                        ).create()
                    )
                    graph.addElementWithInjectedConstructor(returnTypeElement)
                } else {
                    val parameterTypeElement = methodElement.parameters.first()
                    val parameterElement = parameterTypeElement.asType().toElement(processingEnv)
                    val parameterClassName = parameterElement.toClassName(processingEnv)
                    val parameterName = parameterTypeElement.simpleName.toString().decapitalize()
                    val fieldElements = parameterElement.getFieldElements().filter { fieldElement ->
                        fieldElement.hasAnnotation(processingEnv, Inject::class.java)
                    }
                    typeSpecBuilder.addFunction(
                        FunSpec.builder(methodElement.simpleName.toString())
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(parameterName, parameterClassName)
                            .addCode(buildCodeBlock {
                                fieldElements.forEach { fieldElement ->
                                    val fieldTypeElement = fieldElement.asType().toElement(processingEnv)
                                    val fieldTypeClassName = fieldTypeElement.toClassName(processingEnv)
                                    graph.addElementWithInjectedConstructor(fieldTypeElement)
                                    val fieldTypeProvider = "${fieldTypeClassName.simpleName.decapitalize()}Provider"
                                    addStatement("$parameterName.${fieldElement.simpleName} = $fieldTypeProvider.get()")
                                }
                            })
                            .build()
                    )
                }
            }

        typeSpecBuilder.addProperties(graph.getProperties())

        return typeSpecBuilder.build()
    }

    private fun TypeSpec.Builder.setFactoryCompanionObjectSpec(
        factoryInterfaceElement: Element?,
        factoryMethodElement: Element?,
        moduleClassNames: List<ClassName>,
        dependencyElements: List<Element>,
        bindsInstanceClassNames: List<ClassName>
    ): TypeSpec.Builder {

        val dependencyClassNames = dependencyElements.toClassNames(processingEnv)

        dependencyElements.forEach { dependencyElement ->
            val dependencyName = dependencyElement.simpleName.toString().decapitalize()
            val methodElements = dependencyElement.getMethodElements()
            methodElements.forEach { methodElement ->
                if (methodElement.parameters.isEmpty()) {
                    val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                    val returnTypeClassName = returnTypeElement.toClassName(processingEnv)
                    val parameterData = returnTypeClassName.toProviderParameterData()
                    val statement = "%T($dependencyName.${methodElement.simpleName}())"
                    val containerTypeName =
                        ContainerProvider::class.java.toClassName().parameterizedBy(returnTypeClassName)
                    addProperty(ComponentProviderProperty(parameterData, statement, containerTypeName, false).create())
                }
            }
        }

        bindsInstanceClassNames.forEach { bindsInstanceClassName ->
            val parameterData = bindsInstanceClassName.toProviderParameterData()
            val statement = "%T(${bindsInstanceClassName.simpleName.decapitalize()})"
            val containerTypeName = ContainerProvider::class.java.toClassName().parameterizedBy(bindsInstanceClassName)
            addProperty(ComponentProviderProperty(parameterData, statement, containerTypeName, false).create())
        }

        val dependenciesStatement = dependencyClassNames.map { it.simpleName.decapitalize() }
        val bindsStatement = bindsInstanceClassNames.map { it.simpleName.decapitalize() }
        val modulesStatement = List(moduleClassNames.size) { "%T()" }
        val statementCode = (modulesStatement + dependenciesStatement + bindsStatement).joinToString(", ")
        val statement = "return $className(%s)".format(statementCode)

        val bindsParameters = bindsInstanceClassNames.map { bindsInstanceClassName ->
            val bindsInstanceName = bindsInstanceClassName.simpleName.decapitalize()
            ParameterSpec.builder(bindsInstanceName, bindsInstanceClassName).build()
        }
        val dependencyParameters = dependencyClassNames.map { dependencyClassName ->
            val dependencyName = dependencyClassName.simpleName.decapitalize()
            ParameterSpec.builder(dependencyName, dependencyClassName).build()
        }
        val createFunSpec = if (factoryMethodElement != null) {
            FunSpec.builder(factoryMethodElement.simpleName.toString())
                .addParameters(dependencyParameters)
                .addParameters(bindsParameters)
                .addModifiers(KModifier.OVERRIDE)
                .returns(componentClassName)
                .addStatement(statement, *moduleClassNames.toTypedArray())
                .build()
        } else {
            FunSpec.builder("create")
                .addParameters(dependencyParameters)
                .addParameters(bindsParameters)
                .returns(componentClassName)
                .addStatement(statement, *moduleClassNames.toTypedArray())
                .build()
        }

        val companionSpecBuilder = TypeSpec.companionObjectBuilder().addFunction(createFunSpec)
        if (factoryInterfaceElement != null) {
            companionSpecBuilder.addSuperinterface(factoryInterfaceElement.toClassName(processingEnv))
        }

        this.addType(companionSpecBuilder.build())

        return this
    }
}