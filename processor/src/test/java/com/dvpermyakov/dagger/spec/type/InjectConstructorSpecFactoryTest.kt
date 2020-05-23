package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor
import com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructorAndParameters
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.element.getConstructor
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ExecutableElement

class InjectConstructorSpecFactoryTest {

    @get:Rule
    val compilationRule = CompilationRule()

    private lateinit var processingEnvironment: ProcessingEnvironment

    @Before
    fun setup() {
        processingEnvironment = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
    }

    @Test
    fun injectConstructor() {
        val sampleDataElement =
            processingEnvironment.elementUtils.getTypeElement(SampleDataWithInjectedConstructor::class.java.name)
        val constructorElement = sampleDataElement.getConstructor() as ExecutableElement
        val typeSpec = InjectConstructorSpecFactory(
            processingEnv = processingEnvironment,
            className = "SampleData_Factory",
            constructorElement = constructorElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class SampleData_Factory : com.dvpermyakov.dagger.utils.Factory<com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor> {
                |  override fun get(): com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor = SampleDataWithInjectedConstructor()
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun injectedConstructorWithParameters() {
        val sampleDataElement = processingEnvironment.elementUtils.getTypeElement(
            SampleDataWithInjectedConstructorAndParameters::class.java.name
        )
        val constructorElement = sampleDataElement.getConstructor() as ExecutableElement
        val typeSpec = InjectConstructorSpecFactory(
            processingEnv = processingEnvironment,
            className = "SampleData_Factory",
            constructorElement = constructorElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class SampleData_Factory(
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData>,
                |  private val sampleDataOtherProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleDataOther>
                |) : com.dvpermyakov.dagger.utils.Factory<com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructorAndParameters> {
                |  override fun get(): com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructorAndParameters = SampleDataWithInjectedConstructorAndParameters(sampleDataProvider.get(), sampleDataOtherProvider.get())
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }
}