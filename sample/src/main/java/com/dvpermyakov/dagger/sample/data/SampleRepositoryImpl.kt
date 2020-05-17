package com.dvpermyakov.dagger.sample.data

import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleModel
import com.dvpermyakov.dagger.sample.domain.SampleRepository

class SampleRepositoryImpl(
    private val config: SampleConfig,
    private val sampleData: SampleData
) : SampleRepository {

    override fun getModelList(): List<SampleModel> {
        return listOf(
            SampleModel(config.value.data, config.value.data)
        )
    }

}