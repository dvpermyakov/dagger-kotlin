package com.dvpermyakov.dagger.sample.generated.constructor

import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.interactors.SampleInteractor
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleInteractor_Factory(
    private val repositoryProvider: Provider<SampleRepository>
) : Factory<SampleInteractor> {
    override fun get(): SampleInteractor {
        return SampleInteractor(repositoryProvider.get())
    }

}