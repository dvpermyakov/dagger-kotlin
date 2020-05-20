package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig
import com.dvpermyakov.dagger.sample.di.component.KDaggerDependencyComponent
import com.dvpermyakov.dagger.sample.di.dependencies.ConfigDependencies
import com.dvpermyakov.dagger.sample.domain.TransactionModel
import org.junit.Assert
import org.junit.Test

class DependencyComponentTest {

    @Test
    fun getTransactionViewModel() {
        val component = KDaggerDependencyComponent.createNewInstance(
            configDependencies = object : ConfigDependencies {
                override fun getNetworkConfig() = NetworkConfig()
                override fun getDatabaseConfig() = DatabaseConfig()
            }
        )
        val transactions = component.getTransactionViewModel().findAllTransactions()
        Assert.assertEquals(
            listOf(
                TransactionModel("1"),
                TransactionModel("2"),
                TransactionModel("3"),
                TransactionModel("4")
            ),
            transactions
        )
    }

}