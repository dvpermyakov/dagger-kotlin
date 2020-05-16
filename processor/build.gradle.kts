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
    implementation(kotlin("stdlib"))
    implementation(project(":core"))
    implementation("com.squareup:kotlinpoet:1.5.0")
    implementation("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")
}