package com.dvpermyakov.dagger.sample.di

import KDaggerMainComponent

object MainComponentFactory {
    fun create(module: MainModule): MainComponent = KDaggerMainComponent(mainModule = module)
}