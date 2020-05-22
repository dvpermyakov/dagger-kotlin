package com.dvpermyakov.dagger.utils

import javax.annotation.processing.Messager
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class MockMessager : Messager {
    override fun printMessage(
        p0: Diagnostic.Kind?,
        p1: CharSequence?
    ) = Unit

    override fun printMessage(
        p0: Diagnostic.Kind?,
        p1: CharSequence?,
        p2: Element?
    ) = Unit

    override fun printMessage(
        p0: Diagnostic.Kind?,
        p1: CharSequence?,
        p2: Element?,
        p3: AnnotationMirror?
    ) = Unit

    override fun printMessage(
        p0: Diagnostic.Kind?,
        p1: CharSequence?,
        p2: Element?,
        p3: AnnotationMirror?,
        p4: AnnotationValue?
    ) = Unit
}