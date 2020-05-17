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

        providers.clear()

        val moduleElement = processingEnv.elementUtils.getAllTypeElements(moduleClassValue).first()
        moduleElement
            .getMethodElements()
            .forEach { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                val returnClassName = returnTypeElement.toClassName(processingEnv)
                val parameterData = returnClassName.toProviderParameterData()

                val parameters = methodElement.getParametersClassName(processingEnv)
                val parameterNames = listOf(moduleClassName.simpleName.decapitalize()) + parameters.map { it.simpleName.decapitalize() + "Provider" }
                val initStatement = "${moduleClassName.simpleName}_${methodElement.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                typeSpecBuilder.addProviderProperty(parameterData, initStatement)
                providers.add(returnClassName)
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
                typeSpecBuilder.addProviderForElement(returnTypeElement)
            }

        return typeSpecBuilder.build()
    }

    private fun TypeSpec.Builder.addProviderForElement(
        element: Element
    ): TypeSpec.Builder {
        val className = element.toClassName(processingEnv)
        if (!providers.contains(className)) {
            val constructorElement = element.getConstructor()
            val constructorAnnotation = constructorElement?.findAnnotation(processingEnv, Inject::class.java)
            if (constructorAnnotation != null) {
                val parameterElements = constructorElement
                    .getParameterElements(processingEnv)

                parameterElements.forEach { parameterElement ->
                    addProviderForElement(parameterElement)
                }

                val parameterData = className.toProviderParameterData()
                providers.add(className)
                val parameterNames = parameterElements.map { constructorParameterElement ->
                    constructorParameterElement.simpleName.toString().decapitalize() + "Provider"
                }
                val initStatement = "${className.simpleName}_Factory(${parameterNames.joinToString(", ")})"
                addProviderProperty(parameterData, initStatement)
            }
        }

        return this
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        module: ClassName
    ): TypeSpec.Builder {
        val moduleName = module.simpleName.decapitalize()
        val funSpec = FunSpec.constructorBuilder()
            .addParameter(moduleName, module)
            .build()

        this.primaryConstructor(funSpec)

        return this
    }

    private fun TypeSpec.Builder.addProviderProperty(
        parameterData: ParameterData,
        initializer: String
    ): TypeSpec.Builder {
        this.addProperty(
            PropertySpec.builder(parameterData.name, parameterData.typeName, KModifier.PRIVATE)
                .initializer(initializer)
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