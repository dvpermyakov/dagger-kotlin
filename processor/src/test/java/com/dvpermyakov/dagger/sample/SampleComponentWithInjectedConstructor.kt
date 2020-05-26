package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Component

@Component(modules = [], dependencies = [])
interface SampleComponentWithInjectedConstructor {
    fun getSampleData(): SampleDataWithInjectedConstructor
}