package com.dvpermyakov.dagger.sample.di.component

import com.dvpermyakov.dagger.annotation.BindsInstance
import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig
import com.dvpermyakov.dagger.sample.di.dependencies.DataDependencies
import com.dvpermyakov.dagger.sample.di.dependencies.GlobalConfigDependencies
import com.dvpermyakov.dagger.sample.di.dependencies.ProfileDependencies
import com.dvpermyakov.dagger.sample.di.modules.DatabaseModule
import com.dvpermyakov.dagger.sample.di.modules.MainModule
import com.dvpermyakov.dagger.sample.di.modules.RepositoryModule
import com.dvpermyakov.dagger.sample.di.subcomponent.MainSubcomponent
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.presentation.TransactionView
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Component(
    modules = [MainModule::class, DatabaseModule::class, RepositoryModule::class],
    dependencies = [DataDependencies::class]
)
interface MainComponent : ProfileDependencies, GlobalConfigDependencies {

    fun getSubcomponent(): MainSubcomponent

    fun getTransactionViewModel(): TransactionViewModel

    fun getSampleConfig(): GlobalConfig

    fun inject(view: TransactionView)

    @Component.Factory
    interface Factory {
        fun createNewInstance(
            @BindsInstance nConfig: NetworkConfig,
            dataDependencies: DataDependencies,
            @BindsInstance dConfig: DatabaseConfig,
            mainModule: MainModule
        ): MainComponent
    }
}