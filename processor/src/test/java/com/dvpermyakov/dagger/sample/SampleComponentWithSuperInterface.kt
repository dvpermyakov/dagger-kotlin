package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Component

@Component(modules = [SampleModule::class], dependencies = [])
interface SampleComponentWithSuperInterface : SampleInterface