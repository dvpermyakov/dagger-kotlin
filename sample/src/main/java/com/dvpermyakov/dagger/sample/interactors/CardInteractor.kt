package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.data.Database
import com.dvpermyakov.dagger.sample.domain.CardModel
import com.dvpermyakov.dagger.sample.domain.CardRepository
import javax.inject.Inject

class CardInteractor @Inject constructor(
    private val repository: CardRepository,
    private val database: Database
) {

    fun getCards(): List<CardModel> {
        return repository.getCards()
    }

}