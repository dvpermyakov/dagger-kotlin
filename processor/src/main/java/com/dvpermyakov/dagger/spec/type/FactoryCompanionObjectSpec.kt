package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.utils.element.toClassName
import com.dvpermyakov.dagger.utils.element.toClassNames
import com.dvpermyakov.dagger.utils.setParameterDataList
import com.dvpermyakov.dagger.utils.toParameterData
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
    private val dependencyElements: List<Element>,
    private val bindsInstanceClassNames: List<ClassName>
) : TypeSpecFactory {

    override fun create(): TypeSpec {
        val dependencyClassNames = dependencyElements.toClassNames(processingEnv)

        val dependenciesStatement = dependencyClassNames.map { it.simpleName.decapitalize() }
        val bindsStatement = bindsInstanceClassNames.map { it.simpleName.decapitalize() }
        val modulesStatement = List(moduleClassNames.size) { "%T()" }
        val statementCode = (modulesStatement + dependenciesStatement + bindsStatement).joinToString(", ")
        val statement = "return $className(%s)".format(statementCode)

        val bindsParameters = bindsInstanceClassNames.map { bindsInstanceClassName ->
            bindsInstanceClassName.toParameterData()
        }
        val dependencyParameters = dependencyClassNames.map { dependencyClassName ->
            dependencyClassName.toParameterData()
        }
        val createFunSpec = if (factoryMethodElement != null) {
            FunSpec.builder(factoryMethodElement.simpleName.toString())
                .setParameterDataList(dependencyParameters)
                .setParameterDataList(bindsParameters)
                .addModifiers(KModifier.OVERRIDE)
                .returns(componentClassName)
                .addStatement(statement, *moduleClassNames.toTypedArray())
                .build()
        } else {
            FunSpec.builder("create")
                .setParameterDataList(dependencyParameters)
                .setParameterDataList(bindsParameters)
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