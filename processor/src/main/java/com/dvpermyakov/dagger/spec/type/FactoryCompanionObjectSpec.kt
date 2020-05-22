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

class FactoryCompanionObjectSpec(
    private val processingEnv: ProcessingEnvironment,
    private val componentClassName: ClassName,
    private val className: String,
    private val factoryInterfaceElement: Element?,
    private val factoryMethodElement: Element?,
    private val moduleClassNames: List<ClassName>,
    private val otherClassNames: List<ClassName>
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val otherStatement = otherClassNames.map { otherClassName ->
            val name = otherClassName.simpleName.decapitalize()
            "$name = $name"
        }
        val modulesStatement = List(moduleClassNames.size) { "%T()" }
        val statementCode = (modulesStatement + otherStatement).joinToString(", ")
        val statement = "return $className(%s)".format(statementCode)

        val otherParameters = otherClassNames.map { otherClassName ->
            otherClassName.toParameterData()
        }
        val createFunSpec = if (factoryMethodElement != null) {
            FunSpec.builder(factoryMethodElement.simpleName.toString())
                .setParameterDataList(otherParameters)
                .addModifiers(KModifier.OVERRIDE)
                .returns(componentClassName)
                .addStatement(statement, *moduleClassNames.toTypedArray())
                .build()
        } else {
            FunSpec.builder("create")
                .setParameterDataList(otherParameters)
                .returns(componentClassName)
                .addStatement(statement, *moduleClassNames.toTypedArray())
                .build()
        }

        val companionSpecBuilder = TypeSpec.companionObjectBuilder().addFunction(createFunSpec)
        if (factoryInterfaceElement != null) {
            companionSpecBuilder.addSuperinterface(factoryInterfaceElement.toClassName(processingEnv))
        }

        return companionSpecBuilder.build()
    }


}