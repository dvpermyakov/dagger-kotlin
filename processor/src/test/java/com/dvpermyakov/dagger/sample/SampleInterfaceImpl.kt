package com.dvpermyakov.dagger.sample

class SampleInterfaceImpl : SampleInterface {

    override fun getData(): SampleData {
        return SampleData()
    }

}