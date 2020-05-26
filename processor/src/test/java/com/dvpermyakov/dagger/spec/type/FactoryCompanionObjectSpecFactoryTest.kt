package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.*
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toElement
import com.dvpermyakov.dagger.utils.element.getMethodElements
import com.dvpermyakov.dagger.utils.element.toClassName
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class FactoryCompanionObjectSpecFactoryTest {

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
    fun factoryCompanion() {
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            emptyConstructorClassNames = emptyList(),
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
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            emptyConstructorClassNames = listOf(
                EmptyModule::class.java.toClassName(),
                SampleModule::class.java.toClassName()
            ),
            otherClassNames = emptyList()
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(emptyModule = com.dvpermyakov.dagger.sample.EmptyModule(), sampleModule = com.dvpermyakov.dagger.sample.SampleModule())
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryCompanionWithOtherClassNames() {
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            emptyConstructorClassNames = emptyList(),
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
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            emptyConstructorClassNames = listOf(
                EmptyModule::class.java.toClassName(),
                SampleModule::class.java.toClassName()
            ),
            otherClassNames = listOf(SampleData::class.java.toClassName(), SampleDataOther::class.java.toClassName())
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(sampleData: com.dvpermyakov.dagger.sample.SampleData, sampleDataOther: com.dvpermyakov.dagger.sample.SampleDataOther): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(emptyModule = com.dvpermyakov.dagger.sample.EmptyModule(), sampleModule = com.dvpermyakov.dagger.sample.SampleModule(), sampleData = sampleData, sampleDataOther = sampleDataOther)
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun factoryWithMethodAndInterface() {
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val factoryInterfaceElement = SampleComponentFactory::class.java.toElement(processingEnv)
        val factoryMethodElement = factoryInterfaceElement.getMethodElements().first()
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = factoryMethodElement,
            factoryInterfaceElement = factoryInterfaceElement,
            className = "KDaggerComponent",
            emptyConstructorClassNames = emptyList(),
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

    @Test
    fun moduleWithConstructor() {
        val componentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = FactoryCompanionObjectSpecFactory(
            processingEnv = processingEnv,
            componentClassName = componentElement.toClassName(processingEnv),
            factoryMethodElement = null,
            factoryInterfaceElement = null,
            className = "KDaggerComponent",
            emptyConstructorClassNames = emptyList(),
            otherClassNames = listOf(
                SampleModuleWithConstructor::class.java.toClassName()
            )
        ).create()
        Assert.assertEquals(
            """
                |companion object {
                |  fun create(sampleModuleWithConstructor: com.dvpermyakov.dagger.sample.SampleModuleWithConstructor): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerComponent(sampleModuleWithConstructor = sampleModuleWithConstructor)
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }
}