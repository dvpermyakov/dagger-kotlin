package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module

@Module
class SampleModuleWithManyMethods {

    fun getSampleInterface(): SampleInterface {
        return SampleInterfaceImpl()
    }

    fun getSampleWrapper(data: SampleData): SampleDataWrapper {
        return SampleDataWrapper(data)
    }

    fun getSample(): SampleData {
        return SampleData()
    }

    fun getSampleOther(): SampleDataOther {
        return SampleDataOther()
    }

}