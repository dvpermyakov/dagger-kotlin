plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    jcenter()
}

sourceSets.main {
    java.srcDirs("src/main/java")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    implementation("javax.inject:javax.inject:1")
    kapt(project(":processor"))
}