package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.utils.ParameterData
import com.dvpermyakov.dagger.utils.element.*
import com.dvpermyakov.dagger.utils.setParameterData
import com.dvpermyakov.dagger.utils.toProviderName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.buildCodeBlock
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

class ComponentFunSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val graph: ComponentGraphTraversing,
    private val methodElement: ExecutableElement
) : FunSpecFactory {

    override fun create(): FunSpec {
        val parameterCount = methodElement.getParametersClassName(processingEnv).count()
        val returnTypeElement = methodElement.getReturnElement(processingEnv)
        return if (parameterCount == 0 && returnTypeElement != null) {
            graph.addElementWithInjectedConstructor(returnTypeElement)
            createReturnTypeFun(returnTypeClassName = returnTypeElement.toClassName(processingEnv))
        } else {
            val parameterTypeElement = methodElement.parameters.first()
            val parameterElement = parameterTypeElement.asType().toElement(processingEnv)
            val fieldElements = parameterElement.getFieldElements().filter { fieldElement ->
                fieldElement.hasAnnotation(processingEnv, Inject::class.java)
            }
            fieldElements.forEach { fieldElement ->
                val fieldTypeElement = fieldElement.asType().toElement(processingEnv)
                graph.addElementWithInjectedConstructor(fieldTypeElement)
            }
            createOneParameterFun(
                parameterData = ParameterData(
                    name = parameterTypeElement.simpleName.toString().decapitalize(),
                    typeName = parameterElement.toClassName(processingEnv)
                ),
                fieldElements = fieldElements
            )
        }
    }

    private fun createReturnTypeFun(
        returnTypeClassName: ClassName
    ): FunSpec {
        return FunSpec.builder(methodElement.simpleName.toString())
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("return ${returnTypeClassName.toProviderName()}.get()")
            .returns(returnTypeClassName)
            .build()
    }

    private fun createOneParameterFun(
        parameterData: ParameterData,
        fieldElements: List<Element>
    ): FunSpec {
        return FunSpec.builder(methodElement.simpleName.toString())
            .addModifiers(KModifier.OVERRIDE)
            .setParameterData(parameterData)
            .addCode(buildCodeBlock {
                fieldElements.forEach { fieldElement ->
                    val fieldTypeElement = fieldElement.asType().toElement(processingEnv)
                    val fieldTypeClassName = fieldTypeElement.toClassName(processingEnv)
                    val fieldTypeProvider = fieldTypeClassName.toProviderName()
                    addStatement("${parameterData.name}.${fieldElement.simpleName} = $fieldTypeProvider.get()")
                }
            })
            .build()
    }
}