package dev.pandier.hytadle

import dev.pandier.hytadle.internal.HytalePaths
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import java.io.File
import java.util.Optional
import javax.inject.Inject

abstract class HytadlePathsConfig @Inject constructor(objects: ObjectFactory, private val project: Project) {

    private val hytadle: HytadleExtension
        get() = project.extensions.getByType()

    /**
     * The `HytaleServer.jar` file.
     */
    val server: Property<File> = objects.property()

    fun server(value: File) {
        server.set(value)
    }

    /**
     * The `HytaleServer.aot` file.
     */
    val aot: Property<Optional<File>> = objects.property()

    fun aot(value: File?) {
        aot.set(Optional.ofNullable(value))
    }

    /**
     * The `Assets.zip` file.
     */
    val assets: Property<Optional<String>> = objects.property()

    fun assets(value: String?) {
        assets.set(Optional.ofNullable(value))
    }

    fun assets(value: File?) {
        assets(value?.absolutePath)
    }

    fun launcher(directory: File) {
        val paths by lazy { HytalePaths.resolveLauncher(directory, hytadle.patchline.get()) }
        server.set(project.provider { paths.server })
        aot.set(project.provider { Optional.ofNullable(paths.aot) })
        assets.set(project.provider { Optional.ofNullable(paths.assets) })
    }
}
