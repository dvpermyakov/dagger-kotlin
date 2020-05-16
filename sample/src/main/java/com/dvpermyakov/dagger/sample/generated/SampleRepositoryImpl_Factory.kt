package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.data.SampleRepositoryImpl
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleRepositoryImpl_Factory(
    private val configProvider: Provider<SampleConfig>,
    private val dataProvider: Provider<SampleData>
) : Factory<SampleRepositoryImpl> {
    override fun get(): SampleRepositoryImpl {
        return SampleRepositoryImpl(configProvider.get(), dataProvider.get())
    }

}