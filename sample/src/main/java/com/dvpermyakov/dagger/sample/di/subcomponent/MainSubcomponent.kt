package com.dvpermyakov.dagger.sample.di.subcomponent

import com.dvpermyakov.dagger.annotation.Subcomponent
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Subcomponent(modules = [])
interface MainSubcomponent {
    fun getTransactionViewModel(): TransactionViewModel
}