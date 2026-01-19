package dev.pandier.hytadle.tasks

import dev.pandier.hytadle.HytadleExtension
import dev.pandier.hytadle.HytadleRuntimeConfig
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import javax.inject.Inject

abstract class RunServerTask @Inject constructor(
    javaToolchains: JavaToolchainService
) : JavaExec(), HytadleRuntimeConfig {

    @get:Input
    abstract override val allowOp: Property<Boolean>

    @get:Input
    @get:Optional
    abstract override val authMode: Property<String>

    @get:Input
    abstract override val disableSentry: Property<Boolean>

    init {
        val hytadle = project.extensions.getByType<HytadleExtension>()
        hytadle.runtime.copyTo(this)

        javaLauncher.convention(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        })
    }

    override fun exec() {
        val hytadle = project.extensions.getByType<HytadleExtension>()

        classpath(hytadle.server())

        jvmArgs(buildList {
            hytadle.aot().orNull?.let { aot ->
                add("-XX:AOTCache=${aot}")
            }
        })

        args(buildList {
            hytadle.assets().orNull?.let { assets ->
                add("--assets")
                add(assets)
            }

            if (allowOp.orNull == true) {
                add("--allow-op")
            }

            if (disableSentry.orNull == true) {
                add("--disable-sentry")
            }

            authMode.orNull?.let { authMode ->
                add("--auth-mode")
                add(authMode)
            }
        })

        if (!workingDir.exists()) {
            workingDir.mkdirs()
        }

        super.exec()
    }

    override fun copyTo(target: HytadleRuntimeConfig) {
        throw UnsupportedOperationException()
    }
}
