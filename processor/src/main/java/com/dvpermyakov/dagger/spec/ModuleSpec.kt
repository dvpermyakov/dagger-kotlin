package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.utils.Factory
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Provider
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

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
        val returnPackage = processingEnv.elementUtils.getPackageOf(returnElement).qualifiedName.toString()
        val returnClassName = ClassName(returnPackage, returnElement.simpleName.toString())

        val factoryType = Factory::class.java
        val factoryClassName = ClassName(factoryType.packageName, factoryType.simpleName)
        val parameterizedFactoryClassName = factoryClassName.parameterizedBy(returnClassName)

        val parameters = methodElement.parameters.map { parameter ->
            val element = processingEnv.typeUtils.asElement(parameter.asType())
            val parameterPackage = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
            val parameterClassName = ClassName(parameterPackage, element.simpleName.toString())

            val providerType = Provider::class.java
            val providerClassName = ClassName(providerType.packageName, providerType.simpleName)
            val parameterizedProviderClassName = providerClassName.parameterizedBy(parameterClassName)

            ParameterData(
                typeName = parameterizedProviderClassName,
                name = element.simpleName.toString().decapitalize() + "Provider"
            )
        }

        val getCodeStatement = "return module.${methodElement.simpleName}(" +
                "${parameters.joinToString(", ") { parameter ->
                    "${parameter.name}.get()"
                }})"

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassName, parameters)
            .addSuperinterface(parameterizedFactoryClassName)
            .setGetFunctionSpec(returnClassName, getCodeStatement)
            .build()
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        moduleTypeName: TypeName,
        parameters: List<ParameterData>
    ): TypeSpec.Builder {
        val funSpecBuilder = FunSpec.constructorBuilder()
            .addParameter("module", moduleTypeName, KModifier.PRIVATE)

        parameters.forEach { parameter ->
            funSpecBuilder.addParameter(parameter.name, parameter.typeName, KModifier.PRIVATE)
        }

        this.primaryConstructor(funSpecBuilder.build())
        this.addProperty(PropertySpec.builder("module", moduleTypeName).initializer("module").build())

        parameters.forEach { parameter ->
            this.addProperty(
                PropertySpec.builder(parameter.name, parameter.typeName).initializer(parameter.name).build()
            )
        }

        return this
    }

    private fun TypeSpec.Builder.setGetFunctionSpec(
        returnTypeName: TypeName,
        statement: String
    ): TypeSpec.Builder {
        this.addFunction(
            FunSpec.builder("get")
                .addModifiers(KModifier.OVERRIDE)
                .addStatement(statement)
                .returns(returnTypeName)
                .build()
        )
        return this
    }

    private data class ParameterData(
        val typeName: TypeName,
        val name: String
    )
}