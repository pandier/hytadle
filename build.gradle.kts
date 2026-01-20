plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "2.0.0"
    id("org.jetbrains.changelog") version "2.5.0"
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website = "https://hytadle.pandier.dev"
    vcsUrl = "https://github.com/pandier/hytadle.git"

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
    repositoryUrl = "https://github.com/pandier/hytadle"
}
