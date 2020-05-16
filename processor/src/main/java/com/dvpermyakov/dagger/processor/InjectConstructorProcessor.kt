package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.spec.InjectConstructorSpec
import com.dvpermyakov.dagger.utils.writeToDaggerKotlin
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.inject.Inject
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class InjectConstructorProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Inject::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(Inject::class.java)
            .mapNotNull { element ->
                if (element.kind != ElementKind.CONSTRUCTOR) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only constructors can be annotated with @${Inject::class.simpleName}"
                    )
                    null
                } else element
            }
            .map { element ->
                val classElement = element.enclosingElement
                val className = "${classElement.simpleName}_Factory"
                val fileSpecBuilder = FileSpec.builder("", className)
                fileSpecBuilder.addType(
                    InjectConstructorSpec.getInjectConstructorSpec(
                        processingEnv = processingEnv,
                        className = className,
                        constructorElement = element as ExecutableElement
                    )
                )

                fileSpecBuilder.build()
            }
            .writeToDaggerKotlin()

        return true
    }
}