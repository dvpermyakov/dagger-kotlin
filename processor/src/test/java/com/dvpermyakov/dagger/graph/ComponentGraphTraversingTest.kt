package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.sample.*
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toClassName
import com.google.testing.compile.CompilationRule
import com.squareup.kotlinpoet.ClassName
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class ComponentGraphTraversingTest {

    @get:Rule
    val compilationRule = CompilationRule()

    private lateinit var processingEnvironment: ProcessingEnvironment
    private lateinit var graph: ComponentGraphTraversing

    @Before
    fun setup() {
        processingEnvironment = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
        graph = ComponentGraphTraversing(
            processingEnv = processingEnvironment
        )
    }

    @Test
    fun emptyModule() {
        val moduleElement = processingEnvironment.elementUtils.getTypeElement(EmptyModule::class.java.name)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(emptyList<ClassName>(), graph.getClassNames())
    }

    @Test
    fun sampleModule() {
        val moduleElement = processingEnvironment.elementUtils.getTypeElement(SampleModule::class.java.name)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleData::class.java.toClassName()), graph.getClassNames())
    }

    @Test
    fun sampleModuleWithBinds() {
        val moduleElement = processingEnvironment.elementUtils.getTypeElement(SampleModuleWithBinds::class.java.name)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleInterface::class.java.toClassName()), graph.getClassNames())
    }

    @Test
    fun sampleModuleWithWrapperModule() {
        val moduleElement = processingEnvironment.elementUtils.getTypeElement(SampleWithWrapperModule::class.java.name)
        graph.addInjectedClassNames(listOf(SampleData::class.java.toClassName()))
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleDataWrapper::class.java.toClassName()), graph.getClassNames())
    }
}