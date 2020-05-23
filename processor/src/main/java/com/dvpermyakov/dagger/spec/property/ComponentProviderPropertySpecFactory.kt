package com.dvpermyakov.dagger.spec.property

import com.dvpermyakov.dagger.utils.DoubleCheckProvider
import com.dvpermyakov.dagger.utils.ParameterData
import com.dvpermyakov.dagger.utils.className.toClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName

class ComponentProviderPropertySpecFactory(
    private val parameterData: ParameterData,
    private val initializer: String,
    private val initializerTypeName: TypeName,
    private val isSingleton: Boolean
) : PropertySpecFactory {

    override fun create(): PropertySpec {
        return if (isSingleton) {
            val doubleCheckClassName = DoubleCheckProvider::class.java.toClassName()
            PropertySpec.builder(parameterData.name, parameterData.typeName, KModifier.PRIVATE)
                .initializer("%T($initializer)", doubleCheckClassName, initializerTypeName)
                .build()
        } else {
            PropertySpec.builder(parameterData.name, parameterData.typeName, KModifier.PRIVATE)
                .initializer(initializer, initializerTypeName)
                .build()
        }
    }

}