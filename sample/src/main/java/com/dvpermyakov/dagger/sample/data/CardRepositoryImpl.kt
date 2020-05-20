package com.dvpermyakov.dagger.sample.data

import com.dvpermyakov.dagger.sample.domain.*
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val config: GlobalConfig,
    private val profile: ProfileModel
) : CardRepository {

    override fun getCards(): List<CardModel> {
        return if (config.cardAvailable && profile.id.isNotBlank()) {
            listOf(
                CardModel("1"),
                CardModel("2")
            )
        } else emptyList()
    }

}