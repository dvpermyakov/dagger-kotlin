package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.domain.CardModel
import com.dvpermyakov.dagger.sample.domain.CardRepository
import javax.inject.Inject

class CardInteractor @Inject constructor(
    private val repository: CardRepository
) {

    fun getCards(): List<CardModel> {
        return repository.getCards()
    }

}