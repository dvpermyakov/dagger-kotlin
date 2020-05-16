package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.di.SampleModule
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleModule_provideSampleRepository_Factory(
    private val module: SampleModule,
    private val configProvider: Provider<SampleConfig>,
    private val dataProvider: Provider<SampleData>
) : Factory<SampleRepository> {

    override fun get(): SampleRepository {
        return module.provideSampleRepository(
            config = configProvider.get(),
            data = dataProvider.get()
        )
    }

}