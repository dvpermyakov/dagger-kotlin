package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.SampleRepository
import javax.inject.Inject

class SampleViewModel @Inject constructor(val repository: SampleRepository) {

}