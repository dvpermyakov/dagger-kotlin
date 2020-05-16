package com.dvpermyakov.dagger.sample

import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val config: SampleConfig,
    private val sampleData: SampleData
) : SampleRepository {

    override fun getModelList(): List<SampleModel> {
        return listOf(
            SampleModel(config.value.data, config.value.data)
        )
    }

}