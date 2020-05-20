package com.dvpermyakov.dagger.sample.presentation

import com.dvpermyakov.dagger.sample.interactors.AccountInteractor
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val accountInteractor: AccountInteractor
)