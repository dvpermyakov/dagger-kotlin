package com.dvpermyakov.dagger.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subcomponent(
    val modules: Array<KClass<*>>
)