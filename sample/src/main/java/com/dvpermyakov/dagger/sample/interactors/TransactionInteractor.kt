package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.data.Database
import com.dvpermyakov.dagger.sample.domain.CardModel
import com.dvpermyakov.dagger.sample.domain.TransactionModel
import com.dvpermyakov.dagger.sample.domain.TransactionRepository
import javax.inject.Inject

class TransactionInteractor @Inject constructor(
    private val repository: TransactionRepository,
    private val database: Database
) {

    fun getTransactions(card: CardModel): List<TransactionModel> {
        return repository.getTransactions(card)
    }

}