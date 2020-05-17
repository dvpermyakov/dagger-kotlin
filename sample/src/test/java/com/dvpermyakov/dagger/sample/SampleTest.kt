package com.dvpermyakov.dagger.sample

import KDaggerSampleComponent
import com.dvpermyakov.dagger.sample.di.SampleModule
import org.junit.Test

class SampleTest {

    @Test
    fun sampleComponent() {
        val component = com.dvpermyakov.dagger.sample.generated.KDaggerSampleComponent(sampleModule = SampleModule())
        component.getSampleViewModel()
        component.getSampleConfig()
    }

    @Test
    fun sampleComponentGenerated() {
        val component = KDaggerSampleComponent(sampleModule = SampleModule())
        component.getSampleViewModel()
        component.getSampleConfig()
    }

}