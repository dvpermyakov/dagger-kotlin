package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.sample.di.component.KDaggerValueComponent
import com.dvpermyakov.dagger.sample.di.modules.ValueModule
import org.junit.Assert
import org.junit.Test

class ValueComponentTest {

    @Test
    fun getValue() {
        val value = 10
        val component = KDaggerValueComponent.create(ValueModule(value))
        Assert.assertEquals(value, component.getValue().value)
    }

}