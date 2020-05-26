package com.dvpermyakov.dagger.sample

import javax.inject.Inject

class SampleDataWithInjectedConstructorAndParameters @Inject constructor(
    private val sampleData: SampleData,
    private val sampleDataOther: SampleDataOther
)