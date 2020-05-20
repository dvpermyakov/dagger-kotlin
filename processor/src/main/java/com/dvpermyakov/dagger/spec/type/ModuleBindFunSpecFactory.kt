package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.spec.func.OverrideGetFunSpecFactory
import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ExecutableElement

class ModuleBindFunSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val methodElement: ExecutableElement
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val returnClassName = requireNotNull(methodElement.getReturnElement(processingEnv)).toClassName(processingEnv)

        val parameterClassName = methodElement
            .getParametersClassName(processingEnv)
            .first()

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(parameterClassName.toProviderParameterData().typeName)
            .addSuperinterface(returnClassName.toFactoryClassName())
            .addFunction(
                OverrideGetFunSpecFactory(
                    returnTypeName = returnClassName,
                    statement = "return factory.get()"
                ).create()
            )
            .build()
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        factoryTypeName: TypeName
    ): TypeSpec.Builder {

        val factoryName = "factory"
        val funSpec = FunSpec.constructorBuilder()
            .addParameter(factoryName, factoryTypeName, KModifier.PRIVATE)
            .build()
        val propertySpec = PropertySpec.builder(factoryName, factoryTypeName)
            .initializer(factoryName)
            .build()

        this.primaryConstructor(funSpec)
        this.addProperty(propertySpec)

        return this
    }

}