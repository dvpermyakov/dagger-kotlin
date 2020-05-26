package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.utils.ParameterData
import com.dvpermyakov.dagger.utils.spec.setParameterDataList
import com.squareup.kotlinpoet.FunSpec

class ConstructorSpecFactory(
    private val parameters: List<ParameterData>
) : FunSpecFactory {

    override fun create(): FunSpec {
        return FunSpec.constructorBuilder()
            .setParameterDataList(parameters)
            .build()
    }

}