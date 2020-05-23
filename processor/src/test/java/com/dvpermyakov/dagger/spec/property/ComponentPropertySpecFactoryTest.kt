package com.dvpermyakov.dagger.spec.property

import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toProviderParameterData
import org.junit.Assert
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

        Assert.assertEquals(
            providerSpec.toString(), """
                |private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.spec.property.SampleData> = com.dvpermyakov.dagger.spec.property.SampleProvider()
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

        Assert.assertEquals(
            providerSpec.toString(), """
                |private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.spec.property.SampleData> = com.dvpermyakov.dagger.utils.DoubleCheckProvider(com.dvpermyakov.dagger.spec.property.SampleProvider())
                """.trimMargin()
        )
    }

}