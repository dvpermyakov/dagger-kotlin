package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.utils.element.toClassName
import com.dvpermyakov.dagger.utils.spec.setParameterDataList
import com.dvpermyakov.dagger.utils.className.toParameterData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element

class FactoryCompanionObjectSpecFactory(
    private val processingEnv: ProcessingEnvironment,
    private val componentClassName: ClassName,
    private val className: String,
    private val emptyConstructorClassNames: List<ClassName>,
    private val otherClassNames: List<ClassName>,
    private val factoryInterfaceElement: Element?,
    private val factoryMethodElement: Element?
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val emptyConstructorStatement = List(emptyConstructorClassNames.size) { index ->
            val name = emptyConstructorClassNames[index].simpleName.decapitalize()
            "$name = %T()"
        }
        val otherStatement = otherClassNames.map { otherClassName ->
            val name = otherClassName.simpleName.decapitalize()
            "$name = $name"
        }

        val statementCode = (emptyConstructorStatement + otherStatement).joinToString(", ")
        val statement = "return $className(%s)".format(statementCode)

        val otherParameters = otherClassNames.map { otherClassName ->
            otherClassName.toParameterData()
        }
        val createFunSpec = if (factoryMethodElement != null) {
            FunSpec.builder(factoryMethodElement.simpleName.toString())
                .setParameterDataList(otherParameters)
                .addModifiers(KModifier.OVERRIDE)
                .returns(componentClassName)
                .addStatement(statement, *emptyConstructorClassNames.toTypedArray())
                .build()
        } else {
            FunSpec.builder("create")
                .setParameterDataList(otherParameters)
                .returns(componentClassName)
                .addStatement(statement, *emptyConstructorClassNames.toTypedArray())
                .build()
        }

        val companionSpecBuilder = TypeSpec.companionObjectBuilder().addFunction(createFunSpec)
        if (factoryInterfaceElement != null) {
            companionSpecBuilder.addSuperinterface(factoryInterfaceElement.toClassName(processingEnv))
        }

        return companionSpecBuilder.build()
    }


}