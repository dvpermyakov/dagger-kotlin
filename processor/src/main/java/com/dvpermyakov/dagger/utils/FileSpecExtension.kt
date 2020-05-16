package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.FileSpec
import java.io.File

fun List<FileSpec>.writeToDaggerKotlin() {
    forEach { fileSpec ->
        val file = File("build/dagger-kotlin")
        fileSpec.writeTo(file)
    }
}