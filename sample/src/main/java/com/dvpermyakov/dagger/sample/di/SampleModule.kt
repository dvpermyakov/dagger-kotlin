package com.dvpermyakov.dagger.sample.di

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.data.SampleRepositoryImpl

@Module
class SampleModule {

    @Provide
    fun provideConfig(data: SampleData): SampleConfig {
        return SampleConfig(data)
    }

    @Provide
    fun provideData(): SampleData {
        return SampleData(10)
    }

    @Provide
    fun provideSampleRepository(data: SampleData, config: SampleConfig): SampleRepository {
        return SampleRepositoryImpl(config, data)
    }
}