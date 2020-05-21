package com.dvpermyakov.dagger.spec.typeimport com.dvpermyakov.dagger.spec.func.ConstructorSpecFactoryimport com.dvpermyakov.dagger.spec.func.OverrideGetFunSpecFactoryimport com.dvpermyakov.dagger.utils.*import com.squareup.kotlinpoet.TypeSpecimport javax.annotation.processing.Generatedimport javax.annotation.processing.ProcessingEnvironmentimport javax.lang.model.element.ExecutableElementclass InjectConstructorSpecFactory(    private val processingEnv: ProcessingEnvironment,    private val className: String,    private val constructorElement: ExecutableElement) : TypeSpecFactory {    override fun create(): TypeSpec {        val classElement = constructorElement.enclosingElement        val classClassName = classElement.toClassName(processingEnv)        val parameters = constructorElement            .getParametersClassName(processingEnv)            .map { parameterClassName -> parameterClassName.toProviderParameterData() }        val getCodeStatement = "return ${classClassName.simpleName}(" +            "${parameters.joinToString(", ") { parameter ->                "${parameter.name}.get()"            }})"        return TypeSpec.classBuilder(className)            .addAnnotation(Generated::class.java)            .primaryConstructor(ConstructorSpecFactory(parameters).create())            .setProperties(parameters)            .addSuperinterface(classClassName.toFactoryClassName())            .addFunction(                OverrideGetFunSpecFactory(                    returnTypeName = classClassName,                    statement = getCodeStatement                ).create()            )            .build()    }}