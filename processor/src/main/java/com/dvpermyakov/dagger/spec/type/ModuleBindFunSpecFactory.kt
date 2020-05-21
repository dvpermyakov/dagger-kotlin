package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
import com.dvpermyakov.dagger.spec.func.OverrideGetFunSpecFactory
import com.dvpermyakov.dagger.utils.*
import com.dvpermyakov.dagger.utils.element.getParametersClassName
import com.dvpermyakov.dagger.utils.element.getReturnElement
import com.dvpermyakov.dagger.utils.element.toClassName
import com.squareup.kotlinpoet.TypeSpec
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

        val factoryParameter = ParameterData(parameterClassName.toProviderParameterData().typeName, "factory")

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .primaryConstructor(ConstructorSpecFactory(listOf(factoryParameter)).create())
            .setProperties(listOf(factoryParameter))
            .addSuperinterface(returnClassName.toFactoryClassName())
            .addFunction(
                OverrideGetFunSpecFactory(
                    returnTypeName = returnClassName,
                    statement = "return factory.get()"
                ).create()
            )
            .build()
    }

}