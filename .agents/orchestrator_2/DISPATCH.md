# Dispatch Assignment

## 2026-08-28T03:35:06Z

You are the successor Project Orchestrator (Generation 2) for the Scanly Android refactoring project.

Working directory for this project: C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android
Your agent working directory: C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\orchestrator_2
Original User Request file: C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\ORIGINAL_REQUEST.md

Previous Swarm Progress Summary:
- Codebase surveys are complete in `.agents/explorer_survey_1/survey_report.md` and `.agents/explorer_survey_2/survey_report.md`.
- Milestone 1 (Remove Unused Architecture & Legacy Cloud/Firebase Code) was completed by Worker 1. See `.agents/worker_m1/handoff.md` and `.agents/worker_m1/changes.md`. 19 legacy cloud files were removed and 19 persistence/UI/navigation/DI components adapted.
- Milestone 2 (Dependency Cleanup in `build.gradle.kts` and `gradle/libs.versions.toml`) is the next active milestone.
- Milestone 3 (Core Offline Functionality Verification & Hardening) and Final Verification (`./gradlew assembleDebug` or `gradlew.bat assembleDebug`, static analysis / lint check, line/file count reduction comparison) remain to be completed.

Key Requirements:
1. R1. Remove Unused Architecture (Completed, verify cleanly).
2. R2. Dependency Cleanup: Audit and prune dead dependencies (Firebase BOM, Firestore, Auth, Storage, Cloud Messaging, Play Services plugins) from root `build.gradle.kts`, `app/build.gradle.kts`, and `gradle/libs.versions.toml`. Also clean up `AndroidManifest.xml` and ProGuard rules.
3. R3. Maintain Core Functionality: Ensure local scanning, Room DB, PDF export, image processing, OCR, and local settings remain fully intact.

Acceptance Criteria:
- `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows) completes successfully with 0 build errors.
- A file count or line count comparison confirms a net reduction in project size.
- `./gradlew lint` (or static analysis / build checks) does not flag removed classes as missing references.
