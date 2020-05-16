package com.dvpermyakov.dagger.utils

import com.squareup.kotlinpoet.ClassName

internal fun String.toClassName(): ClassName {
    val classNameSplit = split(".")
    val packageName = classNameSplit.subList(0, classNameSplit.lastIndex).joinToString(".")
    val simpleName = classNameSplit.last()
    return ClassName(packageName, simpleName)
}

internal fun Class<*>.toClassName(): ClassName {
    return ClassName(packageName, simpleName)
}