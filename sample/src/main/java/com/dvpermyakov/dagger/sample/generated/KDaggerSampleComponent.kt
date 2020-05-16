package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.*
import javax.inject.Provider

class KDaggerSampleComponent(
    sampleModule: SampleModule
) : SampleComponent {

    private var provideConfigProvider: Provider<SampleConfig> = SampleModule_SampleConfig_Factory(sampleModule)
    private var provideRepositoryProvider: Provider<SampleRepository> =
        SampleModule_SampleRepository_Factory(sampleModule, provideConfigProvider)

    override fun inject(): SampleViewModel {
        return SampleViewModel(provideRepositoryProvider.get())
    }
}