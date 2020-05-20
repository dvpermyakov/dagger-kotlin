package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.TypeMirror

internal fun TypeMirror.toElement(
    processingEnv: ProcessingEnvironment
): Element {
    return processingEnv.typeUtils.asElement(this)
}

internal fun Element.getQualifiedPackageName(
    processingEnv: ProcessingEnvironment
): String {
    return processingEnv.elementUtils.getPackageOf(this).qualifiedName.toString()
}

internal fun Element.toClassName(
    processingEnv: ProcessingEnvironment
): ClassName {
    val name = if (enclosingElement.kind in setOf(ElementKind.CLASS, ElementKind.INTERFACE)) {
        "${enclosingElement.simpleName}.$simpleName"
    } else simpleName.toString()

    val packageName = getQualifiedPackageName(processingEnv)
    return ClassName(packageName, name)
}

internal fun List<Element>.toClassNames(
    processingEnv: ProcessingEnvironment
): List<ClassName> {
    return map { element ->
        element.toClassName(processingEnv)
    }
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

internal fun Element.getFieldElements(): List<Element> {
    return enclosedElements
        .filter { enclosedElement ->
            enclosedElement.kind == ElementKind.FIELD
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

internal fun Element.getNestedInterfaces(): List<Element> {
    return enclosedElements
        .filter { enclosedElement ->
            enclosedElement.kind == ElementKind.INTERFACE
        }
}

internal fun Element.getConstructor(): ExecutableElement? {
    return enclosedElements.firstOrNull { enclosedElement ->
        enclosedElement.kind == ElementKind.CONSTRUCTOR
    } as? ExecutableElement
}

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

internal fun Element.hasAnnotation(
    processingEnv: ProcessingEnvironment,
    annotationClass: Class<*>
): Boolean {
    return findAnnotation(processingEnv, annotationClass) != null
}