package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.setParameterData(parameterData: ParameterData): FunSpec.Builder {
    addParameter(parameterData.name, parameterData.typeName)
    return this
}

fun FunSpec.Builder.setParameterDataList(parameterDataList: List<ParameterData>): FunSpec.Builder {
    parameterDataList.forEach { parameterData ->
        setParameterData(parameterData)
    }
    return this
}