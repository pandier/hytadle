plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("hytadle") {
            id = "dev.pandier.hytadle"
            implementationClass = "dev.pandier.hytadle.HytadlePlugin"
        }
    }
}
