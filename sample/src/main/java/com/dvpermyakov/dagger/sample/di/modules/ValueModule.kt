package com.dvpermyakov.dagger.sample.di.modules

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.domain.ValueModel

@Module
class ValueModule(private val value: Int) {

    @Provide
    fun getValue(): ValueModel {
        return ValueModel(value)
    }

}