package com.dvpermyakov.dagger.sample.domain

interface TransactionRepository {
    fun getTransactions(card: CardModel): List<TransactionModel>
}