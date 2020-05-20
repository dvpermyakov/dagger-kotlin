package com.dvpermyakov.dagger.utils

import javax.inject.Provider

class ContainerProvider<T>(
    private val value: T
) : Provider<T> {

    override fun get(): T {
        return value
    }
}