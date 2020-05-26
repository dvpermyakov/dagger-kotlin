package com.dvpermyakov.dagger.graph

import com.dvpermyakov.dagger.sample.*
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toElement
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

    private lateinit var processingEnv: ProcessingEnvironment
    private lateinit var graph: ComponentGraphTraversing

    @Before
    fun setup() {
        processingEnv = MockProcessingEnvironment(
            elements = compilationRule.elements,
            types = compilationRule.types
        )
        graph = ComponentGraphTraversing(
            processingEnv = processingEnv
        )
    }

    @Test
    fun emptyModule() {
        val moduleElement = EmptyModule::class.java.toElement(processingEnv)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(emptyList<ClassName>(), graph.getClassNames())
    }

    @Test
    fun sampleModule() {
        val moduleElement = SampleModule::class.java.toElement(processingEnv)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleData::class.java.toClassName()), graph.getClassNames())
    }

    @Test
    fun sampleModuleWithBinds() {
        val moduleElement = SampleModuleWithBinds::class.java.toElement(processingEnv)
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleInterface::class.java.toClassName()), graph.getClassNames())
    }

    @Test
    fun sampleModuleWithWrapperModule() {
        val moduleElement = SampleWithWrapperModule::class.java.toElement(processingEnv)
        graph.addInjectedClassNames(listOf(SampleData::class.java.toClassName()))
        graph.setModules(listOf(moduleElement))
        Assert.assertEquals(listOf(SampleDataWrapper::class.java.toClassName()), graph.getClassNames())
    }

    @Test
    fun bindsInstances() {
        graph.addElementsWithBindsInstance(
            listOf(
                SampleData::class.java.toElement(processingEnv),
                SampleDataOther::class.java.toElement(processingEnv)
            )
        )
        Assert.assertEquals(
            listOf(
                SampleData::class.java.toClassName(),
                SampleDataOther::class.java.toClassName()
            ), graph.getClassNames()
        )
    }
}