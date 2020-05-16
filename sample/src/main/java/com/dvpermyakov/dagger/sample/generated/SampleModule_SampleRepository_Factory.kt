package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.SampleConfig
import com.dvpermyakov.dagger.sample.SampleModule
import com.dvpermyakov.dagger.sample.SampleRepository
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleModule_SampleRepository_Factory(
    private val module: SampleModule,
    private val configProvider: Provider<SampleConfig>
) : Factory<SampleRepository> {

    override fun get(): SampleRepository {
        return module.provideSampleRepository(
            config = configProvider.get()
        )
    }

}