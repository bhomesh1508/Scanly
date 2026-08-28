# Original User Request

## Initial Request — 2026-08-27T11:45:49Z

Refactor and extract the Scanly Android application to be a completely self-contained, offline-only project. This involves removing all unused code, dead dependencies, and legacy cloud/Firebase files to ensure a lean, clean, and fully independent codebase. Use a full team of agents to audit the entire project thoroughly.

Working directory: C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android
Integrity mode: development

## Requirements

### R1. Remove Unused Architecture
Audit and delete all Kotlin classes, packages, and mock implementations related to the legacy Firebase/Cloud Sync architecture that are no longer needed. The team should decide the best approach for removing or mocking the UI elements tied to these features.

### R2. Dependency Cleanup
Audit the `build.gradle.kts` files and remove any dependencies that are no longer actively used in the codebase to reduce the final APK size and build time.

### R3. Maintain Core Functionality
The core local functionality (scanning, local storage, PDF export) must remain fully intact and operational.

## Acceptance Criteria

### Objective Verification
- [ ] `./gradlew assembleDebug` completes successfully with 0 build errors.
- [ ] A file count or line count comparison confirms a net reduction in project size.
- [ ] `./gradlew lint` (or similar static analysis) does not flag the removed classes as missing references.
