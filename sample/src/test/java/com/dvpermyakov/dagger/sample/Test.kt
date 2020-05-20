package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig
import com.dvpermyakov.dagger.sample.di.component.KDaggerMainComponent
import com.dvpermyakov.dagger.sample.domain.TransactionModel
import org.junit.Assert
import org.junit.Test

class Test {

    @Test
    fun listOfTransactions() {
        val networkConfig = NetworkConfig()
        val databaseConfig = DatabaseConfig()
        val component = KDaggerMainComponent.createNewInstance(
            networkConfig = networkConfig,
            databaseConfig = databaseConfig
        )
        val viewModel = component.getSampleViewModel()
        val transactions = viewModel.findAllTransactions()
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