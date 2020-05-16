package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.spec.ComponentSpec
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import java.io.File
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
                } else element
            }
            .map { element ->
                val className = "KDagger${element.simpleName}"
                val fileSpecBuilder = FileSpec.builder("", className)
                fileSpecBuilder.addType(
                    ComponentSpec.getComponentSpec(
                        processingEnv = processingEnv,
                        className = className,
                        componentElement = element
                    )
                )
                fileSpecBuilder.build()
            }.forEach { fileSpec ->
                val file = File("build/dagger-kotlin")
                fileSpec.writeTo(file)
            }

        return true
    }

}