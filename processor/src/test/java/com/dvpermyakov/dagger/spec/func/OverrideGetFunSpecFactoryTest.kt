package com.dvpermyakov.dagger.spec.func

import com.dvpermyakov.dagger.utils.className.toClassName
import org.junit.Assert
import org.junit.Test

class OverrideGetFunSpecFactoryTest {

    @Test
    fun overrideGetFun() {
        val funSpec = OverrideGetFunSpecFactory(
            returnTypeName = Int::class.java.toClassName(),
            statement = "return 0"
        ).create()
        Assert.assertEquals(
            """
                |override fun get(): java.lang.int = 0
                |""".trimMargin(),
            funSpec.toString()
        )
    }

}