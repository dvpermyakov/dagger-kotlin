package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.spec.type.ComponentSpecFactory
import com.dvpermyakov.dagger.utils.element.getQualifiedPackageName
import com.dvpermyakov.dagger.utils.writeToDaggerKotlin
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ComponentProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Component::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.getElementsAnnotatedWith(Component::class.java)
            .mapNotNull { element ->
                if (element.kind != ElementKind.INTERFACE) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only interfaces can be annotated with @${Component::class.simpleName}"
                    )
                    null
                } else {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "process component ${element.simpleName}"
                    )
                    element
                }
            }
            .map { element ->
                val className = "KDagger${element.simpleName}"
                val fileSpecBuilder = FileSpec.builder(element.getQualifiedPackageName(processingEnv), className)
                fileSpecBuilder.addType(
                    ComponentSpecFactory(
                        processingEnv = processingEnv,
                        className = className,
                        componentElement = element
                    ).create()
                )
                fileSpecBuilder.build()
            }
            .writeToDaggerKotlin(processingEnv)

        return true
    }

}