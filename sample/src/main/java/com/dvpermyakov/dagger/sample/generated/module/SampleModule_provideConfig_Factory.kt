package com.dvpermyakov.dagger.sample.generated.module

import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.di.SampleModule
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