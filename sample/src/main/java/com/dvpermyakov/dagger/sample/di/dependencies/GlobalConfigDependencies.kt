package com.dvpermyakov.dagger.sample.di.dependencies

import com.dvpermyakov.dagger.sample.domain.GlobalConfig

interface GlobalConfigDependencies {
    fun getGlobalConfig(): GlobalConfig
}