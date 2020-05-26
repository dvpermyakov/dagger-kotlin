package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.sample.SampleData
import com.dvpermyakov.dagger.sample.SampleDataOther
import com.dvpermyakov.dagger.utils.className.toClassName
import com.dvpermyakov.dagger.utils.className.toParameterData
import org.junit.Assert
import org.junit.Test

class ConstructorFunSpecFactoryTest {

    @Test
    fun constructorFunSpec() {
        val parameters = listOf(
            SampleData::class.java.toClassName().toParameterData(),
            SampleDataOther::class.java.toClassName().toParameterData()
        )
        val funSpec = ConstructorSpecFactory(
            parameters = parameters
        ).create()
        Assert.assertEquals(
            """
                |constructor(sampleData: com.dvpermyakov.dagger.sample.SampleData, sampleDataOther: com.dvpermyakov.dagger.sample.SampleDataOther)
                |""".trimMargin(),
            funSpec.toString()
        )
    }

}