package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.inject.Provider

internal fun ClassName.toProviderParameterData(): ParameterData {
    val providerClassName = Provider::class.java.toClassName()
    val parameterizedProviderClassName = providerClassName.parameterizedBy(this)

    return ParameterData(
        typeName = parameterizedProviderClassName,
        name = simpleName.decapitalize() + "Provider"
    )
}

internal fun ClassName.toFactoryClassName(): ParameterizedTypeName {
    val factoryClassName = Factory::class.java.toClassName()
    return factoryClassName.parameterizedBy(this)
}