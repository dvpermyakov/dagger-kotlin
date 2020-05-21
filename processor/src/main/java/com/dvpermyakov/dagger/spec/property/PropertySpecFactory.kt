package com.dvpermyakov.dagger.spec.property

import com.squareup.kotlinpoet.PropertySpec

interface PropertySpecFactory {
    fun create(): PropertySpec
}