package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.graph.SampleData

@Module
class SampleModule {
    fun getSampleData(): SampleData {
        return SampleData()
    }
}