package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.spec.func.ConstructorSpecFactory
import com.dvpermyakov.dagger.spec.func.OverrideGetFunSpecFactory
import com.dvpermyakov.dagger.utils.*
import com.dvpermyakov.dagger.utils.element.getParametersClassName
import com.dvpermyakov.dagger.utils.element.getReturnElement
import com.dvpermyakov.dagger.utils.element.toClassName
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ModuleProvideFunSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val moduleElement: Element,
    private val methodElement: ExecutableElement
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val moduleClassName = moduleElement.toClassName(processingEnv)
        val returnClassName = requireNotNull(methodElement.getReturnElement(processingEnv)).toClassName(processingEnv)

        val methodParameters = methodElement
            .getParametersClassName(processingEnv)
            .map { parameterClassName -> parameterClassName.toProviderParameterData() }
        val moduleParameter = ParameterData(moduleClassName, "module")
        val constructorParameters = listOf(moduleParameter) + methodParameters

        val getCodeStatement = "return ${moduleParameter.name}.${methodElement.simpleName}(" +
            "${methodParameters.joinToString(", ") { parameter ->
                "${parameter.name}.get()"
            }})"

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .primaryConstructor(ConstructorSpecFactory(constructorParameters).create())
            .setProperties(constructorParameters)
            .addSuperinterface(returnClassName.toFactoryClassName())
            .addFunction(
                OverrideGetFunSpecFactory(
                    returnTypeName = returnClassName,
                    statement = getCodeStatement
                ).create()
            )
            .build()
    }
}