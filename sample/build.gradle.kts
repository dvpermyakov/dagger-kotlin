plugins {
    kotlin("jvm")
    kotlin("kapt")
}

repositories {
    jcenter()
}

sourceSets.main {
    java.srcDirs("src/main/java")
    java.srcDirs("build/dagger-kotlin")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    implementation("javax.inject:javax.inject:1")
    kapt(project(":processor"))

    testImplementation("junit:junit:4.13")
    testImplementation("io.mockk:mockk:1.10.0")
}