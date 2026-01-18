package dev.pandier.hytadle

import org.gradle.api.InvalidUserDataException
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
import kotlin.jvm.optionals.getOrNull

abstract class HytadleExtension @Inject constructor(objects: ObjectFactory, private val project: Project) {

    private val defaultPaths by lazy { HytalePaths.resolve(patchline.get()) }

    val patchline: Property<String> = objects.property<String>().convention("release")

    fun patchline(value: String?) {
        this.patchline.set(value)
    }

    val paths: HytadlePathsConfig = objects.newInstance<HytadlePathsConfig>()

    fun paths(block: HytadlePathsConfig.() -> Unit) {
        this.paths.apply(block)
    }

    val includeDependency: Property<Boolean> = objects.property<Boolean>().convention(true)

    fun includeDependency(value: Boolean) {
        this.includeDependency.set(value)
    }

    fun disableAot() {
        this.paths.aot(null)
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
            .map { it.getOrNull() }
    }

    fun assets(): Provider<String> {
        return paths.assets
            .orElse(project.provider { Optional.ofNullable(defaultPaths.assets) })
            .map { it.getOrNull() }
    }
}
