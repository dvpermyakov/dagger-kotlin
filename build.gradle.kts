import java.util.Date

plugins {
    `maven-publish`
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    id("com.jfrog.bintray") version "1.8.5"
}

buildscript {
    repositories {
        dependencies {
            classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        }
    }
}

repositories {
    jcenter()
}

sourceSets.main {
    java.srcDirs("src/main/java")
}

dependencies {
    implementation(kotlin("stdlib"))
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val currentVersion = File("version.txt").readText().trim()

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            group = "com.dvpermyakov"
            version = currentVersion
            artifactId = "dagger-kotlin"
            from(components["java"])
            artifact(sourcesJar)
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository")
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    publish = true

    setPublications("mavenJava")

    pkg.apply {
        repo = "maven"
        name = "com.dvpermyakov.dagger-kotlin"
        vcsUrl = "https://github.com/dvpermyakov/dagger-kotlin"
        setLicenses("MIT")

        version.apply {
            name = currentVersion
            desc = ""
            released = Date().toString()
            vcsTag = currentVersion
        }
    }
}