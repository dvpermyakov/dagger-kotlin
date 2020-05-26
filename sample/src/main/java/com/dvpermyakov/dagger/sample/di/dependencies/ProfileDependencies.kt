package com.dvpermyakov.dagger.sample.di.dependencies

import com.dvpermyakov.dagger.sample.domain.ProfileModel

interface ProfileDependencies {
    fun getProfile(): ProfileModel
}