package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.utils.*
import com.squareup.kotlinpoet.*
import java.lang.IllegalStateException
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.inject.Inject
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic

class ComponentSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val className: String,
    private val componentElement: Element
) : TypeSpecFactory {

    private val componentClassName: ClassName = componentElement.toClassName(processingEnv)

    // return type element to method element
    private val moduleMethodMap = mutableMapOf<Element, ExecutableElement>()
    private val componentProviders = mutableSetOf<TypeName>()

    override fun create(): TypeSpec {

        val moduleElements = getModuleElements()
        val moduleClassNames = moduleElements.map { it.toClassName(processingEnv) }

        val typeSpecBuilder = TypeSpec.classBuilder(className)
            .addAnnotation(Generated::class.java)
            .setConstructorSpec(moduleClassNames)
            .setFactoryCompanionObjectSpec(moduleClassNames)
            .addSuperinterface(componentClassName)

        moduleElements.forEach { moduleElement ->
            moduleElement.getMethodElements().forEach { methodElement ->
                val returnTypeElement = methodElement.getReturnElement(processingEnv)
                moduleMethodMap[returnTypeElement] = methodElement
            }
        }

        componentProviders.clear()
        moduleElements.forEach { moduleElement ->
            moduleElement.getMethodElements().forEach { methodElement ->
                typeSpecBuilder.addProviderForElementWithModule(
                    methodElement = methodElement,
                    moduleElement = moduleElement
                )
            }
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
                    returnTypeName = returnTypeElement.toClassName(processingEnv),
                    statement = "return ${returnTypeElement.simpleName.toString().decapitalize()}Provider.get()"
                )
                typeSpecBuilder.addProviderForElement(returnTypeElement)
            }

        return typeSpecBuilder.build()
    }

    private fun getModuleElements(): List<Element> {
        val componentAnnotation = requireNotNull(componentElement.findAnnotation(processingEnv, Component::class.java))
        val componentAnnotationModulesValue = componentAnnotation.elementValues.entries.first().value
        return (componentAnnotationModulesValue.value as? List<*>)?.map { annotationValue ->
            val moduleClassValue = (annotationValue as AnnotationValue).value.toString()
            processingEnv.elementUtils.getAllTypeElements(moduleClassValue).first()
        } ?: throw IllegalStateException("${Component::class.java} element should contain a module list")
    }

    private fun TypeSpec.Builder.addProviderForElementWithModule(
        methodElement: ExecutableElement,
        moduleElement: Element
    ): TypeSpec.Builder {
        val moduleClassName = moduleElement.toClassName(processingEnv)
        val returnTypeElement = methodElement.getReturnElement(processingEnv)
        val returnClassName = returnTypeElement.toClassName(processingEnv)

        if (!componentProviders.contains(returnClassName)) {
            val parameterElements = methodElement.getParameterElements(processingEnv)
            parameterElements.forEach { parameterElement ->
                val parameterClassName = parameterElement.toClassName(processingEnv)
                if (!componentProviders.contains(parameterClassName)) {
                    addProviderForElementWithModule(
                        methodElement = requireNotNull(moduleMethodMap[parameterElement]),
                        moduleElement = moduleElement
                    )
                }
            }

            val parameterData = returnClassName.toProviderParameterData()
            val parameterClassNames = parameterElements.map { it.toClassName(processingEnv) }
            val parameterNames =
                listOf(moduleClassName.simpleName.decapitalize()) + parameterClassNames.map { it.simpleName.decapitalize() + "Provider" }
            val statementClassName = ClassName(
                moduleClassName.packageName,
                "${moduleClassName.simpleName}_${returnTypeElement.simpleName}_Factory"
            )
            val statement = "%T(${parameterNames.joinToString(", ")})"
            addProviderProperty(parameterData, statement, statementClassName)
            componentProviders.add(returnClassName)
        }

        return this
    }

    private fun TypeSpec.Builder.addProviderForElement(
        element: Element
    ): TypeSpec.Builder {
        val className = element.toClassName(processingEnv)
        if (!componentProviders.contains(className)) {
            val constructorElement = element.getConstructor()
            val constructorAnnotation = constructorElement?.findAnnotation(processingEnv, Inject::class.java)
            if (constructorAnnotation != null) {
                val parameterElements = constructorElement
                    .getParameterElements(processingEnv)

                parameterElements.forEach { parameterElement ->
                    addProviderForElement(parameterElement)
                }

                val parameterData = className.toProviderParameterData()
                componentProviders.add(className)
                val parameterNames = parameterElements.map { constructorParameterElement ->
                    constructorParameterElement.simpleName.toString().decapitalize() + "Provider"
                }
                val statementClassName = ClassName(className.packageName, "${className.simpleName}_Factory")
                val statement = "%T(${parameterNames.joinToString(", ")})"
                addProviderProperty(parameterData, statement, statementClassName)
                componentProviders.add(className)
            }
        }

        return this
    }

    private fun TypeSpec.Builder.setConstructorSpec(
        moduleClassNames: List<ClassName>
    ): TypeSpec.Builder {
        val funSpecBuilder = FunSpec.constructorBuilder().addModifiers(KModifier.PRIVATE)

        moduleClassNames.forEach { moduleClassName ->
            val moduleName = moduleClassName.simpleName.decapitalize()
            funSpecBuilder.addParameter(moduleName, moduleClassName)
        }

        this.primaryConstructor(funSpecBuilder.build())

        return this
    }

    private fun TypeSpec.Builder.setFactoryCompanionObjectSpec(
        moduleClassNames: List<ClassName>
    ): TypeSpec.Builder {
        val statement = "return $className(%s)"
            .format(List(moduleClassNames.size) { "%T()" }.joinToString(", "))
        val createFunSpec = FunSpec.builder("create")
            .returns(componentClassName)
            .addStatement(statement, *moduleClassNames.toTypedArray())
            .build()

        val companionSpec = TypeSpec.companionObjectBuilder()
            .addFunction(createFunSpec)
            .build()

        this.addType(companionSpec)

        return this
    }

    private fun TypeSpec.Builder.addProviderProperty(
        parameterData: ParameterData,
        initializer: String,
        initializerClassName: ClassName
    ): TypeSpec.Builder {
        this.addProperty(
            PropertySpec.builder(parameterData.name, parameterData.typeName, KModifier.PRIVATE)
                .initializer(initializer, initializerClassName)
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