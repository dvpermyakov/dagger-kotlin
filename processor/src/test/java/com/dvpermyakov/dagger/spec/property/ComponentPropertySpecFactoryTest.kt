package com.dvpermyakov.dagger.spec.property

import com.dvpermyakov.dagger.sample.SampleData
import com.dvpermyakov.dagger.sample.SampleProvider
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toProviderParameterData
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ComponentPropertySpecFactoryTest {

    @Test
    fun sampleProperty() {
        val sampleClassName = SampleData::class.java.toClassName()
        val sampleProviderClassName = SampleProvider::class.java.toClassName()
        val providerSpec = ComponentPropertySpecFactory(
            parameterData = sampleClassName.toProviderParameterData(),
            initializer = "%T()",
            initializerTypeName = sampleProviderClassName,
            isSingleton = false
        ).create()

        assertThat(providerSpec.toString()).isEqualTo(
            """
                |private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.sample.SampleProvider()
                |""".trimMargin()
        )
    }


    @Test
    fun sampleSingletonProperty() {
        val sampleClassName = SampleData::class.java.toClassName()
        val sampleProviderClassName = SampleProvider::class.java.toClassName()
        val providerSpec = ComponentPropertySpecFactory(
            parameterData = sampleClassName.toProviderParameterData(),
            initializer = "%T()",
            initializerTypeName = sampleProviderClassName,
            isSingleton = true
        ).create()

        assertThat(providerSpec.toString()).isEqualTo(
            """
                |private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.utils.DoubleCheckProvider(com.dvpermyakov.dagger.sample.SampleProvider())
                |""".trimMargin()
        )
    }

}