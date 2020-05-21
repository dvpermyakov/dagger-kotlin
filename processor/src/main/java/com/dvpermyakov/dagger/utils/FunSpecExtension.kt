package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.setParameterData(parameterData: ParameterData): FunSpec.Builder {
    addParameter(parameterData.name, parameterData.typeName)
    return this
}