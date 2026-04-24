# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew :app:test              # Run unit tests for a specific module
./gradlew lint                   # Run lint checks
```

**Config:** compileSdk 36, minSdk 31 (Android 12+), Java 11, Kotlin 2.2.10, Gradle 9.3.1

## Architecture

This is a **multi-module, layered Android app** (Jetpack Compose + Material 3). The project is in early scaffold phase — modules are planned but most are not yet created.

### Planned Module Graph

```
:app
 ├── :feature:gallery
 ├── :feature:upload
 ├── :feature:search
 └── :feature:settings
      └── (each feature depends only on core:data, core:ui, core:common, core:domain)

:core:data      → :core:domain, :core:network, :core:database, :core:ml, :core:common
:core:network   (no :core:database dependency — sources don't see each other)
:core:ui        → :core:domain, :core:common
:core:domain    → nothing (pure Kotlin)
:core:common    → nothing (Kotlin + coroutines only)
```

**Critical rule:** features must not depend on other features, or on `:core:network`, `:core:database`, `:core:ml` directly — only through `:core:data` repositories.

### Key Patterns

- **Offline-first (gallery):** Room is source of truth; network refreshes in background via WorkManager
- **Online-only (search):** Server queries, not cached locally
- **Single source of truth:** Features subscribe to `Flow<T>` from shared repositories, not ViewModel-local state
- **DI:** Koin (planned)
- **Networking:** Ktor client + kotlinx.serialization
- **Local DB:** Room
- **On-device ML:** MediaPipe Tasks (auto-tagging)

### Backend

A Kotlin + Ktor server runs in Docker on a Raspberry Pi. The Android client is a pure consumer of this self-hosted REST API.

## Dependencies

All versions managed centrally in `gradle/libs.versions.toml`. Current active deps are minimal (Compose BOM, Material 3, AndroidX core/lifecycle/activity). Planned additions: Koin, Ktor client, Room, WorkManager, MediaPipe.

## Project Phase

Phase 0 (scaffold only). The roadmap in `README.md` defines 8 phases. Convention plugins for modules have not been created yet — when adding modules, follow the dependency rules above strictly.

## Working with Claude Code

- Do not run `./gradlew build`, `assembleDebug`, or similar full-build commands without explicit request. The user runs Gradle locally in Android Studio. Exception: you may run `./gradlew :<module>:test` when asked to verify a specific test you just wrote.
- When suggesting new dependencies, always check `gradle/libs.versions.toml` first and add new libraries there. Never hardcode versions in module `build.gradle.kts` files.
- Before creating a new module, verify it's listed in the Planned Module Graph and confirm with the user which phase we're targeting.
- When writing tests, follow TDD strictly: failing test first, then minimum implementation to pass, then refactor. Show the failing test (or expected failure mode) before writing the implementation.

## Conventions

### Code
- All code comments in English
- Package naming: `dev.jarkendar.photovault.{layer}.{module}.*`
- Kotlin DSL only in Gradle — never Groovy
- Compose-first UI — no XML layouts
- Internationalization: all user-facing strings go in `strings.xml`. Default locale is Polish (`values/strings.xml`), English in `values-en/strings.xml`. Never hardcode user-facing strings in Composables.

### Commits
- Format: Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`)
- No `Co-authored-by` or any attribution lines in commit messages

### Testing
- Assertions: kotlin-test (`assertEquals`, `assertTrue`, `assertFalse`) — multiplatform-ready
- Test runner: JUnit 4 for Android instrumented tests (required by `AndroidJUnit4`), JUnit 5 (Jupiter) for pure JVM modules when added
- Mocking: MockK
- Flow assertions: Turbine
- Approach: TDD red → green → refactor

## Known Quirks

- Project is currently a single `:app` module. The modular structure in "Planned Module Graph" is a target, not yet reality.
- No Gradle convention plugins yet. Initial module setup will use copy-paste of `build.gradle.kts` files, to be refactored into convention plugins in a later phase.
- Kotlin version is 2.2.10 (AGP-paired default). Kotlin 2.3.20 is available but upgrade is deferred until there's a concrete reason.
- **AGP 8+ built-in Kotlin support:** `com.android.library` and `com.android.application` plugins automatically configure Kotlin for modules with `.kt` sources. Do NOT explicitly add `alias(libs.plugins.kotlin.android)` to Android library modules — causes "Cannot add extension with name 'kotlin', as there is an extension already registered". Only add a Kotlin plugin when a different variant is needed: `kotlin.compose` for Compose modules, `kotlin.jvm` for pure Kotlin JVM modules (`:core:common`, `:core:domain`).
- **Room `@Junction` uses `entityColumn`, not `childColumn`.** Older tutorials often show `Junction(value = X, parentColumn, childColumn)`, but current API is `Junction(value = X, parentColumn, entityColumn)`. Using `childColumn` produces cryptic KSP error: "Cannot find the child entity referencing column `id` in the junction".
- **Android instrumented tests need explicit runner setup.** Both the library `androidx.test:runner` (provides `AndroidJUnitRunner`) and the build config `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"` in `android.defaultConfig` are required. Without them, runtime fails with `ClassNotFoundException: androidx.test.runner.AndroidJUnitRunner`. `androidx.test.ext:junit` (provides `AndroidJUnit4` class for `@RunWith`) is a separate dependency — both are needed.
