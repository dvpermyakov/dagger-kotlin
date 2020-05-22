package com.dvpermyakov.dagger.utils.element

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

internal fun List<Element>.toClassNames(
    processingEnv: ProcessingEnvironment
): List<ClassName> {
    return map { element ->
        element.toClassName(processingEnv)
    }
}

internal fun List<Element>.excludeInterfaces(): List<Element> {
    return filter { element ->
        element.kind != ElementKind.INTERFACE
    }
}

internal fun List<Element>.interfacesOnly(): List<Element> {
    return filter { element ->
        element.kind == ElementKind.INTERFACE
    }
}
