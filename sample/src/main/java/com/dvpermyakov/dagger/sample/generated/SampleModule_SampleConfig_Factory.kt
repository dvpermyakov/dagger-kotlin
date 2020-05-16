package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.SampleConfig
import com.dvpermyakov.dagger.sample.SampleModule
import com.dvpermyakov.dagger.utils.Factory

class SampleModule_SampleConfig_Factory(
    private val module: SampleModule
) : Factory<SampleConfig> {

    override fun get(): SampleConfig {
        return module.provideConfig()
    }

}