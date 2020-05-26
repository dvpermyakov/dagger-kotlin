package com.dvpermyakov.dagger.sample.di.modules

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.domain.ParameterConfig

@Module
class ParameterModule {

    @Provide
    fun getParameterConfig(): ParameterConfig {
        return ParameterConfig()
    }
}