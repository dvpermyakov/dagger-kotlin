package com.dvpermyakov.dagger.sample.domain

data class GlobalConfig(
    val profileAvailable: Boolean,
    val cardAvailable: Boolean,
    val transactionAvailable: Boolean
)