package dev.pandier.hytadle.tasks

import dev.pandier.hytadle.HytadleExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.getByType

abstract class RunServerTask : JavaExec() {

    @get:Input
    abstract val allowOp: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val authMode: Property<String>

    @get:Input
    abstract val disableSentry: Property<Boolean>

    init {
        val runtime = project.extensions.getByType<HytadleExtension>().runtime
        runtime.copyTo(this)
        allowOp.set(runtime.allowOp)
        authMode.set(runtime.authMode)
        disableSentry.set(runtime.disableSentry)
        javaLauncher.set(runtime.javaLauncher)
    }

    override fun exec() {
        val hytadle = project.extensions.getByType<HytadleExtension>()

        classpath(hytadle.server())

        jvmArgs(buildList {
            if (javaLauncher.get().metadata.languageVersion.asInt() >= 25) {
                hytadle.aot().orNull?.let { aot ->
                    add("-XX:AOTCache=${aot}")
                }
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
}
