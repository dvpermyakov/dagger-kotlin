package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.SampleModuleWithBinds
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class ModuleBindFunSpecFactoryTest {

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
    fun sampleModuleWithBinds() {
        val moduleElement = processingEnvironment.elementUtils.getTypeElement(SampleModuleWithBinds::class.java.name)
        val methodElement = moduleElement.getMethodElements().first()
        val typeSpec = ModuleBindFunSpecFactory(
            processingEnv = processingEnvironment,
            className = "SampleModule_SampleInterface_Binder",
            methodElement = methodElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class SampleModule_SampleInterface_Binder(
                |  private val factory: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleInterfaceImpl>
                |) : com.dvpermyakov.dagger.utils.Factory<com.dvpermyakov.dagger.sample.SampleInterface> {
                |  override fun get(): com.dvpermyakov.dagger.sample.SampleInterface = factory.get()
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

}