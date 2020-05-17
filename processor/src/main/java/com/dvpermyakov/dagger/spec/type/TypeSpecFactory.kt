package com.dvpermyakov.dagger.spec.type

import com.squareup.kotlinpoet.TypeSpec

interface TypeSpecFactory {
    fun create(): TypeSpec
}