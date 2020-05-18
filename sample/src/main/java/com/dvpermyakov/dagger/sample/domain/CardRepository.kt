package com.dvpermyakov.dagger.sample.domain

interface CardRepository {
    fun getCards(): List<CardModel>
}