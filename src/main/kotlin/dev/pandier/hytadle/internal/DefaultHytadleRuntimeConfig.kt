package dev.pandier.hytadle.internal

import dev.pandier.hytadle.HytadleRuntimeConfig
import org.gradle.api.file.ProjectLayout
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.file.PathToFileResolver
import org.gradle.kotlin.dsl.property
import org.gradle.process.internal.DefaultJavaExecSpec
import org.jetbrains.annotations.ApiStatus
import javax.inject.Inject

@ApiStatus.Internal
open class DefaultHytadleRuntimeConfig @Inject constructor(
    objectFactory: ObjectFactory,
    resolver: PathToFileResolver,
    fileCollectionFactory: FileCollectionFactory,
    projectLayout: ProjectLayout,
) : DefaultJavaExecSpec(objectFactory, resolver, fileCollectionFactory), HytadleRuntimeConfig {
    override val allowOp: Property<Boolean> = objectFactory.property<Boolean>().convention(true)
    override val authMode: Property<String> = objectFactory.property()
    override val disableSentry: Property<Boolean> = objectFactory.property<Boolean>().convention(true)

    init {
        workingDir = projectLayout.projectDirectory.dir("run").asFile
        standardInput = System.`in`
        mainClass.convention("com.hypixel.hytale.Main") // TODO: Fetch server jar's MANIFEST instead
    }

    override fun copyTo(target: HytadleRuntimeConfig) {
        target.authMode.set(this.authMode)
        target.allowOp.set(this.allowOp)
        target.disableSentry.set(this.disableSentry)
        super.copyTo(target)
    }
}
