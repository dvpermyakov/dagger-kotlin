package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.SampleSubcomponent
import com.dvpermyakov.dagger.sample.SampleSubcomponentWithModule
import com.dvpermyakov.dagger.sample.SampleSubcomponentWithSampleData
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toElement
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class SubComponentSpecFactoryTest {

    @get:Rule
    val compilationRule = CompilationRule()

    private lateinit var processingEnv: ProcessingEnvironment

    @Before
    fun setup() {
        processingEnv = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
    }

    @Test
    fun sampleSubcomponent() {
        val sampleSubcomponentElement = SampleSubcomponent::class.java.toElement(processingEnv)
        val typeSpec = SubComponentSpecFactory(
            processingEnv = processingEnv,
            subcomponentElement = sampleSubcomponentElement,
            componentInjectedClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |object : com.dvpermyakov.dagger.sample.SampleSubcomponent {
                |}""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleSubcomponentWithSampleData() {
        val sampleSubcomponentElement = SampleSubcomponentWithSampleData::class.java.toElement(processingEnv)
        val typeSpec = SubComponentSpecFactory(
            processingEnv = processingEnv,
            subcomponentElement = sampleSubcomponentElement,
            componentInjectedClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |object : com.dvpermyakov.dagger.sample.SampleSubcomponentWithSampleData {
                |  override fun sampleData(): com.dvpermyakov.dagger.sample.SampleData = sampleDataProvider.get()
                |}""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleSubcomponentWithModule() {
        val sampleSubcomponentElement = SampleSubcomponentWithModule::class.java.toElement(processingEnv)
        val typeSpec = SubComponentSpecFactory(
            processingEnv = processingEnv,
            subcomponentElement = sampleSubcomponentElement,
            componentInjectedClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |object : com.dvpermyakov.dagger.sample.SampleSubcomponentWithModule {
                |  private val sampleModule: com.dvpermyakov.dagger.sample.SampleModule = com.dvpermyakov.dagger.sample.SampleModule()
                |
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.sample.SampleModule_SampleData_Factory(sampleModule)
                |}""".trimMargin(),
            typeSpec.toString()
        )
    }
}