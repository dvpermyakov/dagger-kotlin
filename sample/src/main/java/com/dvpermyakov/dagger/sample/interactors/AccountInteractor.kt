package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig
import javax.inject.Inject

class AccountInteractor @Inject constructor(
    private val databaseConfig: DatabaseConfig,
    private val networkConfig: NetworkConfig
)