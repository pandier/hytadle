package dev.pandier.hytadle

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

    val server: Property<File> = objects.property()

    fun server(value: File) {
        this.server.set(value)
    }

    val aot: Property<Optional<File>> = objects.property()

    fun aot(value: File?) {
        this.aot.set(Optional.ofNullable(value))
    }

    val assets: Property<Optional<String>> = objects.property()

    fun assets(value: String?) {
        this.assets.set(Optional.ofNullable(value))
    }

    fun assets(value: File?) {
        this.assets(value?.path)
    }

    fun launcher(directory: File) {
        val paths by lazy { HytalePaths.resolveLauncher(directory, hytadle.patchline.get()) }
        this.server.set(project.provider { paths.server })
        this.aot.set(project.provider { Optional.ofNullable(paths.aot) })
        this.assets.set(project.provider { Optional.ofNullable(paths.assets) })
    }
}
