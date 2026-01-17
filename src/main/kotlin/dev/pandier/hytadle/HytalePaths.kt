package dev.pandier.hytadle

import java.io.File

internal data class HytalePaths(
    val server: File?,
    val assets: File?,
) {
    companion object {
        fun resolve(patchline: String): HytalePaths {
            println("Resolving paths")

            val server = System.getenv("HYTADLE_SERVER_PATH")?.let { File(it)}?.takeIf { it.exists() }
            val assets = System.getenv("HYTADLE_ASSETS_PATH")?.let { File(it) }?.takeIf { it.exists() }

            if (server != null && assets != null) {
                return HytalePaths(server, assets)
            }

            // Find possible paths for a launcher directory
            val launcherPaths = buildList {
                val launcherEnv = System.getenv("HYTADLE_LAUNCHER_PATH")
                if (launcherEnv != null && launcherEnv.isNotBlank()) {
                    add(launcherEnv)
                }

                val os = System.getProperty("os.name").lowercase()

                when {
                    os.contains("windows") -> {
                        System.getenv("APPDATA")?.let { appData ->
                            add("${appData}\\Hytale")
                        }
                    }
                    os.contains("mac") || os.contains("darwin") -> {
                        val home = System.getProperty("user.home")
                        add("${home}/Application Support/Hytale")
                    }
                    os.contains("linux") -> {
                        val home = System.getProperty("user.home")

                        System.getenv("XDG_DATA_HOME")?.let { dataHome ->
                            add("${dataHome}/Hytale")
                        }

                        add("${home}/.local/share/Hytale")
                        add("${home}/.var/app/com.hypixel.HytaleLauncher/data/Hytale") // Flatpak
                    }
                }
            }

            // Try locating the server in each of those paths
            for (launcherPath in launcherPaths) {
                val launcherPaths = resolveLauncher(File(launcherPath), patchline)

                if (launcherPaths.assets == null || launcherPaths.server == null)
                    continue

                // Environment variables have higher priority
                return HytalePaths(server ?: launcherPaths.server, assets ?: launcherPaths.assets)
            }

            return HytalePaths(server, assets)
        }

        fun resolveLauncher(dir: File, patchline: String): HytalePaths {
            val game = dir
                .resolve("install")
                .resolve(patchline)
                .resolve("package")
                .resolve("game")
                .resolve("latest")

            return HytalePaths(
                game.resolve("Server").resolve("HytaleServer.jar").takeIf { it.exists() },
                game.resolve("Assets.zip").takeIf { it.exists() },
            )
        }
    }
}
