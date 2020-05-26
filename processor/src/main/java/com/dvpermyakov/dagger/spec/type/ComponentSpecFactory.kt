package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.BindsInstance
import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.annotation.Subcomponent
import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.spec.func.ComponentFunSpecFactory
import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
import com.dvpermyakov.dagger.utils.className.toParameterData
import com.dvpermyakov.dagger.utils.element.*
import com.squareup.kotlinpoet.ClassName
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

        val moduleElements = componentElement.getAnnotationElements(processingEnv, Component::class.java, 0)
        val moduleClassNamesExcludeInterfaces = moduleElements
            .excludeInterfaces()
            .toClassNames(processingEnv)
        val dependencyElements = componentElement.getAnnotationElements(processingEnv, Component::class.java, 1)

        val constructorParameters = (
            moduleClassNamesExcludeInterfaces +
                dependencyElements.toClassNames(processingEnv) +
                bindsInstanceElements.toClassNames(processingEnv)
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
                    otherClassNames = factoryCreateFunction?.getParametersClassName(processingEnv) ?: emptyList()
                ).create()
            )
            .addSuperinterface(componentClassName)

        graph.addElementsWithBindsInstance(bindsInstanceElements)
        graph.addDependencyElements(dependencyElements)
        graph.addModules(moduleElements)

        val componentMethodElements = componentElement
            .getMethodElements()
            .sortedBy { methodElement ->
                methodElement.getReturnElement(processingEnv)
                    ?.hasAnnotation(processingEnv, Subcomponent::class.java)
            }
        val superInterfacesMethodElements = componentElement
            .getSuperInterfaces(processingEnv)
            .flatMap { superInterfaceElement ->
                superInterfaceElement.getMethodElements()
            }

        (componentMethodElements + superInterfacesMethodElements).forEach { methodElement ->
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