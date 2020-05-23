package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.*
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.dvpermyakov.dagger.utils.element.toClassName
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class FactoryCompanionObjectSpecTest {

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
    fun factoryCompanion() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val typeSpec = FactoryCompanionObjectSpec(
            processingEnv = processingEnvironment,
            componentClassName = componentElement.toClassName(processingEnvironment),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            moduleClassNames = emptyList(),
            otherClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent()
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryCompanionWithModules() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val typeSpec = FactoryCompanionObjectSpec(
            processingEnv = processingEnvironment,
            componentClassName = componentElement.toClassName(processingEnvironment),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            moduleClassNames = listOf(EmptyModule::class.java.toClassName(), SampleModule::class.java.toClassName()),
            otherClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(com.dvpermyakov.dagger.sample.EmptyModule(), com.dvpermyakov.dagger.sample.SampleModule())
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryCompanionWithOtherClassNames() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val typeSpec = FactoryCompanionObjectSpec(
            processingEnv = processingEnvironment,
            componentClassName = componentElement.toClassName(processingEnvironment),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            moduleClassNames = emptyList(),
            otherClassNames = listOf(SampleData::class.java.toClassName(), SampleDataOther::class.java.toClassName())
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(sampleData: com.dvpermyakov.dagger.sample.SampleData, sampleDataOther: com.dvpermyakov.dagger.sample.SampleDataOther): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(sampleData = sampleData, sampleDataOther = sampleDataOther)
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryCompanionWithModulesAndOtherClassNames() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val typeSpec = FactoryCompanionObjectSpec(
            processingEnv = processingEnvironment,
            componentClassName = componentElement.toClassName(processingEnvironment),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            moduleClassNames = listOf(EmptyModule::class.java.toClassName(), SampleModule::class.java.toClassName()),
            otherClassNames = listOf(SampleData::class.java.toClassName(), SampleDataOther::class.java.toClassName())
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(sampleData: com.dvpermyakov.dagger.sample.SampleData, sampleDataOther: com.dvpermyakov.dagger.sample.SampleDataOther): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(com.dvpermyakov.dagger.sample.EmptyModule(), com.dvpermyakov.dagger.sample.SampleModule(), sampleData = sampleData, sampleDataOther = sampleDataOther)
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryWithMethodAndInterface() {
        val componentElement = processingEnvironment.elementUtils.getTypeElement(SampleComponent::class.java.name)
        val factoryInterfaceElement = processingEnvironment.elementUtils.getTypeElement(SampleComponentFactory::class.java.name)
        val factoryMethodElement = factoryInterfaceElement.getMethodElements().first()
        val typeSpec = FactoryCompanionObjectSpec(
            processingEnv = processingEnvironment,
            componentClassName = componentElement.toClassName(processingEnvironment),
            factoryMethodElement = factoryMethodElement,
            factoryInterfaceElement = factoryInterfaceElement,
            className = "KDaggerComponent",
            moduleClassNames = emptyList(),
            otherClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |companion object : com.dvpermyakov.dagger.sample.SampleComponentFactory {
                |  override fun createInstance(): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent()
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }
}