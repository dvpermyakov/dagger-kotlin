package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.spec.ModuleFunSpec
import com.dvpermyakov.dagger.utils.writeToDaggerKotlin
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class ModuleProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Module::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        roundEnv.getElementsAnnotatedWith(Module::class.java)
            .mapNotNull { element ->
                if (element.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only classes can be annotated with @${Module::class.simpleName}"
                    )
                    null
                } else element
            }
            .forEach { element ->
                element.enclosedElements
                    .filter { enclosedElement ->
                        enclosedElement.kind == ElementKind.METHOD
                    }
                    .map { methodElement ->
                        val className = "${element.simpleName}_${methodElement.simpleName}_Factory"
                        val fileSpecBuilder = FileSpec.builder("", className)
                        fileSpecBuilder.addType(
                            ModuleFunSpec.getModuleSpec(
                                processingEnv = processingEnv,
                                className = className,
                                moduleElement = element,
                                methodElement = methodElement as ExecutableElement
                            )
                        )

                        fileSpecBuilder.build()
                    }
                    .writeToDaggerKotlin()
            }

        return true
    }

}