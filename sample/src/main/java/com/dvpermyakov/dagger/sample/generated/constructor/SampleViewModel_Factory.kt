package com.dvpermyakov.dagger.sample.generated.constructor

import com.dvpermyakov.dagger.sample.interactors.SampleInteractor
import com.dvpermyakov.dagger.sample.interactors.TransactionInteractor
import com.dvpermyakov.dagger.sample.presentation.SampleViewModel
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class SampleViewModel_Factory(
    private val sampleInteractorProvider: Provider<SampleInteractor>,
    private val transactionInteractorProvider: Provider<TransactionInteractor>
) : Factory<SampleViewModel> {
    override fun get(): SampleViewModel {
        return SampleViewModel(sampleInteractorProvider.get(), transactionInteractorProvider.get())
    }

}