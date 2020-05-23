package com.dvpermyakov.dagger.spec.property

import javax.inject.Provider

class SampleProvider : Provider<SampleData> {
    override fun get(): SampleData {
        return SampleData()
    }
}