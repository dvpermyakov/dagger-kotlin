package com.dvpermyakov.dagger.sample.di.modules

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.data.Database
import com.dvpermyakov.dagger.sample.domain.GlobalConfig
import com.dvpermyakov.dagger.sample.domain.ProfileModel

@Module(includes = [DatabaseModule::class])
class MainModule(private val uselessValue: Int) {

    @Provide
    fun provideConfig(profile: ProfileModel): GlobalConfig {
        return GlobalConfig(
            profileAvailable = profile.id.isNotBlank(),
            cardAvailable = true,
            transactionAvailable = true
        )
    }

    @Provide
    fun provideData(database: Database): ProfileModel {
        return ProfileModel("profileId")
    }
}