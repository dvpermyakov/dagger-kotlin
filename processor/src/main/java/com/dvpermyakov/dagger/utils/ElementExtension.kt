package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

internal fun Element.getQualifiedPackageName(
    processingEnv: ProcessingEnvironment
): String {
    return processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()
}

internal fun Element.toClassName(
    processingEnv: ProcessingEnvironment
): ClassName {
    val packageName = getQualifiedPackageName(processingEnv)
    return ClassName(packageName, simpleName.toString())
}

internal fun ExecutableElement.getParametersClassName(
    processingEnv: ProcessingEnvironment
): List<ClassName> {
    return parameters.map { parameter ->
        val parameterElement = processingEnv.typeUtils.asElement(parameter.asType())
        val parameterPackage = parameterElement.getQualifiedPackageName(processingEnv)
        ClassName(parameterPackage, parameterElement.simpleName.toString())
    }
}