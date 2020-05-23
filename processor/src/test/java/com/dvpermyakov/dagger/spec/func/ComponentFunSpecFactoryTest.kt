package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.sample.SampleComponentWithInjectedConstructor
import com.dvpermyakov.dagger.sample.SampleComponentWithInjectedField
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.element.*
import com.google.testing.compile.CompilationRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class ComponentFunSpecFactoryTest {

    @get:Rule
    val compilationRule = CompilationRule()

    private lateinit var processingEnvironment: ProcessingEnvironment

    private val graph = mockk<ComponentGraphTraversing> {
        every { addElementWithInjectedConstructor(any()) } returns Unit
    }

    @Before
    fun setup() {
        processingEnvironment = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
    }

    @Test
    fun componentFunWithInjectedConstructor() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponentWithInjectedConstructor::class.java.name)
        val methodElement = componentElement.getMethodElements().first()
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnvironment,
            graph = graph,
            methodElement = methodElement
        ).create()

        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnvironment))
        verify { graph.addElementWithInjectedConstructor(returnTypeElement) }

        Assert.assertEquals(
            """
                |override fun getSampleData(): com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor = sampleDataWithInjectedConstructorProvider.get()
                |""".trimMargin(),
            funSpec.toString()
        )
    }

    @Test
    fun componentFunWithInjectedField() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponentWithInjectedField::class.java.name)
        val methodElement = componentElement.getMethodElements().first()
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnvironment,
            graph = graph,
            methodElement = methodElement
        ).create()

        val parameterElement = methodElement.getParameterElements(processingEnvironment).first()
        val fieldTypeElement = parameterElement.getFieldElements().first().asType().toElement(processingEnvironment)
        verify { graph.addElementWithInjectedConstructor(fieldTypeElement) }

        Assert.assertEquals(
            """
                |override fun inject(arg0: com.dvpermyakov.dagger.sample.SampleDataWithInjectedField) {
                |  arg0.sampleData = sampleDataWithInjectedConstructorProvider.get()
                |}
                |""".trimMargin(),
            funSpec.toString()
        )
    }

}