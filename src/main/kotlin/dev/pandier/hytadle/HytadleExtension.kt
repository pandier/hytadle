package dev.pandier.hytadle

import dev.pandier.hytadle.internal.DefaultHytadleRuntimeConfig
import dev.pandier.hytadle.internal.HytalePaths
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import java.io.File
import java.util.Optional
import javax.inject.Inject

abstract class HytadleExtension @Inject constructor(objects: ObjectFactory, private val project: Project) {

    private val defaultPaths by lazy { HytalePaths.resolve(patchline.get()) }

    /**
     * The patchline (e.g., release, pre-release) used when locating game files.
     */
    val patchline: Property<String> = objects.property<String>().convention("release")

    /**
     * Overrides for individual paths of game files.
     */
    val paths: HytadlePathsConfig = objects.newInstance<HytadlePathsConfig>()

    fun paths(block: HytadlePathsConfig.() -> Unit) {
        paths.apply(block)
    }

    /**
     * Configuration for the runtime of a local server.
     */
    val runtime: HytadleRuntimeConfig = objects.newInstance<DefaultHytadleRuntimeConfig>()

    fun runtime(value: HytadleRuntimeConfig.() -> Unit) {
        runtime.apply(value)
    }

    /**
     * If enabled, the Hytale server will be added as a dependency.
     */
    val enableDependency: Property<Boolean> = objects.property<Boolean>().convention(true)

    /**
     * @see enableDependency
     */
    fun disableDependency() {
        enableDependency.set(false)
    }

    /**
     * Disables the AOT cache file.
     *
     * This is a shorthand for:
     *
     * ```kts
     * paths {
     *     aot(null)
     * }
     * ```
     */
    fun disableAot() {
        paths.aot(null)
    }

    fun server(): Provider<File> {
        return paths.server.orElse(project.provider { defaultPaths.server })
    }

    fun serverDependency(): Dependency {
        return project.dependencies.create(project.files(server()))
    }

    fun aot(): Provider<File> {
        // Don't fall back to defaults if server is set explicitly
        return paths.aot
            .orElse(project.provider { Optional.ofNullable(if (paths.server.orNull == null) defaultPaths.aot else null) })
            .map { it.orElse(null) }
    }

    fun assets(): Provider<String> {
        return paths.assets
            .orElse(project.provider { Optional.ofNullable(defaultPaths.assets) })
            .map { it.orElse(null) }
    }
}
