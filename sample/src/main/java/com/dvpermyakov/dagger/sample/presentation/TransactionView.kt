package com.dvpermyakov.dagger.sample.presentation

import javax.inject.Inject

class TransactionView {

    @Inject
    lateinit var transactionViewModel: TransactionViewModel

    @Inject
    lateinit var accountViewModel: AccountViewModel

}