package com.dvpermyakov.dagger.spec.func

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName

class OverrideGetFunSpecFactory(
    private val returnTypeName: TypeName,
    private val statement: String
) : FunSpecFactory {

    override fun create(): FunSpec {
        return FunSpec.builder("get")
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(statement)
            .returns(returnTypeName)
            .build()
    }
}