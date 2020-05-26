package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.spec.property.ComponentPropertySpecFactory
import com.dvpermyakov.dagger.utils.ContainerProvider
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toProviderName
import com.dvpermyakov.dagger.utils.className.toProviderParameterData
import com.dvpermyakov.dagger.utils.element.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.inject.Singleton
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ComponentGraphTraversing(private val processingEnv: ProcessingEnvironment) {

    private val moduleMethodMap = mutableMapOf<Element, ExecutableElement>()
    private val nodesProperty = mutableMapOf<ClassName, PropertySpec>()
    private val alreadyInjectedNodes = mutableSetOf<ClassName>()

    fun addInjectedClassNames(classNames: List<ClassName>) {
        alreadyInjectedNodes.addAll(classNames)
    }

    fun addElementsWithBindsInstance(elements: List<Element>) {
        elements.map { element ->
            element.toClassName(processingEnv)
        }.forEach { className ->
            nodesProperty[className] = ComponentPropertySpecFactory(
                parameterData = className.toProviderParameterData(),
                initializer = "%T(${className.simpleName.decapitalize()})",
                initializerTypeName = ContainerProvider::class.java.toClassName()
                    .parameterizedBy(className),
                isSingleton = false
            ).create()
        }
    }

    fun addDependencyElements(elements: List<Element>) {
        elements.forEach { element ->
            val elementName = element.simpleName.toString().decapitalize()
            element.getMethodElements()
                .forEach { methodElement ->
                    if (methodElement.parameters.isEmpty()) {
                        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                        val returnTypeClassName = returnTypeElement.toClassName(processingEnv)

                        nodesProperty[returnTypeClassName] = ComponentPropertySpecFactory(
                            parameterData = returnTypeClassName.toProviderParameterData(),
                            initializer = "%T($elementName.${methodElement.simpleName}())",
                            initializerTypeName = ContainerProvider::class.java.toClassName()
                                .parameterizedBy(returnTypeClassName),
                            isSingleton = false
                        ).create()
                    }
                }
        }
    }

    fun setModules(moduleElements: List<Element>) {
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
                    addElementInModule(methodElement, moduleElement)
                }
            }

        moduleElements
            .interfacesOnly()
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    addElementWithBinds(methodElement, moduleElement)
                }
            }
    }

    fun addElementWithInjectedConstructor(element: Element) {
        val className = element.toClassName(processingEnv)
        if (!alreadyInjectedNodes.contains(className) && !nodesProperty.containsKey(className)) {
            val constructorElement = element.getConstructor()
            if (constructorElement?.hasAnnotation(processingEnv, Inject::class.java) == true) {
                val parameterElements = constructorElement.getParameterElements(processingEnv)
                parameterElements.forEach { parameterElement ->
                    addElementWithInjectedConstructor(parameterElement)
                }
                val parameterNames = parameterElements.map { parameterElement ->
                    parameterElement.toClassName(processingEnv).toProviderName()
                }

                nodesProperty[className] = ComponentPropertySpecFactory(
                    parameterData = className.toProviderParameterData(),
                    initializer = "%T(${parameterNames.joinToString(", ")})",
                    initializerTypeName = ClassName(className.packageName, "${className.simpleName}_Factory"),
                    isSingleton = element.hasAnnotation(processingEnv, Singleton::class.java)
                ).create()
            }
        }
    }

    fun getClassNames(): List<ClassName> {
        return nodesProperty.keys.toList()
    }

    fun getProperties(): List<PropertySpec> {
        return nodesProperty.values.toList()
    }

    private fun addElementInModule(methodElement: ExecutableElement, moduleElement: Element) {
        val moduleClassName = moduleElement.toClassName(processingEnv)
        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
        val returnClassName = returnTypeElement.toClassName(processingEnv)

        if (!alreadyInjectedNodes.contains(returnClassName) && !nodesProperty.containsKey(returnClassName)) {
            val parameterElements = methodElement.getParameterElements(processingEnv)
            parameterElements.forEach { parameterElement ->
                val parameterClassName = parameterElement.toClassName(processingEnv)
                if (!alreadyInjectedNodes.contains(parameterClassName) && !nodesProperty.containsKey(parameterClassName)) {
                    addElementInModule(
                        methodElement = requireNotNull(moduleMethodMap[parameterElement]),
                        moduleElement = moduleElement
                    )
                }
            }

            val parameterClassNames = parameterElements.toClassNames(processingEnv)
            val parameterNames =
                listOf(moduleClassName.simpleName.decapitalize()) + parameterClassNames.map { it.simpleName.decapitalize() + "Provider" }

            nodesProperty[returnClassName] = ComponentPropertySpecFactory(
                parameterData = returnClassName.toProviderParameterData(),
                initializer = "%T(${parameterNames.joinToString(", ")})",
                initializerTypeName = ClassName(
                    moduleClassName.packageName, "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Factory"
                ),
                isSingleton = methodElement.hasAnnotation(processingEnv, Singleton::class.java)
            ).create()
        }
    }

    private fun addElementWithBinds(methodElement: ExecutableElement, moduleElement: Element) {
        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
        val returnTypeClassName = returnTypeElement.toClassName(processingEnv)

        if (!nodesProperty.containsKey(returnTypeClassName)) {
            val parameterElement = methodElement.getParameterElements(processingEnv).first()
            val parameterClassName = parameterElement.toClassName(processingEnv)
            addElementWithInjectedConstructor(parameterElement)

            val moduleClassName = moduleElement.toClassName(processingEnv)

            val property = ComponentPropertySpecFactory(
                parameterData = returnTypeClassName.toProviderParameterData(),
                initializer = "%T(${parameterClassName.simpleName.decapitalize()}Provider)",
                initializerTypeName = ClassName(
                    moduleClassName.packageName, "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Binder"
                ),
                isSingleton = methodElement.hasAnnotation(processingEnv, Singleton::class.java)
            ).create()
            nodesProperty[returnTypeClassName] = property
        }
    }
}