package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.utils.Factory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement

object ModuleSpec {
    fun getModuleSpec(
        processingEnv: ProcessingEnvironment,
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