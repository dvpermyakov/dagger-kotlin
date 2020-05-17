package com.dvpermyakov.dagger.sample.generated.constructor

import com.dvpermyakov.dagger.sample.domain.TransactionRepository
import com.dvpermyakov.dagger.sample.interactors.TransactionInteractor
import com.dvpermyakov.dagger.utils.Factory
import javax.inject.Provider

class TransactionInteractor_Factory(
    private val transactionRepositoryProvider: Provider<TransactionRepository>
) : Factory<TransactionInteractor> {
    override fun get(): TransactionInteractor {
        return TransactionInteractor(transactionRepositoryProvider.get())
    }

}