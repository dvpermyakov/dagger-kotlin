package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.BindsInstance
import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.spec.func.ComponentFunSpecFactory
import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
import com.dvpermyakov.dagger.spec.property.ComponentProviderProperty
import com.dvpermyakov.dagger.utils.ContainerProvider
import com.dvpermyakov.dagger.utils.element.*
import com.dvpermyakov.dagger.utils.toClassName
import com.dvpermyakov.dagger.utils.toParameterData
import com.dvpermyakov.dagger.utils.toProviderParameterData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    private val componentClassName: ClassName = componentElement.toClassName(processingEnv)
    private val graph = ComponentGraphTraversing(processingEnv)

    override fun create(): TypeSpec {
        val typeSpecBuilder = TypeSpec.classBuilder(className).addAnnotation(Generated::class.java)

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
            bindsInstanceClassNames + moduleClassNamesExcludeInterfaces + dependencyElements.toClassNames(processingEnv)
            ).map { className ->
                className.toParameterData()
            }

        typeSpecBuilder
            .primaryConstructor(ConstructorSpecFactory(constructorParameters).create())
            .addType(
                FactoryCompanionObjectSpec(
                    processingEnv = processingEnv,
                    className = className,
                    componentClassName = componentClassName,
                    factoryInterfaceElement = factoryInterfaceElement,
                    factoryMethodElement = factoryCreateFunction,
                    moduleClassNames = moduleClassNamesExcludeInterfaces,
                    dependencyElements = dependencyElements,
                    bindsInstanceClassNames = bindsInstanceClassNames
                ).create()
            )
            .addSuperinterface(componentClassName)

        dependencyElements.forEach { dependencyElement ->
            val dependencyName = dependencyElement.simpleName.toString().decapitalize()
            dependencyElement
                .getMethodElements()
                .forEach { methodElement ->
                    if (methodElement.parameters.isEmpty()) {
                        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                        val returnTypeClassName = returnTypeElement.toClassName(processingEnv)
                        val parameterData = returnTypeClassName.toProviderParameterData()
                        val statement = "%T($dependencyName.${methodElement.simpleName}())"
                        val containerTypeName =
                            ContainerProvider::class.java.toClassName().parameterizedBy(returnTypeClassName)
                        typeSpecBuilder.addProperty(
                            ComponentProviderProperty(
                                parameterData,
                                statement,
                                containerTypeName,
                                false
                            ).create()
                        )
                    }
                }
        }

        bindsInstanceClassNames.forEach { bindsInstanceClassName ->
            val parameterData = bindsInstanceClassName.toProviderParameterData()
            val statement = "%T(${bindsInstanceClassName.simpleName.decapitalize()})"
            val containerTypeName = ContainerProvider::class.java.toClassName().parameterizedBy(bindsInstanceClassName)
            typeSpecBuilder.addProperty(
                ComponentProviderProperty(
                    parameterData,
                    statement,
                    containerTypeName,
                    false
                ).create()
            )
        }

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
                typeSpecBuilder.addFunction(
                    ComponentFunSpecFactory(
                        processingEnv = processingEnv,
                        graph = graph,
                        methodElement = methodElement
                    ).create()
                )
            }

        return typeSpecBuilder.addProperties(graph.getProperties()).build()
    }
}