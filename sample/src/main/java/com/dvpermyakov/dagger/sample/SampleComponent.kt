package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Component

@Component(module = SampleModule::class)
interface SampleComponent {

    fun inject(): SampleViewModel

}