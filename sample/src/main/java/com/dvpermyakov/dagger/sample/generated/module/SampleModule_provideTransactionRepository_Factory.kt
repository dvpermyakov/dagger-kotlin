package com.dvpermyakov.dagger.sample.generated.module

import com.dvpermyakov.dagger.sample.di.SampleModule
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.domain.TransactionRepository
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleModule_provideTransactionRepository_Factory(
    private val module: SampleModule,
    private val dataProvider: Provider<SampleData>,
    private val configProvider: Provider<SampleConfig>,
    private val sampleRepositoryProvider: Provider<SampleRepository>
) : Factory<TransactionRepository> {

    override fun get(): TransactionRepository {
        return module.provideTransactionRepository(
            dataProvider.get(),
            configProvider.get(),
            sampleRepositoryProvider.get()
        )
    }

}