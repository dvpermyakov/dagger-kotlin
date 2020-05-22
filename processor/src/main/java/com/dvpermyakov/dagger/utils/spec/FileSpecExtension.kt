package com.dvpermyakov.dagger.utils.spec

import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.ProcessingEnvironment

internal fun List<FileSpec>.writeToDaggerKotlin(
    processingEnv: ProcessingEnvironment
) {
    forEach { fileSpec ->
        fileSpec.writeTo(processingEnv.filer)
    }
}