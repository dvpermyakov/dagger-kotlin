package com.dvpermyakov.dagger.sample.data

import com.dvpermyakov.dagger.sample.domain.*
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val config: GlobalConfig,
    private val profile: ProfileModel
) : TransactionRepository {

    override fun getTransactions(card: CardModel): List<TransactionModel> {
        return if (config.transactionAvailable && profile.id.isNotBlank()) {
            when (card.id) {
                "1" -> {
                    listOf(
                        TransactionModel("1"),
                        TransactionModel("2")
                    )
                }
                "2" -> {
                    listOf(
                        TransactionModel("3"),
                        TransactionModel("4")
                    )
                }
                else -> emptyList()
            }
        } else emptyList()
    }

}