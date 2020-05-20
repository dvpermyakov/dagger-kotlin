package com.dvpermyakov.dagger.processor

import com.dvpermyakov.dagger.annotation.Binds
import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.spec.type.ModuleBindFunSpecFactory
import com.dvpermyakov.dagger.spec.type.ModuleProvideFunSpecFactory
import com.dvpermyakov.dagger.utils.*
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
                if (element.kind !in setOf(ElementKind.CLASS, ElementKind.INTERFACE)) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only classes can be annotated with @${Module::class.simpleName}"
                    )
                    null
                } else element
            }
            .forEach { moduleElement ->
                moduleElement
                    .getMethodElements()
                    .map { methodElement ->
                        processingEnv.messager.printMessage(
                            Diagnostic.Kind.NOTE,
                            "process module method ${methodElement.simpleName}"
                        )
                        when {
                            methodElement.hasAnnotation(processingEnv, Provide::class.java) -> {
                                val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                                val className = "${moduleElement.simpleName}_${returnTypeElement.simpleName}_Factory"
                                val fileSpecBuilder =
                                    FileSpec.builder(moduleElement.getQualifiedPackageName(processingEnv), className)

                                fileSpecBuilder.addType(
                                    ModuleProvideFunSpecFactory(
                                        processingEnv = processingEnv,
                                        className = className,
                                        moduleElement = moduleElement,
                                        methodElement = methodElement
                                    ).create()
                                )
                                fileSpecBuilder.build()
                            }
                            methodElement.hasAnnotation(processingEnv, Binds::class.java) -> {
                                val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
                                val className = "${moduleElement.simpleName}_${returnTypeElement.simpleName}_Binder"
                                val fileSpecBuilder =
                                    FileSpec.builder(moduleElement.getQualifiedPackageName(processingEnv), className)

                                fileSpecBuilder.addType(
                                    ModuleBindFunSpecFactory(
                                        processingEnv = processingEnv,
                                        className = className,
                                        methodElement = methodElement
                                    ).create()
                                )
                                fileSpecBuilder.build()
                            }
                            else -> throw IllegalStateException("Method in module should have ${Provide::class.java.simpleName} or ${Binds::class.java} annotation")
                        }
                    }
                    .writeToDaggerKotlin(processingEnv)
            }

        return true
    }

}