package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Subcomponent

@Subcomponent(modules = [])
interface SampleSubcomponentWithSampleData {
    fun sampleData(): SampleData
}