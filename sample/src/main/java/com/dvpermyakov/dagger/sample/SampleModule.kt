package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide

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