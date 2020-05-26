package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module

@Module(includes = [SampleModule::class])
interface SampleModuleWithIncludes {

    fun getWrapper(sampleData: SampleData): SampleDataWrapper {
        return SampleDataWrapper(sampleData)
    }
}