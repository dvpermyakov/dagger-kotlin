package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.Subcomponent
import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.spec.func.ComponentFunSpecFactory
import com.dvpermyakov.dagger.utils.element.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class SubComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val subcomponentElement: Element,
    private val componentInjectedClassNames: List<ClassName>
) : TypeSpecFactory {

    private val graph = ComponentGraphTraversing(processingEnv)

    override fun create(): TypeSpec {
        val typeSpecBuilder = TypeSpec.anonymousClassBuilder()
            .addSuperinterface(subcomponentElement.toClassName(processingEnv))

        val moduleElements = subcomponentElement.getAnnotationElements(processingEnv, Subcomponent::class.java, 0)
        moduleElements.forEach { moduleElement ->
            val moduleClassName = moduleElement.toClassName(processingEnv)
            typeSpecBuilder.addProperty(
                PropertySpec
                    .builder(
                        name = moduleClassName.simpleName.decapitalize(),
                        type = moduleClassName,
                        modifiers = listOf(KModifier.PRIVATE)
                    )
                    .initializer("%T()", moduleClassName)
                    .build()
            )
        }

        graph.addInjectedClassNames(componentInjectedClassNames)
        graph.addModules(moduleElements)

        subcomponentElement
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