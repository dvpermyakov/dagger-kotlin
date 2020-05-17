package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.di.SampleModule
import org.junit.Test

class SampleTest {

    @Test
    fun sampleComponent() {
        val component = com.dvpermyakov.dagger.sample.generated.KDaggerSampleComponent(sampleModule = SampleModule())
        component.getSampleViewModel()
        component.getSampleConfig()
    }

}