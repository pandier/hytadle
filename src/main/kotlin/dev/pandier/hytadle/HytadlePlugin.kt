package dev.pandier.hytadle

import dev.pandier.hytadle.tasks.RunServer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

class HytadlePlugin : Plugin<Project> {
    companion object {
        const val TASK_GROUP = "hytadle"
    }

    override fun apply(project: Project) {
        val hytadle = project.extensions.create("hytadle", HytadleExtension::class.java)

        project.pluginManager.withPlugin("java") {
            val sourceSets = project.extensions.getByType<SourceSetContainer>()

            project.tasks.register<RunServer>("runServer") {
                group = TASK_GROUP
                description = "Runs the Hytale server for plugin development"
                allowOp.set(true)
                disableSentry.set(true)

                workingDir(project.layout.projectDirectory.dir("run"))
                classpath({ sourceSets.getByName("main").runtimeClasspath })
            }
        }

        project.afterEvaluate {
            if (hytadle.includeDependency.get()) {
                dependencies.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, hytadle.serverDependency())
            }
        }
    }
}
