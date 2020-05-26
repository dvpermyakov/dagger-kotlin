package com.dvpermyakov.dagger.sample.di.subcomponent

import com.dvpermyakov.dagger.annotation.Subcomponent
import com.dvpermyakov.dagger.sample.di.modules.ParameterModule
import com.dvpermyakov.dagger.sample.domain.ParameterConfig
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Subcomponent(modules = [ParameterModule::class])
interface MainSubcomponent {
    fun getTransactionViewModel(): TransactionViewModel

    fun getParameterConfig(): ParameterConfig
}