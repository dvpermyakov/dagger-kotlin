package com.dvpermyakov.dagger.sample.di.component

import com.dvpermyakov.dagger.annotation.Component
import com.dvpermyakov.dagger.sample.di.dependencies.ConfigDependencies
import com.dvpermyakov.dagger.sample.di.modules.DatabaseModule
import com.dvpermyakov.dagger.sample.di.modules.MainModule
import com.dvpermyakov.dagger.sample.di.modules.RepositoryModule
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.presentation.TransactionView
import com.dvpermyakov.dagger.sample.presentation.TransactionViewModel

@Component(
    modules = [MainModule::class, DatabaseModule::class, RepositoryModule::class],
    dependencies = [ConfigDependencies::class]
)
interface DependencyComponent {

    fun getTransactionViewModel(): TransactionViewModel

    fun getSampleConfig(): GlobalConfig

    fun inject(view: TransactionView)

    @Component.Factory
    interface Factory {
        fun createNewInstance(
            dependencies: ConfigDependencies
        ): DependencyComponent
    }
}