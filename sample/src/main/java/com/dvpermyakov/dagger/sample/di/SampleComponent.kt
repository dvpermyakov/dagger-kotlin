package com.dvpermyakov.dagger.sample.di

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel

@Component(module = SampleModule::class)
interface SampleComponent {

    fun inject(): SampleViewModel

}