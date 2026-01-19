# hytadle

> [!NOTE]
> Hytadle is in early development and a lot of changes should be expected.
> Make sure to check new versions frequently for bugfixes and new features.

An unofficial [Gradle](https://gradle.org/) plugin that sets up Hytale dependencies and a server runtime.

- Automatically locates game files installed through the official launcher
- Adds the server jar as a dependency
- Sets up a `runServer` task that runs a server locally
- Supports using a debugger (including breakpoints and HotSwap) 
- Lets you specify paths to game files using environment variables

## 🚀 Getting started

Create an empty Gradle project through your IDE of choice or by following
[this guide](https://docs.gradle.org/current/userguide/part1_gradle_init.html).
It's recommended to use the Kotlin DSL and the following code examples will be written in it.

This plugin can also be applied to an existing project by first removing all code that sets up Hytale dependencies,
or by configuring Hytadle to not include the dependency (see section [Additional configuration](#additional-configuration)).

### Applying the plugin

At the top of your `build.gradle(.kts)` file, add the following code:

```kts
plugins {
    java
    id("dev.pandier.hytadle") version "0.0.0"
}
```

And that's it! Hytale will now be properly added as a dependency as long as you have it installed.

### Running the server

Hytadle provides a `runServer` task that lets you run a Hytale server locally with the configured options.
This task also allows you to use the debugger in most IDEs.

**Using IntelliJ IDEA**

Double-click Ctrl and type in `gradle runServer`. The task will then appear as a run configuration
in the toolbar and can be run again by simply clicking the Run icon. To use the debugger, click the Debug icon.

**Using the command-line**

Run `./gradlew runServer`

## ⚙️ Configuring

### Changing the patchline

The game's release cycle is split into multiple channels called patchlines. You can see those patchlines by going
into the launcher's settings. Hytadle lets you change which patchline will be used to locate the game files:

```kts
hytadle {
    patchline = "pre-release"
}
```

Do note that the game needs to be installed for that patchline, and multiple patchlines can be installed at once.

### Overriding paths to game files

You can override the location of game files either by specifying them in the build configuration or by using
environment variables. You can also specify the path to the lancher or to each file individually.

Build configuration has a higher priority than environment variables.

> [!NOTE]
> Using build configuration to specify game files is **not recommended** as the path will be shared across all
> machines (unless that's intended of course). You can read a guide on how to set up environment variables
> in IntelliJ IDEA [here](https://www.jetbrains.com/help/idea/program-arguments-and-environment-variables.html#environment_variables).

<details>
<summary><strong>Using environment variables</strong></summary>
<p></p>

a) Path to the launcher files:

```properties
HYTADLE_LAUNCHER_PATH=/home/user/my-epic-launcher-path/Hytale
```

b) Individual files:

```properties
HYTADLE_SERVER_PATH=/path/to/the/HytaleServer.jar
HYTADLE_ASSETS_PATH=/path/to/the/Assets.zip
# Optional
HYTADLE_AOT_PATH=/path/to/the/HytaleServer.aot
```

If you only specify some of these files, the rest will be automatically located. If you want to disable that,
set the property to an empty string. For example, setting `HYTADLE_AOT_PATH=` as an environment variable
will disable the AOT cache file.

</details>

<details>
<summary><strong>Using build configuration</strong></summary>
<p></p>

a) Path to the launcher files:

```kts
hytadle {
    paths {
        launcher(file("/home/user/my-epic-launcher-path/Hytale"))
    }
}
```

b) Individual files:

```kts
hytadle {
    paths {
        server(file("/path/to/the/HytaleServer.jar"))
        assets(file("/path/to/the/Assets.zip"))
        
        // Optional
        aot(file("/path/to/the/HytaleServer.aot"))
    }
}
```

If you only specify some of these files, the rest will be automatically located. If you want to disable that,
set the property to `null`. For example, `aot(null)` will disable the AOT cache file.

</details>

### Modifying the `runServer` environment

```kts
hytadle {
    runtime {
        // Allows you to configure a bunch of stuff about the runtime of a local Hytale server. This includes
        // all options of a JavaExec task plus some additional ones. The following is just a selection of some
        // of the properties. You can use auto-completion to show them all.

        allowOp = true
        disableSentry = true
        authMode = "authenticated"

        // Sets the directory where the Hytale server will be running.
        workingDir("run")
        
        // Adds additional arguments supplied to the Hytale server. Use the "--help" argument to show all available arguments.
        // The following is just an example.
        args("--backup", "--backup-dir", "./myBackups")
        
        // Adds arguments supplied to the JVM. Don't use this if you don't know what you're doing.
        // The following is just an example.
        jvmArgs("-Xlog:aot")
    }
}
```

### Additional configuration

```kts
hytadle {
    // Disables the use of an AOT cache file. Equivalent to "paths.aot(null)" or "HYTADLE_AOT_PATH=".
    disableAot()

    // Disables the addition of Hytale as a dependency.
    disableDependency()
}
```

## 🐞 Report a bug

You can report a bug in [the issue tracker](https://github.com/pandier/hytadle/issues).

## 📜 License

This project is licensed under [the MIT license](https://github.com/pandier/hytadle/blob/main/LICENSE).

This project is not affiliated with, endorsed by, or sponsored by Hypixel Studios or its affiliates.
