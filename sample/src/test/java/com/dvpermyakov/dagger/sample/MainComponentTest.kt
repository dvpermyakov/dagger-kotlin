package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.data.Data
import com.dvpermyakov.dagger.sample.data.DatabaseConfig
import com.dvpermyakov.dagger.sample.data.NetworkConfig
import com.dvpermyakov.dagger.sample.di.component.KDaggerMainComponent
import com.dvpermyakov.dagger.sample.di.dependencies.DataDependencies
import com.dvpermyakov.dagger.sample.di.modules.MainModule
import com.dvpermyakov.dagger.sample.domain.TransactionModel
import com.dvpermyakov.dagger.sample.presentation.TransactionView
import org.junit.Assert
import org.junit.Test

class MainComponentTest {

    @Test
    fun getTransactionViewModel() {
        val component = KDaggerMainComponent.createNewInstance(
            networkConfig = NetworkConfig(),
            databaseConfig = DatabaseConfig(),
            dataDependencies = object : DataDependencies {
                override fun getData() = Data()
            },
            mainModule = MainModule(10)
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

    @Test
    fun injectTransactionView() {
        val component = KDaggerMainComponent.createNewInstance(
            networkConfig = NetworkConfig(),
            databaseConfig = DatabaseConfig(),
            dataDependencies = object : DataDependencies {
                override fun getData() = Data()
            },
            mainModule = MainModule(10)
        )
        val view = TransactionView()
        component.inject(view)

        val transactions = view.transactionViewModel.findAllTransactions()
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