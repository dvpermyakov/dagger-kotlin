package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Provider
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

object ModuleFunSpec {

    fun getModuleSpec(
        processingEnv: ProcessingEnvironment,
        className: String,
        moduleElement: Element,
        methodElement: ExecutableElement
    ): TypeSpec {

        val moduleClassName = moduleElement.toClassName(processingEnv)

        val returnElement = processingEnv.typeUtils.asElement(methodElement.returnType)
        val returnClassName = returnElement.toClassName(processingEnv)

        val factoryClassName = Factory::class.java.toClassName()
        val parameterizedFactoryClassName = factoryClassName.parameterizedBy(returnClassName)

        val parameters = methodElement
            .getParametersClassName(processingEnv)
            .map { parameterClassName ->
                val providerClassName = Provider::class.java.toClassName()
                val parameterizedProviderClassName = providerClassName.parameterizedBy(parameterClassName)

                ParameterData(
                    typeName = parameterizedProviderClassName,
                    name = parameterClassName.simpleName.decapitalize() + "Provider"
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
}