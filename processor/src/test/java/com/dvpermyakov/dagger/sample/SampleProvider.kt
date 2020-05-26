package com.dvpermyakov.dagger.sample

import javax.inject.Provider

class SampleProvider : Provider<SampleData> {
    override fun get(): SampleData {
        return SampleData()
    }
}