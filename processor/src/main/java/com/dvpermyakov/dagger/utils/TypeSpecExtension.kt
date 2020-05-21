package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal fun TypeSpec.Builder.setProperties(
    parameters: List<ParameterData>
): TypeSpec.Builder {
    this.addProperties(
        parameters.map { parameter ->
            PropertySpec.builder(parameter.name, parameter.typeName).initializer(parameter.name).build()
        }
    )
    return this
}