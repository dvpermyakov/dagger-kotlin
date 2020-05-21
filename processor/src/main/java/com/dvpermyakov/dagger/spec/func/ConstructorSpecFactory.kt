package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.utils.ParameterData
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

class ConstructorSpecFactory(
    private val parameters: List<ParameterData>
) : FunSpecFactory {
    override fun create(): FunSpec {
        val funSpecBuilder = FunSpec.constructorBuilder()

        parameters.forEach { parameter ->
            funSpecBuilder.addParameter(parameter.name, parameter.typeName, KModifier.PRIVATE)
        }

        return funSpecBuilder.build()
    }

}