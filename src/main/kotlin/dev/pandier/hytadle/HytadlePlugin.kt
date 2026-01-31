package dev.pandier.hytadle

import dev.pandier.hytadle.tasks.RunServerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.kotlin.dsl.register

class HytadlePlugin : Plugin<Project> {
    companion object {
        const val TASK_GROUP = "hytadle"
    }

    override fun apply(project: Project) {
        project.pluginManager.withPlugin("java") {
            val hytadle = project.extensions.create("hytadle", HytadleExtension::class.java)

            project.tasks.register<RunServerTask>("runServer") {
                group = TASK_GROUP
                description = "Runs the Hytale server for plugin development"
            }

            project.afterEvaluate {
                if (hytadle.enableDependency.get()) {
                    dependencies.add(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME, hytadle.serverDependency())
                }
            }
        }
    }
}
