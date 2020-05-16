package com.dvpermyakov.dagger

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.utils.Factory
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
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
                element.enclosedElements.filter { enclosedElement ->
                    enclosedElement.kind == ElementKind.METHOD
                }.map { methodElement ->
                    val className = "${element.simpleName}_${methodElement.simpleName}_Factory"
                    val fileSpecBuilder = FileSpec.builder("", className)
                    fileSpecBuilder.addType(
                        getTypeSpec(
                            className = className,
                            moduleElement = element,
                            methodElement = (methodElement as ExecutableElement)
                        )
                    )

                    fileSpecBuilder.build()
                }.forEach { fileSpec ->
                    val file = File("build/dagger-kotlin")
                    fileSpec.writeTo(file)
                }
            }

        return true
    }

    private fun getTypeSpec(
        className: String,
        moduleElement: Element,
        methodElement: ExecutableElement
    ): TypeSpec {

        val modulePackage = processingEnv.elementUtils.getPackageOf(moduleElement).qualifiedName.toString()
        val moduleClassName = ClassName(modulePackage, moduleElement.simpleName.toString())

        val returnElement = processingEnv.typeUtils.asElement(methodElement.returnType)
        val returnPackage = (returnElement.enclosingElement as PackageElement).qualifiedName.toString()
        val returnClassName = ClassName(returnPackage, returnElement.simpleName.toString())

        val factoryType = Factory::class.java
        val factoryClassName = ClassName(factoryType.packageName, factoryType.simpleName)
        val parameterizedFactoryClassName = factoryClassName.parameterizedBy(returnClassName)

        PropertySpec

        return TypeSpec.classBuilder(className)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("module", moduleClassName, KModifier.PRIVATE)
                    .build()
            )
            .addProperty(PropertySpec.builder("module", moduleClassName).initializer("module").build())
            .addSuperinterface(parameterizedFactoryClassName)
            .addFunction(
                FunSpec.builder("get")
                    .addModifiers(KModifier.OVERRIDE)
                    .addStatement("return module.${methodElement.simpleName}()")
                    .returns(returnClassName)
                    .build()
            )
            .build()
    }
}