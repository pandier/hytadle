# Changelog

All notable changes to this project will be documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Add `javaLauncher` property in the `runtime` block for modifying the [`JavaLauncher`](https://docs.gradle.org/current/javadoc/org/gradle/jvm/toolchain/JavaLauncher.html).

### Changed

- The `runServer` task no longer forces Java 25 by default
- Add the `--enable-native-access=ALL-UNNAMED` JVM argument to server runtime by default

## [0.1.0] - 2026-01-20

Initial release

[Unreleased]: https://github.com/pandier/hytadle/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/pandier/hytadle/commits/v0.1.0
