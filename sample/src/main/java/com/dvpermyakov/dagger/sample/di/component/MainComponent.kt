package com.dvpermyakov.dagger.sample.di.component

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.di.modules.DatabaseModule
import com.dvpermyakov.dagger.sample.di.modules.MainModule
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Component(modules = [MainModule::class, DatabaseModule::class])
interface MainComponent {

    fun getSampleViewModel(): TransactionViewModel

    fun getSampleConfig(): GlobalConfig

}