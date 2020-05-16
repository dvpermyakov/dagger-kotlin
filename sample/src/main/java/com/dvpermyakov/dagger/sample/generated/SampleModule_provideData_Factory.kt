package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.SampleData
import com.dvpermyakov.dagger.sample.SampleModule
import com.dvpermyakov.dagger.utils.Factory

class SampleModule_provideData_Factory(
    private val module: SampleModule
) : Factory<SampleData> {

    override fun get(): SampleData {
        return module.provideData()
    }

}