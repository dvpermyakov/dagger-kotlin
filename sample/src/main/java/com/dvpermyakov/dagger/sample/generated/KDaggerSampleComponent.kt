package com.dvpermyakov.dagger.sample.generated

import SampleRepositoryImpl_Factory
import com.dvpermyakov.dagger.sample.data.SampleRepositoryImpl
import com.dvpermyakov.dagger.sample.di.SampleComponent
import com.dvpermyakov.dagger.sample.di.SampleModule
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel
import javax.inject.Provider

class KDaggerSampleComponent(
    sampleModule: SampleModule
) : SampleComponent {

    // from module
    private lateinit var provideRepositoryProvider: Provider<SampleRepository>
    private lateinit var provideSampleDataProvider: Provider<SampleData>
    private lateinit var provideConfigProvider: Provider<SampleConfig>

    // from inject constructor
    private lateinit var sampleViewModelProvider: Provider<SampleViewModel>
    private lateinit var sampleRepositoryImplProvider: Provider<SampleRepositoryImpl>

    init {
        provideRepositoryProvider = SampleModule_provideSampleRepository_Factory(sampleModule, provideConfigProvider, provideSampleDataProvider)
        provideSampleDataProvider = SampleModule_provideData_Factory(sampleModule)
        provideConfigProvider = SampleModule_provideConfig_Factory(sampleModule, provideSampleDataProvider)

        sampleViewModelProvider = SampleViewModel_Factory(provideRepositoryProvider)
        sampleRepositoryImplProvider = SampleRepositoryImpl_Factory(provideConfigProvider, provideSampleDataProvider)
    }

    override fun inject(): SampleViewModel {
        return sampleViewModelProvider.get()
    }

    override fun injectAnotherOne(): SampleConfig {
        return provideConfigProvider.get()
    }
}