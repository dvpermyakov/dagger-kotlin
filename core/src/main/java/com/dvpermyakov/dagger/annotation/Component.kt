package com.dvpermyakov.dagger.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Component(
    val modules: Array<KClass<*>>
)