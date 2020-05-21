package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.utils.toProviderName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

class ComponentEmptyFunSpecFactory(
    private val methodName: String,
    private val returnTypeClassName: ClassName
) : FunSpecFactory {
    override fun create(): FunSpec {
        return FunSpec.builder(methodName)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                "return ${returnTypeClassName.toProviderName()}.get()"
            )
            .returns(returnTypeClassName)
            .build()
    }

}