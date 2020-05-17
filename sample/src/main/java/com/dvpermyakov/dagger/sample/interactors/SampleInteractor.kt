package com.dvpermyakov.dagger.sample.interactors

import com.dvpermyakov.dagger.sample.domain.SampleRepository
import javax.inject.Inject

class SampleInteractor @Inject constructor(
    val repository: SampleRepository
)