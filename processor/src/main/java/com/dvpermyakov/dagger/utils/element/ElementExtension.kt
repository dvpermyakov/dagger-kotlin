package com.dvpermyakov.dagger.utils.element

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*

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

internal fun Element.getNestedInterfaces(): List<Element> {
    return enclosedElements
        .filter { enclosedElement ->
            enclosedElement.kind == ElementKind.INTERFACE
        }
}

internal fun Element.getSuperInterfaces(
    processingEnv: ProcessingEnvironment
): List<Element> {
    return processingEnv.typeUtils.directSupertypes(this.asType())
        .filterIndexed { index, _ ->
            index > 0
        }
        .map { type ->
            type.toElement(processingEnv)
        }
}

internal fun Element.getConstructor(): ExecutableElement? {
    return enclosedElements.firstOrNull { enclosedElement ->
        enclosedElement.kind == ElementKind.CONSTRUCTOR
    } as? ExecutableElement
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

internal fun Element.getAnnotationElements(
    processingEnv: ProcessingEnvironment,
    annotationClass: Class<*>,
    index: Int
): List<Element> {
    val annotation = requireNotNull(this.findAnnotation(processingEnv, annotationClass))
    val annotationIndexValue = annotation.elementValues.entries.elementAt(index).value
    return (annotationIndexValue.value as? List<*>)?.map { annotationValue ->
        val classValue = (annotationValue as AnnotationValue).value.toString()
        processingEnv.elementUtils.getAllTypeElements(classValue).first()
    } ?: throw IllegalStateException("$annotationClass element should contain a list of items")
}

internal fun Element.getAnnotationElementsOrEmpty(
    processingEnv: ProcessingEnvironment,
    annotationClass: Class<*>,
    index: Int
): List<Element> {
    return try {
        getAnnotationElements(processingEnv, annotationClass, index)
    } catch (ignore: Throwable) {
        emptyList()
    }
}