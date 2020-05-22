package com.dvpermyakov.dagger.utils.spec

import com.dvpermyakov.dagger.utils.ParameterData
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal fun TypeSpec.Builder.setProperties(
    parameters: List<ParameterData>
): TypeSpec.Builder {
    this.addProperties(
        parameters.map { parameter ->
            PropertySpec.builder(parameter.name, parameter.typeName, KModifier.PRIVATE)
                .initializer(parameter.name)
                .build()
        }
    )
    return this
}