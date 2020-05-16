package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.annotation.Component
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

object ComponentSpec {

    fun getComponentSpec(
        processingEnv: ProcessingEnvironment,
        className: String,
        componentElement: Element
    ): TypeSpec {

        val componentPackage = processingEnv.elementUtils.getPackageOf(componentElement).qualifiedName.toString()
        val componentClassName = ClassName(componentPackage, componentElement.simpleName.toString())

        val componentAnnotation = processingEnv.elementUtils
            .getAllAnnotationMirrors(componentElement)
            .first { annotationMirror ->
                val annotationElement = annotationMirror.annotationType.asElement()
                val annotationPackage =
                    processingEnv.elementUtils.getPackageOf(annotationElement).qualifiedName.toString()

                val componentClass = Component::class.java
                componentClass.`package`.name == annotationPackage && componentClass.simpleName == annotationElement.simpleName.toString()
            }

        val componentAnnotationModuleValue = componentAnnotation.elementValues.entries.first().value
        val moduleClassValue = componentAnnotationModuleValue.value.toString()

        val moduleClassNameSplit = moduleClassValue.split(".")
        val modulePackage = moduleClassNameSplit.subList(0, moduleClassNameSplit.lastIndex).joinToString(".")
        val moduleSimpleName = moduleClassNameSplit.last()
        val moduleClassName = ClassName(modulePackage, moduleSimpleName)

        return TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(ParameterData(moduleClassName, moduleSimpleName.decapitalize()))
            .addSuperinterface(componentClassName)
            .build()
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        module: ParameterData
    ): TypeSpec.Builder {
        val funSpec = FunSpec.constructorBuilder()
            .addParameter(module.name, module.typeName, KModifier.PRIVATE)
            .build()

        this.primaryConstructor(funSpec)
        this.addProperty(PropertySpec.builder(module.name, module.typeName).initializer(module.name).build())

        return this
    }
}