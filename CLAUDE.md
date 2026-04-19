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

## Conventions

### Code
- All code comments in English
- Package naming: `dev.jarkendar.photovault.{layer}.{module}.*`
- Kotlin DSL only in Gradle — never Groovy
- Compose-first UI — no XML layouts

### Commits
- Format: Conventional Commits (`feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`)
- No `Co-authored-by` or any attribution lines in commit messages

### Testing
- Framework: JUnit 5 + MockK + Turbine (for Flows)
- Approach: TDD red → green → refactor
