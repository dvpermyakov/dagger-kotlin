package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.utils.Factory
import com.dvpermyakov.dagger.utils.ParameterData
import com.dvpermyakov.dagger.utils.getParametersClassName
import com.dvpermyakov.dagger.utils.toClassName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Provider
import javax.lang.model.element.ExecutableElement

object InjectConstructorSpec {

    fun getInjectConstructorSpec(
        processingEnv: ProcessingEnvironment,
        className: String,
        constructorElement: ExecutableElement
    ): TypeSpec {

        val classElement = constructorElement.enclosingElement
        val classClassName = classElement.toClassName(processingEnv)

        val parameters = constructorElement
            .getParametersClassName(processingEnv)
            .map { parameterClassName ->
                val providerClassName = Provider::class.java.toClassName()
                val parameterizedProviderClassName = providerClassName.parameterizedBy(parameterClassName)

                ParameterData(
                    typeName = parameterizedProviderClassName,
                    name = parameterClassName.simpleName.decapitalize() + "Provider"
                )
            }

        val factoryClassName = Factory::class.java.toClassName()
        val parameterizedFactoryClassName = factoryClassName.parameterizedBy(classClassName)

        val getCodeStatement = "return ${classClassName.simpleName}(" +
                "${parameters.joinToString(", ") { parameter ->
                    "${parameter.name}.get()"
                }})"

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(parameters)
            .addSuperinterface(parameterizedFactoryClassName)
            .setGetFunctionSpec(
                returnTypeName = classClassName,
                statement = getCodeStatement
            )
            .build()
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        parameters: List<ParameterData>
    ): TypeSpec.Builder {
        val funSpecBuilder = FunSpec.constructorBuilder()

        parameters.forEach { parameter ->
            funSpecBuilder.addParameter(parameter.name, parameter.typeName, KModifier.PRIVATE)
        }

        this.primaryConstructor(funSpecBuilder.build())

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