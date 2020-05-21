package com.dvpermyakov.dagger.sample.di.modules

import com.dvpermyakov.dagger.annotation.Module
import com.dvpermyakov.dagger.annotation.Provide
import com.dvpermyakov.dagger.sample.data.Database
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provide
    fun getDatabase(): Database {
        return Database()
    }

}