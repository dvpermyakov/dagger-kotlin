package com.dvpermyakov.dagger.sample.di.dependencies

import com.dvpermyakov.dagger.sample.data.Data

interface DataDependencies {
    fun getData(): Data
}