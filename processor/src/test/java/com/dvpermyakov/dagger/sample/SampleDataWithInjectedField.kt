package com.dvpermyakov.dagger.sample

import javax.inject.Inject

class SampleDataWithInjectedField {

    @Inject
    lateinit var sampleData: SampleDataWithInjectedConstructor

}