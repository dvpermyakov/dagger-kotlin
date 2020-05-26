package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.graph.ComponentGraphTraversing
import com.dvpermyakov.dagger.sample.SampleComponentWithInjectedConstructor
import com.dvpermyakov.dagger.sample.SampleComponentWithInjectedField
import com.dvpermyakov.dagger.sample.SampleComponentWithSubcomponent
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toElement
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

    private lateinit var processingEnv: ProcessingEnvironment

    private val graph = mockk<ComponentGraphTraversing> {
        every { addInjectedClassNames(any()) } returns Unit
        every { addElementWithInjectedConstructor(any()) } returns Unit
        every { getClassNames() } returns emptyList()
        every { getProperties() } returns emptyList()
    }

    @Before
    fun setup() {
        processingEnv = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
    }

    @Test
    fun componentFunWithInjectedConstructor() {
        val componentElement = SampleComponentWithInjectedConstructor::class.java.toElement(processingEnv)
        val methodElement = componentElement.getMethodElements().first()
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnv,
            graph = graph,
            methodElement = methodElement
        ).create()

        val returnTypeElement = requireNotNull(methodElement.getReturnElement(processingEnv))
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
        val componentElement = SampleComponentWithInjectedField::class.java.toElement(processingEnv)
        val methodElement = componentElement.getMethodElements().first()
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnv,
            graph = graph,
            methodElement = methodElement
        ).create()

        val parameterElement = methodElement.getParameterElements(processingEnv).first()
        val fieldTypeElement = parameterElement.getFieldElements().first().asType().toElement(processingEnv)
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

    @Test
    fun componentFunWithSubcomponent() {
        val componentElement = SampleComponentWithSubcomponent::class.java.toElement(processingEnv)
        val methodElement = componentElement.getMethodElements().first()
        val funSpec = ComponentFunSpecFactory(
            processingEnv = processingEnv,
            graph = graph,
            methodElement = methodElement
        ).create()

        Assert.assertEquals(
            """
                |override fun getSubcomponent(): com.dvpermyakov.dagger.sample.SampleSubcomponent = object : com.dvpermyakov.dagger.sample.SampleSubcomponent {
                |}
                |""".trimMargin(),
            funSpec.toString()
        )
    }
}