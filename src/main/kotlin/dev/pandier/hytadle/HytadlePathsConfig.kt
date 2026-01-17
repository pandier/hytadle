package dev.pandier.hytadle

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject

abstract class HytadlePathsConfig @Inject constructor(objects: ObjectFactory, private val project: Project) {

    private val hytadle
        get() = project.extensions.getByType<HytadleExtension>()

    private val defaultPaths by lazy { HytalePaths.resolve(hytadle.patchline.get()) }

    val server: Property<File> = objects.property<File>()
        .convention(project.provider { defaultPaths.server })

    fun server(value: File) {
        this.server.set(value)
    }

    val assets: Property<File> = objects.property<File>()
        .convention(project.provider { defaultPaths.assets })

    fun assets(value: File) {
        this.assets.set(value)
    }

    fun launcher(directory: File) {
        val paths by lazy { HytalePaths.resolveLauncher(directory, hytadle.patchline.get()) }
        this.server.set(project.provider { paths.server })
        this.assets.set(project.provider { paths.assets })
    }
}
