package com.dvpermyakov.dagger.sample.di

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Component(modules = [MainModule::class])
interface MainComponent {

    fun getSampleViewModel(): TransactionViewModel

    fun getSampleConfig(): GlobalConfig

}