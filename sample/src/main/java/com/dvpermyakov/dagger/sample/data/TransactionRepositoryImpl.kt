package com.dvpermyakov.dagger.sample.data

import com.dvpermyakov.dagger.sample.domain.SampleConfig
import com.dvpermyakov.dagger.sample.domain.SampleData
import com.dvpermyakov.dagger.sample.domain.SampleRepository
import com.dvpermyakov.dagger.sample.domain.TransactionRepository

class TransactionRepositoryImpl(
    private val config: SampleConfig,
    private val sampleData: SampleData,
    private val sampleRepository: SampleRepository
) : TransactionRepository