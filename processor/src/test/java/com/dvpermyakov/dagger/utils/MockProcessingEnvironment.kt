package com.dvpermyakov.dagger.utils

import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class MockProcessingEnvironment(
    private val elements: Elements,
    private val types: Types
) : ProcessingEnvironment {

    override fun getElementUtils(): Elements {
        return elements
    }

    override fun getTypeUtils(): Types {
        return types
    }

    override fun getMessager(): Messager {
        return MockMessager()
    }

    override fun getLocale(): Locale {
        return Locale.getDefault()
    }

    override fun getSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getOptions(): MutableMap<String, String> {
        return mutableMapOf()
    }

    override fun getFiler(): Filer {
        return MockFiler()
    }

}