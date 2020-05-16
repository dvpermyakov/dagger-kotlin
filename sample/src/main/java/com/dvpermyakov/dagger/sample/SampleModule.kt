package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide

@Module
class SampleModule {

    @Provide
    fun provideConfig(): SampleConfig {
        return SampleConfig(10)
    }

    @Provide
    fun provideSampleRepository(config: SampleConfig): SampleRepository {
        return SampleRepositoryImpl(config)
    }


}