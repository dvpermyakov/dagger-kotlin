package com.dvpermyakov.dagger.utils.className

import com.dvpermyakov.dagger.utils.Factory
import com.dvpermyakov.dagger.utils.ParameterData
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.inject.Provider

internal fun ClassName.toProviderParameterData(): ParameterData {
    val providerClassName = Provider::class.java.toClassName()
    val parameterizedProviderClassName = providerClassName.parameterizedBy(this)

    return ParameterData(
        typeName = parameterizedProviderClassName,
        name = toProviderName()
    )
}

internal fun ClassName.toParameterData(): ParameterData {
    return ParameterData(
        typeName = this,
        name = simpleName.decapitalize()
    )
}

internal fun ClassName.toFactoryClassName(): ParameterizedTypeName {
    val factoryClassName = Factory::class.java.toClassName()
    return factoryClassName.parameterizedBy(this)
}

internal fun ClassName.toProviderName(): String {
    return simpleName.decapitalize() + "Provider"
}