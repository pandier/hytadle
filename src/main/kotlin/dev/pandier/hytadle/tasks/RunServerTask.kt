package dev.pandier.hytadle.tasks

import dev.pandier.hytadle.HytadleExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.internal.JavaExecExecutableUtils
import org.gradle.kotlin.dsl.getByType

abstract class RunServerTask : JavaExec() {

    @get:Input
    abstract val allowOp: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val authMode: Property<String>

    @get:Input
    abstract val disableSentry: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val source: Property<SourceSet>

    init {
        val runtime = project.extensions.getByType<HytadleExtension>().runtime
        runtime.copyTo(this)

        allowOp.set(runtime.allowOp)
        authMode.set(runtime.authMode)
        disableSentry.set(runtime.disableSentry)
        source.set(runtime.source)

        val javaPluginExtension = project.extensions.getByType<JavaPluginExtension>()

        val defaultToolchainSpec = project.provider {
            JavaExecExecutableUtils.getExecutableOverrideToolchainSpec(this, propertyFactory)
        }.orElse(javaPluginExtension.toolchain)
        val defaultJavaLauncher = defaultToolchainSpec.flatMap(javaToolchainService::launcherFor)

        javaLauncher.set(runtime.javaLauncher.orElse(defaultJavaLauncher))
    }

    override fun exec() {
        val hytadle = project.extensions.getByType<HytadleExtension>()

        classpath(hytadle.server())

        source.orNull?.let { source ->
            classpath(source.runtimeClasspath)
        }

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
