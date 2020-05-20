package com.dvpermyakov.dagger.sample.di.modules

import com.dvpermyakov.dagger.annotation.Binds
import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.sample.data.CardRepositoryImpl
import com.dvpermyakov.dagger.sample.data.TransactionRepositoryImpl
import com.dvpermyakov.dagger.sample.domain.CardRepository
import com.dvpermyakov.dagger.sample.domain.TransactionRepository

@Module
interface RepositoryModule {

    @Binds
    fun provideSampleRepository(impl: CardRepositoryImpl): CardRepository

    @Binds
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

}