package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.SampleConfig
import com.dvpermyakov.dagger.sample.SampleModel
import com.dvpermyakov.dagger.sample.SampleRepository
import javax.inject.Inject

class SampleRepositoryImpl @Inject constructor(
    private val config: SampleConfig
) : SampleRepository {

    override fun getModelList(): List<SampleModel> {
        return listOf(
            SampleModel(config.value, config.value)
        )
    }

}