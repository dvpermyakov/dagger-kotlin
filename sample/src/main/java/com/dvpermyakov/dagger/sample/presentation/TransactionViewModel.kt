package com.dvpermyakov.dagger.sample.presentation

import com.dvpermyakov.dagger.sample.domain.TransactionModel
import com.dvpermyakov.dagger.sample.interactors.CardInteractor
import com.dvpermyakov.dagger.sample.interactors.TransactionInteractor
import javax.inject.Inject

class TransactionViewModel @Inject constructor(
    private val cardInteractor: CardInteractor,
    private val transactionInteractor: TransactionInteractor
) {

    fun findAllTransactions(): List<TransactionModel> {
        return cardInteractor.getCards().flatMap { card ->
            transactionInteractor.getTransactions(card)
        }
    }

}