package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module


@Module
class SampleWithWrapperModule {
    fun getSampleDataWrapper(data: SampleData): SampleDataWrapper {
        return SampleDataWrapper(data)
    }
}