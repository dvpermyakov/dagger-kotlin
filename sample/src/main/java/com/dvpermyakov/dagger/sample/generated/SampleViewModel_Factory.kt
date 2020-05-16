package com.dvpermyakov.dagger.sample.generated

import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleViewModel_Factory(
    private val repositoryProvider: Provider<SampleRepository>
) : Factory<SampleViewModel> {
    override fun get(): SampleViewModel {
        return SampleViewModel(repositoryProvider.get())
    }

}