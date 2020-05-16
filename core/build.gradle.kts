plugins {
    kotlin("jvm")
}

repositories {
    jcenter()
}

sourceSets.main {
    java.srcDirs("src/main/java")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("javax.inject:javax.inject:1")
}