package dev.pandier.hytadle.tasks

import dev.pandier.hytadle.HytadleExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.getByType
import java.io.File

abstract class RunServer : JavaExec() {

    @get:InputFile
    @get:Optional
    abstract val assets: Property<File>

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

        assets.convention(hytadle.paths.assets)
        mainClass.convention("com.hypixel.hytale.Main") // TODO: Fetch server jar's MANIFEST instead
        standardInput = System.`in`

        classpath(hytadle.paths.server)
    }

    override fun exec() {
        args(buildList {
            assets.orNull?.let { assets ->
                add("--assets")
                add(assets.toString())
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
