package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.SampleModule
import com.dvpermyakov.dagger.sample.SampleWithWrapperModule
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toElement
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class ModuleProviderFunSpecFactoryTest {

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
    fun sampleModule() {
        val moduleElement = SampleModule::class.java.toElement(processingEnv)
        val methodElement = moduleElement.getMethodElements().first()
        val typeSpec = ModuleProvideFunSpecFactory(
            processingEnv = processingEnv,
            className = "SampleModule_SampleData_Factory",
            methodElement = methodElement,
            moduleElement = moduleElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class SampleModule_SampleData_Factory(
                |  private val module: com.dvpermyakov.dagger.sample.SampleModule
                |) : com.dvpermyakov.dagger.utils.Factory<com.dvpermyakov.dagger.sample.SampleData> {
                |  override fun get(): com.dvpermyakov.dagger.sample.SampleData = module.getSampleData()
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }


    @Test
    fun sampleWithWrapperModule() {
        val moduleElement = SampleWithWrapperModule::class.java.toElement(processingEnv)
        val methodElement = moduleElement.getMethodElements().first()
        val typeSpec = ModuleProvideFunSpecFactory(
            processingEnv = processingEnv,
            className = "SampleModule_SampleData_Factory",
            methodElement = methodElement,
            moduleElement = moduleElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class SampleModule_SampleData_Factory(
                |  private val module: com.dvpermyakov.dagger.sample.SampleWithWrapperModule,
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData>
                |) : com.dvpermyakov.dagger.utils.Factory<com.dvpermyakov.dagger.sample.SampleDataWrapper> {
                |  override fun get(): com.dvpermyakov.dagger.sample.SampleDataWrapper = module.getSampleDataWrapper(sampleDataProvider.get())
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

}