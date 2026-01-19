package dev.pandier.hytadle.internal

import org.gradle.api.InvalidUserDataException
import java.io.File

internal data class HytalePaths(
    val server: File,
    val aot: File?,
    val assets: String?,
) {
    companion object {
        private const val HYTADLE_SERVER_PATH = "HYTADLE_SERVER_PATH"
        private const val HYTADLE_AOT_PATH = "HYTADLE_AOT_PATH"
        private const val HYTADLE_ASSETS_PATH = "HYTADLE_ASSETS_PATH"
        private const val HYTADLE_LAUNCHER_PATH = "HYTADLE_LAUNCHER_PATH"

        fun resolve(patchline: String): HytalePaths {
            val envServer = System.getenv(HYTADLE_SERVER_PATH)
            val envAot = System.getenv(HYTADLE_AOT_PATH)
            val envAssets = System.getenv(HYTADLE_ASSETS_PATH)
            val envLauncher = System.getenv(HYTADLE_LAUNCHER_PATH)

            val server = envServer?.let { File(it) }
            if (server?.exists() == false)
                throw InvalidUserDataException("Couldn't find a Hytale server at $HYTADLE_SERVER_PATH (${envServer})")

            val aot = envAot?.takeIf { it.isNotBlank() }?.let { File(it) }
            if (aot?.exists() == false)
                throw InvalidUserDataException("Couldn't find a Hytale server AOT file at $HYTADLE_AOT_PATH (${envAot})")

            val assets = envAssets?.takeIf { it.isNotBlank() }

            // We can return early if all variables are set
            // We're not checking AOT because if server path is set then the correct AOT for that server should be specified explicitly
            // (e.g. if we set HYTADLE_SERVER_PATH but not HYTADLE_AOT_PATH, that means we don't have an AOT file)
            if (server != null && envAssets != null) {
                return HytalePaths(server, aot, assets)
            }

            // Find possible paths for a launcher directory
            val launcherPaths = buildList {
                // Only use the environment variable if provided
                if (envLauncher != null) {
                    add(envLauncher)
                    return@buildList
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
                val launcherPaths = resolveLauncherNullable(File(launcherPath), patchline) ?: continue

                // Use environment variables if set
                return HytalePaths(
                    server ?: launcherPaths.server,
                    if (envAot != null || server != null) aot else launcherPaths.aot,
                    if (envAssets != null) assets else launcherPaths.assets,
                )
            }

            if (envLauncher != null)
                throw InvalidUserDataException("Couldn't locate Hytale server files for patchline " +
                        "'${patchline}' at $HYTADLE_LAUNCHER_PATH (${envLauncher})")

            if (server == null)
                throw InvalidUserDataException("Couldn't locate Hytale server files for patchline " +
                        "'${patchline}'. Make sure that the game has been installed through the official launcher " +
                        "with the correct patchline, or configure the paths explicitly.")

            return HytalePaths(server, aot, assets)
        }

        fun resolveLauncher(dir: File, patchline: String): HytalePaths {
            return resolveLauncherNullable(dir, patchline)
                ?: throw InvalidUserDataException("Couldn't locate Hytale server files for patchline " +
                        "'${patchline}' at the provided launcher path (${dir.absolutePath})")
        }

        private fun resolveLauncherNullable(dir: File, patchline: String): HytalePaths? {
            val game = dir
                .resolve("install")
                .resolve(patchline)
                .resolve("package")
                .resolve("game")
                .resolve("latest")

            val serverFile = game.resolve("Server").resolve("HytaleServer.jar")
            val assetsFile = game.resolve("Assets.zip")

            if (!serverFile.exists() || !assetsFile.exists())
                return null

            return HytalePaths(
                serverFile,
                game.resolve("Server").resolve("HytaleServer.aot").takeIf { it.exists() },
                assetsFile.path,
            )
        }
    }
}