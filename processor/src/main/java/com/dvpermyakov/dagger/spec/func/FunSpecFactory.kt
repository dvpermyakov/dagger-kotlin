package com.dvpermyakov.dagger.spec.func

import com.squareup.kotlinpoet.FunSpec

interface FunSpecFactory {
    fun create(): FunSpec
}