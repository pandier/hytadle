package dev.pandier.hytadle

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class HytadleExtension @Inject constructor(objects: ObjectFactory, private val project: Project) {

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

    fun serverDependency(): Dependency {
        return project.dependencies.create(project.files(paths.server))
    }
}
