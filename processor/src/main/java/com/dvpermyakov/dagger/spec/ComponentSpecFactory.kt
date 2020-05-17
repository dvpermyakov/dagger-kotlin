package com.dvpermyakov.dagger.spec

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.utils.getMethodElements
import com.dvpermyakov.dagger.utils.getParametersClassName
import com.dvpermyakov.dagger.utils.getReturnElement
import com.dvpermyakov.dagger.utils.toClassName
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Provider
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) {

    fun create(): TypeSpec {
        val componentClassName = componentElement.toClassName(processingEnv)

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

        val moduleClassName = moduleClassValue.toClassName()

        val typeSpec = TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassName)
            .addSuperinterface(componentClassName)

        val initBlockBuilder = CodeBlock.builder()

        val moduleElement = processingEnv.elementUtils.getAllTypeElements(moduleClassValue).first()
        moduleElement
            .getMethodElements()
            .forEach { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                val returnClassName = returnTypeElement.toClassName(processingEnv)
                val providerClassName = Provider::class.java.toClassName().parameterizedBy(returnClassName)
                val fieldName = returnClassName.simpleName.decapitalize() + "Provider"
                typeSpec.addProperty(
                    PropertySpec.builder(fieldName, providerClassName)
                        .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
                        .mutable(true)
                        .build()
                )

                val parameters = methodElement.getParametersClassName(processingEnv)
                val parameterNames = listOf(moduleClassName.simpleName.decapitalize()) + parameters.map { it.simpleName.decapitalize() + "Provider" }
                val initStatement = "$fieldName = ${moduleClassName.simpleName}_${methodElement.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                initBlockBuilder.addStatement(initStatement)
            }

        typeSpec.addInitializerBlock(initBlockBuilder.build())

        componentElement
            .getMethodElements()
            .map { methodElement ->
                val count = methodElement.getParametersClassName(processingEnv).count()
                if (count > 0) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Method ${methodElement.simpleName} has $count parameters in interface ${componentClassName.simpleName}. You shouldn't put parameters there."
                    )
                }
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                val returnClassName = returnTypeElement.toClassName(processingEnv)
                typeSpec.addOverrideFunSpec(
                    funName = methodElement.simpleName.toString(),
                    returnTypeName = methodElement.getReturnElement(processingEnv).toClassName(processingEnv),
                    statement = "return ${returnClassName.simpleName.decapitalize()}Provider.get()"
                )
            }

        return typeSpec.build()
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        module: ClassName
    ): TypeSpec.Builder {
        val moduleName = module.simpleName.decapitalize()
        val funSpec = FunSpec.constructorBuilder()
            .addParameter(moduleName, module, KModifier.PRIVATE)
            .build()

        this.primaryConstructor(funSpec)
        this.addProperty(PropertySpec.builder(moduleName, module).initializer(moduleName).build())

        return this
    }

    private fun TypeSpec.Builder.addOverrideFunSpec(
        funName: String,
        returnTypeName: TypeName,
        statement: String
    ): TypeSpec.Builder {
        this.addFunction(
            FunSpec.builder(funName)
                .addModifiers(KModifier.OVERRIDE)
                .addStatement(statement)
                .returns(returnTypeName)
                .build()
        )
        return this
    }
}