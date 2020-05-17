package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    private val providers = mutableSetOf<TypeName>()

    override fun create(): TypeSpec {
        val componentClassName = componentElement.toClassName(processingEnv)

        val componentAnnotation = requireNotNull(componentElement.findAnnotation(processingEnv, Component::class.java))
        val componentAnnotationModuleValue = componentAnnotation.elementValues.entries.first().value
        val moduleClassValue = componentAnnotationModuleValue.value.toString()

        val moduleClassName = moduleClassValue.toClassName()

        val typeSpecBuilder = TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassName)
            .addSuperinterface(componentClassName)

        val initBlockBuilder = CodeBlock.builder()
        providers.clear()

        val moduleElement = processingEnv.elementUtils.getAllTypeElements(moduleClassValue).first()
        moduleElement
            .getMethodElements()
            .forEach { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                val returnClassName = returnTypeElement.toClassName(processingEnv)
                val parameterData = returnClassName.toProviderParameterData()

                typeSpecBuilder.addProviderProperty(parameterData)
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
                typeSpecBuilder.addOverrideFunSpec(
                    funName = methodElement.simpleName.toString(),
                    returnTypeName = methodElement.getReturnElement(processingEnv).toClassName(processingEnv),
                    statement = "return ${returnTypeElement.simpleName.toString().decapitalize()}Provider.get()"
                )
                typeSpecBuilder.addProviderForElement(
                    element = returnTypeElement,
                    initBlockBuilder = initBlockBuilder
                )
            }

        typeSpecBuilder.addInitializerBlock(initBlockBuilder.build())

        return typeSpecBuilder.build()
    }

    private fun TypeSpec.Builder.addProviderForElement(
        element: Element,
        initBlockBuilder: CodeBlock.Builder
    ): TypeSpec.Builder {
        val className = element.toClassName(processingEnv)
        if (!providers.contains(className)) {
            val parameterData = className.toProviderParameterData()
            addProviderProperty(parameterData)
            providers.add(className)

            val constructorElement = element.getConstructor()
            val constructorAnnotation = constructorElement?.findAnnotation(processingEnv, Inject::class.java)
            if (constructorAnnotation != null) {
                val parameterElements = constructorElement
                    .getParameterElements(processingEnv)

                parameterElements.forEach { parameterElement ->
                    addProviderForElement(parameterElement, initBlockBuilder)
                }

                val parameterNames = parameterElements.map { constructorParameterElement ->
                    constructorParameterElement.simpleName.toString().decapitalize() + "Provider"
                }

                val initStatement = "${parameterData.name} = ${className.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                initBlockBuilder.addStatement(initStatement)
            }
        }

        return this
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