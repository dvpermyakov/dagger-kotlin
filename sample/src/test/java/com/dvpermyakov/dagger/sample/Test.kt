package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.di.component.KDaggerMainComponent
import com.dvpermyakov.dagger.sample.di.modules.MainModule
import com.dvpermyakov.dagger.sample.domain.TransactionModel
import org.junit.Assert
import org.junit.Test

class Test {

    @Test
    fun listOfTransactions() {
        val component = KDaggerMainComponent(MainModule())
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