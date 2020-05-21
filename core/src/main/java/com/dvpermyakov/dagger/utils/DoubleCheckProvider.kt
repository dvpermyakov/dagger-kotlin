package com.dvpermyakov.dagger.utils

import javax.inject.Provider

class DoubleCheckProvider<T>(
    private val provider: Provider<T>
) : Provider<T> {

    private var instance: T? = null

    override fun get(): T {
        return if (instance != null) {
            instance!!
        } else synchronized(this) {
            if (instance == null) {
                instance = provider.get()
                instance!!
            } else {
                instance!!
            }
        }
    }
}