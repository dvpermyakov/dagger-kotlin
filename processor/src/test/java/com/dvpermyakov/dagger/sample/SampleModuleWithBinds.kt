package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Module

@Module
interface SampleModuleWithBinds {
    fun getSampleInterface(impl: SampleInterfaceImpl): SampleInterface
}