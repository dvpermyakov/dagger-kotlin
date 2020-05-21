package com.dvpermyakov.dagger.utils.element

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.type.TypeMirror

internal fun TypeMirror.toElement(
    processingEnv: ProcessingEnvironment
): Element {
    return processingEnv.typeUtils.asElement(this)
}