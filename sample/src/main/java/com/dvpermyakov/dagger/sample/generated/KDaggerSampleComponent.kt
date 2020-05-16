package com.dvpermyakov.dagger.sample.generated

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

    private val provideSampleDataProvider: Provider<SampleData> = SampleModule_provideData_Factory(sampleModule)
    private val provideConfigProvider: Provider<SampleConfig> =
        SampleModule_provideConfig_Factory(sampleModule, provideSampleDataProvider)
    private val provideRepositoryProvider: Provider<SampleRepository> =
        SampleModule_provideSampleRepository_Factory(sampleModule, provideConfigProvider, provideSampleDataProvider)

    override fun inject(): SampleViewModel {
        return SampleViewModel(provideRepositoryProvider.get())
    }
}