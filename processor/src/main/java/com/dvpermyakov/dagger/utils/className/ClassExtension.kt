package com.dvpermyakov.dagger.utils.className

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

internal fun String.toClassName(): ClassName {
    val classNameSplit = split(".")
    val packageName = classNameSplit.subList(0, classNameSplit.lastIndex).joinToString(".")
    val simpleName = classNameSplit.last()
    return ClassName(packageName, simpleName)
}

internal fun Class<*>.toClassName(): ClassName {
    return ClassName(packageName, simpleName)
}

internal fun Class<*>.toElement(
    processingEnv: ProcessingEnvironment
): Element {
    return processingEnv.elementUtils.getTypeElement(name)
}