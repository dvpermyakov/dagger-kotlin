package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.*
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