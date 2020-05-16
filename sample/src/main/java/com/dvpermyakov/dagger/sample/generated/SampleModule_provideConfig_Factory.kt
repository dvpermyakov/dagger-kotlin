package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.SampleConfig
import com.dvpermyakov.dagger.sample.SampleData
import com.dvpermyakov.dagger.sample.SampleModule
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleModule_provideConfig_Factory(
    private val module: SampleModule,
    private val dataProvider: Provider<SampleData>
) : Factory<SampleConfig> {

    override fun get(): SampleConfig {
        return module.provideConfig(dataProvider.get())
    }

}