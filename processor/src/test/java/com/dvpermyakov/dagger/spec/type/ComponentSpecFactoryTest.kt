package com.dvpermyakov.dagger.spec.type

import com.dvpermyakov.dagger.sample.*
import com.dvpermyakov.dagger.utils.MockProcessingEnvironment
import com.dvpermyakov.dagger.utils.className.toElement
import com.google.testing.compile.CompilationRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.annotation.processing.ProcessingEnvironment

class ComponentSpecFactoryTest {

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
    fun sampleComponent() {
        val sampleComponentElement = SampleComponent::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent : com.dvpermyakov.dagger.sample.SampleComponent {
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponent = KDaggerSampleComponent()
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithInjectedConstructor() {
        val sampleComponentElement = SampleComponentWithInjectedConstructor::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent : com.dvpermyakov.dagger.sample.SampleComponentWithInjectedConstructor {
                |  private val sampleDataWithInjectedConstructorProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor> = com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor_Factory()
                |
                |  override fun getSampleData(): com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor = sampleDataWithInjectedConstructorProvider.get()
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithInjectedConstructor = KDaggerSampleComponent()
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithInjectedField() {
        val sampleComponentElement = SampleComponentWithInjectedField::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent : com.dvpermyakov.dagger.sample.SampleComponentWithInjectedField {
                |  private val sampleDataWithInjectedConstructorProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor> = com.dvpermyakov.dagger.sample.SampleDataWithInjectedConstructor_Factory()
                |
                |  override fun inject(arg0: com.dvpermyakov.dagger.sample.SampleDataWithInjectedField) {
                |    arg0.sampleData = sampleDataWithInjectedConstructorProvider.get()
                |  }
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithInjectedField = KDaggerSampleComponent()
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithSubcomponent() {
        val sampleComponentElement = SampleComponentWithSubcomponent::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent : com.dvpermyakov.dagger.sample.SampleComponentWithSubcomponent {
                |  override fun getSubcomponent(): com.dvpermyakov.dagger.sample.SampleSubcomponent = object : com.dvpermyakov.dagger.sample.SampleSubcomponent {
                |  }
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithSubcomponent = KDaggerSampleComponent()
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithModule() {
        val sampleComponentElement = SampleComponentWithModule::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent(
                |  sampleModule: com.dvpermyakov.dagger.sample.SampleModule
                |) : com.dvpermyakov.dagger.sample.SampleComponentWithModule {
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.sample.SampleModule_SampleData_Factory(sampleModule)
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithModule = KDaggerSampleComponent(sampleModule = com.dvpermyakov.dagger.sample.SampleModule())
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithDependency() {
        val sampleComponentElement = SampleComponentWithDependency::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent(
                |  sampleInterface: com.dvpermyakov.dagger.sample.SampleInterface
                |) : com.dvpermyakov.dagger.sample.SampleComponentWithDependency {
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.utils.ContainerProvider<com.dvpermyakov.dagger.sample.SampleData>(sampleInterface.getData())
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithDependency = KDaggerSampleComponent()
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }

    @Test
    fun sampleComponentWithSuperInterface() {
        val sampleComponentElement = SampleComponentWithSuperInterface::class.java.toElement(processingEnv)
        val typeSpec = ComponentSpecFactory(
            processingEnv = processingEnv,
            className = "KDaggerSampleComponent",
            componentElement = sampleComponentElement
        ).create()
        Assert.assertEquals(
            """
                |@javax.annotation.processing.Generated
                |class KDaggerSampleComponent(
                |  sampleModule: com.dvpermyakov.dagger.sample.SampleModule
                |) : com.dvpermyakov.dagger.sample.SampleComponentWithSuperInterface {
                |  private val sampleDataProvider: javax.inject.Provider<com.dvpermyakov.dagger.sample.SampleData> = com.dvpermyakov.dagger.sample.SampleModule_SampleData_Factory(sampleModule)
                |
                |  override fun getData(): com.dvpermyakov.dagger.sample.SampleData = sampleDataProvider.get()
                |
                |  companion object {
                |    fun create(): com.dvpermyakov.dagger.sample.SampleComponentWithSuperInterface = KDaggerSampleComponent(sampleModule = com.dvpermyakov.dagger.sample.SampleModule())
                |  }
                |}
                |""".trimMargin(),
            typeSpec.toString()
        )
    }
}