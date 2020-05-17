package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
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

internal fun Element.getMethodElements(): List<ExecutableElement> {
    return enclosedElements
        .filter { enclosedElement ->
            enclosedElement.kind == ElementKind.METHOD
        }
        .map { element ->
            element as ExecutableElement
        }
}

internal fun Element.getConstructor(): ExecutableElement? {
    return enclosedElements.firstOrNull { enclosedElement ->
        enclosedElement.kind == ElementKind.CONSTRUCTOR
    } as ExecutableElement
}

internal fun ExecutableElement.getReturnElement(
    processingEnv: ProcessingEnvironment
): Element {
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
    return getParameterElements(processingEnv).map { element ->
        element.toClassName(processingEnv)
    }
}

internal fun Element.findAnnotation(
    processingEnv: ProcessingEnvironment,
    annotationClass: Class<*>
): AnnotationMirror? {
    return processingEnv.elementUtils
        .getAllAnnotationMirrors(this)
        .firstOrNull { annotationMirror ->
            val annotationElement = annotationMirror.annotationType.asElement()
            val annotationPackage = processingEnv.elementUtils.getPackageOf(annotationElement).qualifiedName.toString()

            annotationClass.`package`.name == annotationPackage && annotationClass.simpleName == annotationElement.simpleName.toString()
        }
}