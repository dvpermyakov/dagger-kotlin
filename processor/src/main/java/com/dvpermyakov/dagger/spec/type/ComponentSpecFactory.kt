package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.BindsInstance
import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
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
import javax.inject.Singleton
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    private val componentClassName: ClassName = componentElement.toClassName(processingEnv)

    // return type element to method element
    private val moduleMethodMap = mutableMapOf<Element, ExecutableElement>()
    private val componentProviders = mutableSetOf<TypeName>()

    override fun create(): TypeSpec {
        componentProviders.clear()

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

        moduleElements
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                    moduleMethodMap[returnTypeElement] = methodElement
                }
            }

        moduleElements
            .excludeInterfaces()
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    typeSpecBuilder.addProviderForElementWithModule(
                        methodElement = methodElement,
                        moduleElement = moduleElement
                    )
                }
            }

        moduleElements
            .interfacesOnly()
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    typeSpecBuilder.addProviderForBinder(
                        methodElement = methodElement,
                        moduleElement = moduleElement
                    )
                }
            }

        componentElement
            .getMethodElements()
            .map { methodElement ->
                val count = methodElement.getParametersClassName(processingEnv).count()
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                if (count == 0 && returnTypeElement != null) {
                    typeSpecBuilder.addFunction(
                        FunSpec.builder(methodElement.simpleName.toString())
                            .addModifiers(KModifier.OVERRIDE)
                            .addStatement(
                                "return ${returnTypeElement.simpleName.toString().decapitalize()}Provider.get()"
                            )
                            .returns(returnTypeElement.toClassName(processingEnv))
                            .build()
                    )
                    typeSpecBuilder.addProviderForElement(returnTypeElement)
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
                                    if (!componentProviders.contains(fieldTypeClassName)) {
                                        typeSpecBuilder.addProviderForElement(fieldTypeElement)
                                    }
                                    val fieldTypeProvider = "${fieldTypeClassName.simpleName.decapitalize()}Provider"
                                    addStatement("$parameterName.${fieldElement.simpleName} = $fieldTypeProvider.get()")
                                }
                            })
                            .build()
                    )
                }
            }

        return typeSpecBuilder.build()
    }

    private fun TypeSpec.Builder.addProviderForElementWithModule(
        methodElement: ExecutableElement,
        moduleElement: Element
    ): TypeSpec.Builder {
        val moduleClassName = moduleElement.toClassName(processingEnv)
        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
        val returnClassName = returnTypeElement.toClassName(processingEnv)

        if (!componentProviders.contains(returnClassName)) {
            val parameterElements = methodElement.getParameterElements(processingEnv)
            parameterElements.forEach { parameterElement ->
                val parameterClassName = parameterElement.toClassName(processingEnv)
                if (!componentProviders.contains(parameterClassName)) {
                    addProviderForElementWithModule(
                        methodElement = requireNotNull(moduleMethodMap[parameterElement]),
                        moduleElement = moduleElement
                    )
                }
            }

            val parameterData = returnClassName.toProviderParameterData()
            val parameterClassNames = parameterElements.toClassNames(processingEnv)
            val parameterNames =
                listOf(moduleClassName.simpleName.decapitalize()) + parameterClassNames.map { it.simpleName.decapitalize() + "Provider" }
            val statementClassName = ClassName(
                moduleClassName.packageName,
                "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Factory"
            )
            val statement = "%T(${parameterNames.joinToString(", ")})"
            val isSingleton = methodElement.hasAnnotation(processingEnv, Singleton::class.java)

            addProperty(
                ComponentProviderProperty(parameterData, statement, statementClassName, isSingleton).create()
            )
            componentProviders.add(returnClassName)
        }

        return this
    }

    private fun TypeSpec.Builder.addProviderForBinder(
        methodElement: ExecutableElement,
        moduleElement: Element
    ): TypeSpec.Builder {
        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
        val returnTypeClassName = returnTypeElement.toClassName(processingEnv)

        val parameterElement = methodElement.getParameterElements(processingEnv).first()
        val parameterClassName = parameterElement.toClassName(processingEnv)
        if (!componentProviders.contains(parameterClassName)) {
            addProviderForElement(parameterElement)
        }

        val moduleClassName = moduleElement.toClassName(processingEnv)
        val statementClassNames = ClassName(
            moduleClassName.packageName,
            "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Binder"
        )
        val statement = "%T(${parameterClassName.simpleName.decapitalize()}Provider)"
        val isSingleton = methodElement.hasAnnotation(processingEnv, Singleton::class.java)

        addProperty(
            ComponentProviderProperty(
                returnTypeClassName.toProviderParameterData(),
                statement,
                statementClassNames,
                isSingleton
            ).create()
        )

        return this
    }

    private fun TypeSpec.Builder.addProviderForElement(
        element: Element
    ): TypeSpec.Builder {
        val className = element.toClassName(processingEnv)
        if (!componentProviders.contains(className)) {
            val constructorElement = element.getConstructor()
            if (constructorElement?.hasAnnotation(processingEnv, Inject::class.java) == true) {
                val parameterElements = constructorElement
                    .getParameterElements(processingEnv)

                parameterElements.forEach { parameterElement ->
                    addProviderForElement(parameterElement)
                }

                val parameterData = className.toProviderParameterData()
                componentProviders.add(className)
                val parameterNames = parameterElements.map { constructorParameterElement ->
                    constructorParameterElement.simpleName.toString().decapitalize() + "Provider"
                }
                val statementClassName = ClassName(className.packageName, "${className.simpleName}_Factory")
                val statement = "%T(${parameterNames.joinToString(", ")})"
                val isSingleton = element.hasAnnotation(processingEnv, Singleton::class.java)
                addProperty(
                    ComponentProviderProperty(
                        parameterData,
                        statement,
                        statementClassName,
                        isSingleton
                    ).create()
                )
                componentProviders.add(className)
            }
        }

        return this
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