package com.dvpermyakov.dagger.utils

import com.google.testing.compile.JavaFileObjects
import javax.annotation.processing.Filer
import javax.lang.model.element.Element
import javax.tools.FileObject
import javax.tools.JavaFileManager
import javax.tools.JavaFileObject

class MockFiler : Filer {
    private val mockFileObject = JavaFileObjects.forSourceString("Mock", "final class Mock {}")


    override fun createSourceFile(
        p0: CharSequence?,
        vararg p1: Element?
    ): JavaFileObject {
        return mockFileObject
    }

    override fun createClassFile(
        p0: CharSequence?,
        vararg p1: Element?
    ): JavaFileObject {
        return mockFileObject
    }

    override fun getResource(
        p0: JavaFileManager.Location?,
        p1: CharSequence?,
        p2: CharSequence?
    ): FileObject {
        return mockFileObject
    }

    override fun createResource(
        p0: JavaFileManager.Location?,
        p1: CharSequence?,
        p2: CharSequence?,
        vararg p3: Element?
    ): FileObject {
        return mockFileObject
    }
}