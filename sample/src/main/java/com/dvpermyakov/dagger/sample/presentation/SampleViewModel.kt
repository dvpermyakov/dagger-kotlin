package com.dvpermyakov.dagger.sample.presentation

import com.dvpermyakov.dagger.sample.interactors.SampleInteractor
import javax.inject.Inject

class SampleViewModel @Inject constructor(val interactor: SampleInteractor)