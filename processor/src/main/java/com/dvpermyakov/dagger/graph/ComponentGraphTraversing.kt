package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.spec.property.ComponentProviderProperty
import com.dvpermyakov.dagger.utils.ContainerProvider
import com.dvpermyakov.dagger.utils.element.*
import com.dvpermyakov.dagger.utils.toClassName
import com.dvpermyakov.dagger.utils.toProviderName
import com.dvpermyakov.dagger.utils.toProviderParameterData
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

    fun setModules(moduleElements: List<Element>) {
        moduleElements
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                    moduleMethodMap[returnTypeElement] = methodElement
                }
            }
    }

    fun addElementsWithBindsInstance(elements: List<Element>) {
        elements.map { element ->
            element.toClassName(processingEnv)
        }.forEach { bindsInstanceClassName ->
            val parameterData = bindsInstanceClassName.toProviderParameterData()
            val statement = "%T(${bindsInstanceClassName.simpleName.decapitalize()})"
            val containerTypeName = ContainerProvider::class.java.toClassName().parameterizedBy(bindsInstanceClassName)
            nodesProperty[bindsInstanceClassName] = ComponentProviderProperty(
                parameterData,
                statement,
                containerTypeName,
                false
            ).create()
        }
    }

    fun addDependencyElements(elements: List<Element>) {
        elements.forEach { dependencyElement ->
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
                        nodesProperty[returnTypeClassName] = ComponentProviderProperty(
                            parameterData,
                            statement,
                            containerTypeName,
                            false
                        ).create()
                    }
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

                val parameterData = className.toProviderParameterData()
                val parameterNames = parameterElements.map { parameterElement ->
                    parameterElement.toClassName(processingEnv).toProviderName()
                }
                val statementClassName = ClassName(className.packageName, "${className.simpleName}_Factory")
                val statement = "%T(${parameterNames.joinToString(", ")})"
                val isSingleton = element.hasAnnotation(processingEnv, Singleton::class.java)

                nodesProperty[className] = ComponentProviderProperty(
                    parameterData,
                    statement,
                    statementClassName,
                    isSingleton
                ).create()
            }
        }
    }

    fun addElementInModule(methodElement: ExecutableElement, moduleElement: Element) {
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

            nodesProperty[returnClassName] = ComponentProviderProperty(
                parameterData,
                statement,
                statementClassName,
                isSingleton
            ).create()
        }
    }

    fun addElementWithBinds(methodElement: ExecutableElement, moduleElement: Element) {
        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
        val returnTypeClassName = returnTypeElement.toClassName(processingEnv)

        if (!nodesProperty.containsKey(returnTypeClassName)) {
            val parameterElement = methodElement.getParameterElements(processingEnv).first()
            val parameterClassName = parameterElement.toClassName(processingEnv)
            addElementWithInjectedConstructor(parameterElement)

            val moduleClassName = moduleElement.toClassName(processingEnv)
            val statementClassNames = ClassName(
                moduleClassName.packageName,
                "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Binder"
            )
            val statement = "%T(${parameterClassName.simpleName.decapitalize()}Provider)"
            val isSingleton = methodElement.hasAnnotation(processingEnv, Singleton::class.java)

            val property = ComponentProviderProperty(
                returnTypeClassName.toProviderParameterData(),
                statement,
                statementClassNames,
                isSingleton
            ).create()
            nodesProperty[returnTypeClassName] = property
        }
    }

    fun getClassNames(): List<ClassName> {
        return nodesProperty.keys.toList()
    }

    fun getProperties(): List<PropertySpec> {
        return nodesProperty.values.toList()
    }
}