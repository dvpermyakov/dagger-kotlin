package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.di.SampleComponent
import com.dvpermyakov.dagger.sample.di.SampleModule
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.domain.TransactionRepository
import com.dvpermyakov.dagger.sample.generated.constructor.SampleInteractor_Factory
import com.dvpermyakov.dagger.sample.generated.constructor.SampleViewModel_Factory
import com.dvpermyakov.dagger.sample.generated.constructor.TransactionInteractor_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideConfig_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideData_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideSampleRepository_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideTransactionRepository_Factory
import com.dvpermyakov.dagger.sample.interactors.SampleInteractor
import com.dvpermyakov.dagger.sample.interactors.TransactionInteractor
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel
import javax.inject.Provider

class KDaggerSampleComponent(
    sampleModule: SampleModule
) : SampleComponent {

    // from sampleModule
    private lateinit var sampleRepositoryProvider: Provider<SampleRepository>
    private lateinit var sampleDataProvider: Provider<SampleData>
    private lateinit var sampleConfigProvider: Provider<SampleConfig>
    private lateinit var transactionRepositoryProvider: Provider<TransactionRepository>

    // from inject constructor
    private lateinit var sampleViewModelProvider: Provider<SampleViewModel>
    private lateinit var sampleInteractorProvider: Provider<SampleInteractor>
    private lateinit var transactionInteractorProvider: Provider<TransactionInteractor>

    init {
        sampleDataProvider = SampleModule_provideData_Factory(sampleModule)
        sampleConfigProvider = SampleModule_provideConfig_Factory(sampleModule, sampleDataProvider)
        sampleRepositoryProvider = SampleModule_provideSampleRepository_Factory(sampleModule, sampleConfigProvider, sampleDataProvider)
        transactionRepositoryProvider = SampleModule_provideTransactionRepository_Factory(sampleModule, sampleDataProvider, sampleConfigProvider, sampleRepositoryProvider)

        sampleInteractorProvider = SampleInteractor_Factory(sampleRepositoryProvider)
        transactionInteractorProvider = TransactionInteractor_Factory(transactionRepositoryProvider)
        sampleViewModelProvider = SampleViewModel_Factory(sampleInteractorProvider, transactionInteractorProvider)
    }

    override fun getSampleConfig(): SampleConfig {
        return sampleConfigProvider.get()
    }

    override fun getSampleViewModel(): SampleViewModel {
        return sampleViewModelProvider.get()
    }
}