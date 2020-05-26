package com.dvpermyakov.dagger.sample

import com.dvpermyakov.dagger.annotation.Component

@Component(modules = [], dependencies = [SampleInterface::class])
interface SampleComponentWithDependency