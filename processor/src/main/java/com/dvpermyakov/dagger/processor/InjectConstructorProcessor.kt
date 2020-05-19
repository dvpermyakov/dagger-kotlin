package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.spec.type.InjectConstructorSpecFactory
import com.dvpermyakov.dagger.utils.getQualifiedPackageName
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
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "process constructor of ${classElement.simpleName}"
                )

                val className = "${classElement.simpleName}_Factory"
                val fileSpecBuilder = FileSpec.builder(element.getQualifiedPackageName(processingEnv), className)
                fileSpecBuilder.addType(
                    InjectConstructorSpecFactory(
                        processingEnv = processingEnv,
                        className = className,
                        constructorElement = element as ExecutableElement
                    ).create()
                )

                fileSpecBuilder.build()
            }
            .writeToDaggerKotlin(processingEnv)

        return true
    }
}