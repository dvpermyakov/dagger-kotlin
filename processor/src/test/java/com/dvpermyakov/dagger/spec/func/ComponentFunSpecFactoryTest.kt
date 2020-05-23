package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.sample.SampleComponent
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.google.testing.compile.CompilationRule
import io.mockk.every
import io.mockk.mockk
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
        val component = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnvironment,
            graph = graph,
            methodElement = component.getMethodElements().first()
        ).create()

        Assert.assertEquals(
            """
                |override fun getSampleData(): com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor = sampleDataWithInjectedConstructorProvider.get()
                |""".trimMargin(),
            funSpec.toString()
        )
    }

}