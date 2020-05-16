package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.SampleModel

interface SampleRepository {
    fun getModelList(): List<SampleModel>
}