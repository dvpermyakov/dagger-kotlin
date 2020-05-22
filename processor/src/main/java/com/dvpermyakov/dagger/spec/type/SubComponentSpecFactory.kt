package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.spec.func.ComponentFunSpecFactory
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.dvpermyakov.dagger.utils.element.toClassName
import com.squareup.kotlinpoet.ClassName
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

        graph.addInjectedClassNames(componentInjectedClassNames)

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