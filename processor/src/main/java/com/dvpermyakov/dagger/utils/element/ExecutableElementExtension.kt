package com.dvpermyakov.dagger.utils.element

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

internal fun ExecutableElement.getReturnElement(
    processingEnv: ProcessingEnvironment
): Element? {
    return processingEnv.typeUtils.asElement(returnType)
}

internal fun ExecutableElement.getParameterElements(
    processingEnv: ProcessingEnvironment
): List<Element> {
    return parameters.map { parameter ->
        processingEnv.typeUtils.asElement(parameter.asType())
    }
}

internal fun ExecutableElement.getParametersClassName(
    processingEnv: ProcessingEnvironment
): List<ClassName> {
    return getParameterElements(processingEnv).toClassNames(processingEnv)
}