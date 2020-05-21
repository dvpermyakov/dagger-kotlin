package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.spec.property.ComponentProviderProperty
import com.dvpermyakov.dagger.utils.element.*
import com.dvpermyakov.dagger.utils.toProviderParameterData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.inject.Singleton
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ComponentGraphTraversing(private val processingEnv: ProcessingEnvironment) {

    private val moduleMethodMap = mutableMapOf<Element, ExecutableElement>()
    private val nodesProperty = mutableMapOf<ClassName, PropertySpec>()

    fun initWithModules(moduleElements: List<Element>) {
        moduleElements
            .forEach { moduleElement ->
                moduleElement.getMethodElements().forEach { methodElement ->
                    val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                    moduleMethodMap[returnTypeElement] = methodElement
                }
            }
        nodesProperty.clear()
    }

    fun addElementWithInjectedConstructor(element: Element) {
        val className = element.toClassName(processingEnv)
        if (!nodesProperty.containsKey(className)) {
            val constructorElement = element.getConstructor()
            if (constructorElement?.hasAnnotation(processingEnv, Inject::class.java) == true) {
                val parameterElements = constructorElement.getParameterElements(processingEnv)
                parameterElements.forEach { parameterElement ->
                    addElementWithInjectedConstructor(parameterElement)
                }

                val parameterData = className.toProviderParameterData()
                val parameterNames = parameterElements.map { parameterElement ->
                    parameterElement.simpleName.toString().decapitalize() + "Provider"
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

        if (!nodesProperty.containsKey(returnClassName)) {
            val parameterElements = methodElement.getParameterElements(processingEnv)
            parameterElements.forEach { parameterElement ->
                val parameterClassName = parameterElement.toClassName(processingEnv)
                if (!nodesProperty.containsKey(parameterClassName)) {
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

    fun getProperties(): List<PropertySpec> {
        return nodesProperty.values.toList()
    }
}