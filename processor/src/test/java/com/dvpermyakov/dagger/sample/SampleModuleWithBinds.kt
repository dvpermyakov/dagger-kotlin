package com.dvpermyakov.dagger.sample

interface SampleModuleWithBinds {
    fun getSampleInterface(impl: SampleInterfaceImpl): SampleInterface
}