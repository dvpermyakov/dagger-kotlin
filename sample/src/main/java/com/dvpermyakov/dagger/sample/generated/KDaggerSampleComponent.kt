package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.di.SampleComponent
import com.dvpermyakov.dagger.sample.di.SampleModule
import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.generated.constructor.SampleViewModel_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideConfig_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideData_Factory
import com.dvpermyakov.dagger.sample.generated.module.SampleModule_provideSampleRepository_Factory
import com.dvpermyakov.dagger.sample.interactors.SampleInteractor
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel
import javax.inject.Provider

class KDaggerSampleComponent(
    sampleModule: SampleModule
) : SampleComponent {

    // from sampleModule
    private lateinit var sampleRepositoryProvider: Provider<SampleRepository>
    private lateinit var sampleDataProvider: Provider<SampleData>
    private lateinit var sampleConfigProvider: Provider<SampleConfig>

    // from inject constructor
    private lateinit var sampleViewModelProvider: Provider<SampleViewModel>
    private lateinit var sampleInteractorProvider: Provider<SampleInteractor>

    init {
        sampleRepositoryProvider = SampleModule_provideSampleRepository_Factory(sampleModule, sampleConfigProvider, sampleDataProvider)
        sampleDataProvider = SampleModule_provideData_Factory(sampleModule)
        sampleConfigProvider = SampleModule_provideConfig_Factory(sampleModule, sampleDataProvider)

        sampleViewModelProvider = SampleViewModel_Factory(sampleInteractorProvider)
    }

    override fun getSampleConfig(): SampleConfig {
        return sampleConfigProvider.get()
    }

    override fun getSampleViewModel(): SampleViewModel {
        return sampleViewModelProvider.get()
    }
}