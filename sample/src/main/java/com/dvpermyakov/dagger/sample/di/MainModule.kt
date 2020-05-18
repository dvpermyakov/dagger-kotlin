package com.dvpermyakov.dagger.sample.di

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.domain.ProfileModel
import com.dvpermyakov.dagger.sample.domain.CardRepository
import com.dvpermyakov.dagger.sample.data.CardRepositoryImpl
import com.dvpermyakov.dagger.sample.data.TransactionRepositoryImpl
import com.dvpermyakov.dagger.sample.domain.TransactionRepository

@Module
class MainModule {

    @Provide
    fun provideConfig(profile: ProfileModel): GlobalConfig {
        return GlobalConfig(
            profileAvailable = profile.id.isNotBlank(),
            cardAvailable = true,
            transactionAvailable = true
        )
    }

    @Provide
    fun provideData(): ProfileModel {
        return ProfileModel("profileId")
    }

    @Provide
    fun provideSampleRepository(data: ProfileModel, config: GlobalConfig): CardRepository {
        return CardRepositoryImpl(config, data)
    }

    @Provide
    fun provideTransactionRepository(
        profile: ProfileModel,
        config: GlobalConfig
    ): TransactionRepository {
        return TransactionRepositoryImpl(config, profile)
    }
}