package dev.pandier.hytadle.internal

import dev.pandier.hytadle.HytadleRuntimeConfig
import org.gradle.api.Project
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.file.PathToFileResolver
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import org.gradle.process.internal.DefaultJavaExecSpec
import org.jetbrains.annotations.ApiStatus
import javax.inject.Inject

@ApiStatus.Internal
open class DefaultHytadleRuntimeConfig @Inject constructor(
    objects: ObjectFactory,
    resolver: PathToFileResolver,
    fileCollections: FileCollectionFactory,
    project: Project,
) : DefaultJavaExecSpec(objects, resolver, fileCollections), HytadleRuntimeConfig {
    override val allowOp: Property<Boolean> = objects.property<Boolean>().convention(true)
    override val authMode: Property<String> = objects.property()
    override val disableSentry: Property<Boolean> = objects.property<Boolean>().convention(true)
    override val javaLauncher: Property<JavaLauncher> = objects.property()
    override val source: Property<SourceSet> = objects.property()

    init {
        workingDir = project.layout.projectDirectory.dir("run").asFile
        standardInput = System.`in`
        mainClass.convention("com.hypixel.hytale.Main") // TODO: Fetch server jar's MANIFEST instead
        source.convention(project.provider { project.extensions.getByType<SourceSetContainer>().getByName("main") })

        jvmArgs("--enable-native-access=ALL-UNNAMED")
    }
}
