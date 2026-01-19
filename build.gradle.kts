plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "2.0.0"
    id("org.jetbrains.changelog") version "2.5.0"
}

val repository = property("repository") as String

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://hytadle.pandier.dev"
    vcsUrl = repository

    plugins {
        create("hytadle") {
            id = "dev.pandier.hytadle"
            displayName = "Hytadle"
            description = "An unofficial Gradle plugin for developing Hytale plugins"
            tags = listOf("hytale")
            implementationClass = "dev.pandier.hytadle.HytadlePlugin"
        }
    }
}

changelog {
    groups.empty()
    repositoryUrl = repository
}
