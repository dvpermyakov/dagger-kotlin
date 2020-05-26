package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide

@Module
class SampleModule {

    @Provide
    fun getSampleData(): SampleData {
        return SampleData()
    }
}