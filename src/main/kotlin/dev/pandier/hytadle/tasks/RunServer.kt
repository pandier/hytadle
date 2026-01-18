package dev.pandier.hytadle.tasks

import dev.pandier.hytadle.HytadleExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import java.io.File
import javax.inject.Inject

abstract class RunServer @Inject constructor(
    javaToolchains: JavaToolchainService
) : JavaExec() {

    @get:InputFile
    @get:Optional
    abstract val aot: Property<File>

    @get:Input
    @get:Optional
    abstract val assets: Property<String>

    @get:Input
    @get:Optional
    abstract val authMode: Property<String>

    @get:Input
    @get:Optional
    abstract val allowOp: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val disableSentry: Property<Boolean>

    init {
        val hytadle = project.extensions.getByType<HytadleExtension>()

        aot.convention(hytadle.aot())
        assets.convention(hytadle.assets())
        mainClass.convention("com.hypixel.hytale.Main") // TODO: Fetch server jar's MANIFEST instead

        javaLauncher.convention(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        })

        standardInput = System.`in`
        classpath(hytadle.server())
    }

    override fun exec() {
        jvmArgs(buildList {
            aot.orNull?.let { aot ->
                add("-XX:AOTCache=${aot}")
            }
        })

        args(buildList {
            assets.orNull?.let { assets ->
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

        super.exec()
    }
}
