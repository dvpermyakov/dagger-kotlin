package com.dvpermyakov.dagger.sample.presentation

import com.dvpermyakov.dagger.sample.domain.SampleRepository
import javax.inject.Inject

class SampleViewModel @Inject constructor(val repository: SampleRepository)