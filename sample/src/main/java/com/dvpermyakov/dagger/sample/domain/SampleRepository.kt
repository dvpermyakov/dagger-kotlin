package com.dvpermyakov.dagger.sample.domain

interface SampleRepository {
    fun getModelList(): List<SampleModel>
}