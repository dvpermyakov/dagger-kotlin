package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.spec.type.ModuleFunSpecFactory
import com.dvpermyakov.dagger.utils.getMethodElements
import com.dvpermyakov.dagger.utils.getReturnElement
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
                element
                    .getMethodElements()
                    .map { methodElement ->
                        val returnTypeElement = methodElement.getReturnElement(processingEnv)
                        val className = "${element.simpleName}_${returnTypeElement.simpleName}_Factory"
                        val fileSpecBuilder = FileSpec.builder("", className)
                        fileSpecBuilder.addType(
                            ModuleFunSpecFactory(
                                processingEnv = processingEnv,
                                className = className,
                                moduleElement = element,
                                methodElement = methodElement
                            ).create()
                        )

                        fileSpecBuilder.build()
                    }
                    .writeToDaggerKotlin()
            }

        return true
    }

}