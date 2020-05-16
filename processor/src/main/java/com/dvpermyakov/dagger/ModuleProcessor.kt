package com.dvpermyakov.dagger

import com.dvpermyakov.dagger.annotation.Module
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
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
                val methodName = "methodName"
                val fileName = "${element.simpleName}_${methodName}_Factory"
                val fileSpecBuilder = FileSpec.builder("", fileName)
                fileSpecBuilder.addType(getTypeSpec(element))

                val fileSpec = fileSpecBuilder.build()
                val file = File("build/dagger-kotlin")
                fileSpec.writeTo(file)
            }

        return true
    }

    private fun getTypeSpec(element: Element): TypeSpec {
        return TypeSpec.classBuilder("Greeter")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("name", String::class)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("name", String::class)
                    .initializer("name")
                    .build()
            )
            .addFunction(
                FunSpec.builder("greet")
                    .addStatement("println(%P)", "Hello, \$name")
                    .build()
            )
            .build()
    }
}