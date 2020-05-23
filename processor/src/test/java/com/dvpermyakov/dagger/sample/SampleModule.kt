package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module

@Module
class SampleModule {
    fun getSampleData(): SampleData {
        return SampleData()
    }
}