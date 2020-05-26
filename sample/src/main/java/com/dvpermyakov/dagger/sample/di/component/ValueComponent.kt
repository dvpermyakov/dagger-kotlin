package com.dvpermyakov.dagger.sample.di.component

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.di.modules.ValueModule
import com.dvpermyakov.dagger.sample.domain.ValueModel

@Component(modules = [ValueModule::class], dependencies = [])
interface ValueComponent {
    fun getValue(): ValueModel
}