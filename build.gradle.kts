plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
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
