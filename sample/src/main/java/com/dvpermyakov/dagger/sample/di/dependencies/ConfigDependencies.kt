package com.dvpermyakov.dagger.sample.di.dependencies

import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig

interface ConfigDependencies {
    fun getNetworkConfig(): NetworkConfig
    fun getDatabaseConfig(): DatabaseConfig
}