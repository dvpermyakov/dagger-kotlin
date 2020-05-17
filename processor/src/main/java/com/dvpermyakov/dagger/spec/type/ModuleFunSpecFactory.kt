package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.spec.func.OverrideGetFunSpecFactory
import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ModuleFunSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val moduleElement: Element,
    private val methodElement: ExecutableElement
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val moduleClassName = moduleElement.toClassName(processingEnv)
        val returnClassName = methodElement.getReturnElement(processingEnv).toClassName(processingEnv)

        val parameters = methodElement
            .getParametersClassName(processingEnv)
            .map { parameterClassName -> parameterClassName.toProviderParameterData() }

        val getCodeStatement = "return module.${methodElement.simpleName}(" +
            "${parameters.joinToString(", ") { parameter ->
                "${parameter.name}.get()"
            }})"

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassName, parameters)
            .addSuperinterface(returnClassName.toFactoryClassName())
            .addFunction(OverrideGetFunSpecFactory(
                returnTypeName = returnClassName,
                statement = getCodeStatement
            ).create())
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
}