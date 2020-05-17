package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.domain.TransactionRepository
import javax.inject.Inject

class TransactionInteractor @Inject constructor(
    private val repository: TransactionRepository
)