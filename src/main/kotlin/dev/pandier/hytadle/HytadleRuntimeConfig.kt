package dev.pandier.hytadle

import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.process.JavaExecSpec

interface HytadleRuntimeConfig : JavaExecSpec {
    val allowOp: Property<Boolean>
    val authMode: Property<String>
    val disableSentry: Property<Boolean>
    val javaLauncher: Property<JavaLauncher>

    fun copyTo(target: JavaExecSpec)
}
