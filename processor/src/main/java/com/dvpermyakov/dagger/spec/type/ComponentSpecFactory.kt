package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.inject.Provider
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val componentClassName = componentElement.toClassName(processingEnv)

        val componentAnnotation = requireNotNull(componentElement.findAnnotation(processingEnv, Component::class.java))
        val componentAnnotationModuleValue = componentAnnotation.elementValues.entries.first().value
        val moduleClassValue = componentAnnotationModuleValue.value.toString()

        val moduleClassName = moduleClassValue.toClassName()

        val typeSpec = TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassName)
            .addSuperinterface(componentClassName)

        val initBlockBuilder = CodeBlock.builder()
        val providers = mutableSetOf<TypeName>()

        val moduleElement = processingEnv.elementUtils.getAllTypeElements(moduleClassValue).first()
        moduleElement
            .getMethodElements()
            .forEach { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                val returnClassName = returnTypeElement.toClassName(processingEnv)
                val parameterData = returnClassName.toParameterDataWithProvider()

                typeSpec.addProviderProperty(parameterData)
                providers.add(returnClassName)

                val parameters = methodElement.getParametersClassName(processingEnv)
                val parameterNames = listOf(moduleClassName.simpleName.decapitalize()) + parameters.map { it.simpleName.decapitalize() + "Provider" }
                val initStatement = "${parameterData.name} = ${moduleClassName.simpleName}_${methodElement.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                initBlockBuilder.addStatement(initStatement)
            }

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

                if (!providers.contains(returnClassName)) {
                    val parameterData = returnClassName.toParameterDataWithProvider()
                    typeSpec.addProviderProperty(parameterData)

                    val constructorElement = returnTypeElement.getConstructor()
                    val constructorAnnotation = constructorElement.findAnnotation(processingEnv, Inject::class.java)
                    if (constructorAnnotation != null) {
                        val parameterNames = constructorElement
                            .getParameterElements(processingEnv)
                            .map { constructorParameterElement ->
                                constructorParameterElement.simpleName.toString().decapitalize() + "Provider"
                            }
                        val initStatement = "${parameterData.name} = ${returnClassName.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                        initBlockBuilder.addStatement(initStatement)
                    }
                }
            }

        typeSpec.addInitializerBlock(initBlockBuilder.build())

        return typeSpec.build()
    }

    private fun ClassName.toParameterDataWithProvider(): ParameterData {
        val providerClassName = Provider::class.java.toClassName().parameterizedBy(this)
        val fieldName = this.simpleName.decapitalize() + "Provider"
        return ParameterData(providerClassName, fieldName)
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

    private fun TypeSpec.Builder.addProviderProperty(
        parameterData: ParameterData
    ): TypeSpec.Builder {
        this.addProperty(
            PropertySpec.builder(parameterData.name, parameterData.typeName)
                .addModifiers(KModifier.PRIVATE, KModifier.LATEINIT)
                .mutable(true)
                .build()
        )

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