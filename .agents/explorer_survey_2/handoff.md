# Self-Contained Handoff Report - Explorer 2 (Dependency & Build Configuration Auditor)

**Date**: 2026-08-27  
**Agent**: Explorer 2 (Dependency & Build Configuration Auditor)  
**Parent Agent**: df5d44e1-5699-4cdd-8128-9089f0a21f84  
**Report Reference**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_survey_2\survey_report.md`  

---

## 1. Observation

### 1.1 Gradle Files & Project Structure
- **Root `settings.gradle.kts`** (lines 22-23):
  ```kotlin
  rootProject.name = "Scanly"
  include(":app")
  ```
- **Root `build.gradle.kts`** (lines 2-8):
  ```kotlin
  plugins {
      alias(libs.plugins.android.application) apply false
      alias(libs.plugins.compose.compiler) apply false
      alias(libs.plugins.hilt) apply false
      alias(libs.plugins.ksp) apply false
      alias(libs.plugins.google.services) apply false
  }
  ```
- **Gradle Wrapper** (`gradle/wrapper/gradle-wrapper.properties`, line 3):
  `distributionUrl=https\://services.gradle.org/distributions/gradle-9.7.1-bin.zip`
- **Build Settings & Compatibility** (`app/build.gradle.kts`, lines 8-46):
  - `namespace = "com.docscanner.app"`
  - `compileSdk = 37`, `minSdk = 24`, `targetSdk = 34`
  - `sourceCompatibility = JavaVersion.VERSION_21`, `targetCompatibility = JavaVersion.VERSION_21`
  - `compose = true`

### 1.2 Declared Dependencies vs Codebase Usage
- **Firebase Stack** (`app/build.gradle.kts` lines 86-91):
  - `implementation(platform(libs.firebase.bom))`
  - `implementation(libs.firebase.auth)` (Only used in `FirebaseAuthService.kt:8-10`)
  - `implementation(libs.firebase.firestore)` (Only used in `FirestoreService.kt:3-4` and unused imports in `DocScannerApp.kt:5-6`)
  - `implementation(libs.firebase.storage)` (Only used in `CloudStorageService.kt:4`)
  - `implementation(libs.firebase.analytics)` (0 imports / 0 usages across the entire codebase)
- **Google Identity & Credential Manager** (`app/build.gradle.kts` lines 103-105):
  - `implementation(libs.credentials)` (Only used in `FirebaseAuthService.kt:4-5`)
  - `implementation(libs.credentials.play.services.auth)` (Used as auth provider for CredentialManager in `FirebaseAuthService.kt`)
  - `implementation(libs.googleid)` (Only used in `FirebaseAuthService.kt:6-7`)
- **Coroutines Play Services** (`app/build.gradle.kts` line 60):
  - `implementation(libs.coroutines.play.services)` (Only used for `.await()` on Google Task objects in `FirebaseAuthService.kt:14`, `FirestoreService.kt:8`, `CloudStorageService.kt:5`)
- **WorkManager & Background Sync** (`app/build.gradle.kts` lines 78-79, 98):
  - `implementation(libs.work.runtime.ktx)` (Only used in `DocumentSyncWorker.kt:5-6` and `SyncRepositoryImpl.kt:4-9`)
  - `implementation(libs.hilt.work)` (Only used in `DocumentSyncWorker.kt:4`)
  - `ksp(libs.hilt.work.compiler)` (Annotation processor for `DocumentSyncWorker.kt`)
- **Coil Network Engine** (`app/build.gradle.kts` line 109):
  - `implementation(libs.coil.network.okhttp)` (No usages; app only loads local images via `libs.coil.compose`)
- **Active Local Libraries**:
  - Room (`room-runtime`, `room-ktx`, `room-compiler`) in `AppDatabase.kt`, `DocumentDao.kt`, etc.
  - ML Kit (`mlkit-docscanner`, `mlkit-text-recognition`) in `ScannerScreen.kt`, `ViewerViewModel.kt`
  - Biometrics & Security (`biometric`, `security-crypto`) in `AppLockGate.kt`, `EncryptionService.kt`
  - DataStore (`datastore-preferences`) in `SettingsRepositoryImpl.kt`
  - Core & Compose UI (`core-ktx`, `lifecycle-*`, `coroutines-android`, `compose-*`, `navigation-compose`, `hilt-*`) across UI and DI layers.

### 1.3 AndroidManifest & Proguard Directives
- `AndroidManifest.xml` (lines 53-68): Overrides and disables `WorkManagerInitializer` and `FirebaseInitProvider` using `tools:node="remove"`.
- `app/proguard-rules.pro` (lines 1-5): Contains keep rules for `com.google.firebase.database.IgnoreExtraProperties`.

---

## 2. Logic Chain

1. **Step 1 (Offline Scope Definition)**: Per `ORIGINAL_REQUEST.md`, the Scanly application is being refactored to be 100% self-contained and offline-only, eliminating all legacy cloud sync, authentication, and Firebase infrastructure.
2. **Step 2 (Firebase & Auth Isolation)**: Observations 1.2 demonstrate that `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`, `credentials`, `credentials-play-services-auth`, and `googleid` are exclusively referenced by three legacy remote services (`FirebaseAuthService.kt`, `FirestoreService.kt`, `CloudStorageService.kt`) or are completely unused (`firebase-analytics`). Removing these services renders all 8 dependencies dead.
3. **Step 3 (Task Coroutines Dependency)**: `coroutines-play-services` is solely imported to call `.await()` on Firebase/Play Tasks in the aforementioned remote services. With their deletion, this library is unused.
4. **Step 4 (WorkManager Isolation)**: Background WorkManager (`work-runtime-ktx`, `hilt-work`, `hilt-work-compiler`) is solely used by `DocumentSyncWorker.kt` and `SyncRepositoryImpl.kt` for cloud sync scheduling. The local app does not perform any offline background processing with WorkManager. Thus, the entire WorkManager and Hilt Work stack is dead.
5. **Step 5 (OkHttp / Network Isolation)**: `coil-network-okhttp` is only required for remote HTTP/HTTPS image loading. Scanly only displays locally captured and stored documents (`AsyncImage(document.thumbnailUrl)` where thumbnailUrl is a local file URI). Hence `coil-network-okhttp` is dead.
6. **Step 6 (Plugin & Manifest Cleansing)**: The `com.google.gms.google-services` plugin and the `FirebaseInitProvider` / `WorkManagerInitializer` nodes in `AndroidManifest.xml`, along with Firebase rules in `app/proguard-rules.pro`, exist exclusively to support the dead cloud dependencies and should be removed.

---

## 3. Caveats

1. **Test Dependencies**: `junit`, `mockk`, `turbine`, `room-testing`, and Compose UI test libraries are currently declared in `app/build.gradle.kts`, but there are 0 test files in `app/src/test` or `app/src/androidTest`. We recommend retaining standard test dependencies in `build.gradle.kts` and catalog so future unit/integration tests can be written without needing to re-add them.
2. **Offline Network Permissions**: `android.permission.INTERNET` and `android.permission.ACCESS_NETWORK_STATE` in `AndroidManifest.xml` can be safely removed or kept minimal. Removing them enforces the offline guarantee at the OS level.
3. **Build Execution Permission**: Command execution was performed via file viewing and AST/manifest analysis rather than direct terminal execution to ensure safety and prevent timeout prompts.

---

## 4. Conclusion

A total of **12 libraries**, **1 Gradle plugin**, **2 manifest provider entries**, and **1 Proguard rule block** can and should be eliminated from the build configuration.

### Summary of Items to Remove:
- **`app/build.gradle.kts` dependencies**:
  1. `implementation(libs.coroutines.play.services)`
  2. `implementation(libs.hilt.work)`
  3. `ksp(libs.hilt.work.compiler)`
  4. `implementation(platform(libs.firebase.bom))`
  5. `implementation(libs.firebase.auth)`
  6. `implementation(libs.firebase.firestore)`
  7. `implementation(libs.firebase.storage)`
  8. `implementation(libs.firebase.analytics)`
  9. `implementation(libs.work.runtime.ktx)`
  10. `implementation(libs.credentials)`
  11. `implementation(libs.credentials.play.services.auth)`
  12. `implementation(libs.googleid)`
  13. `implementation(libs.coil.network.okhttp)`
- **`build.gradle.kts` (root)**:
  - `alias(libs.plugins.google.services) apply false`
- **`gradle/libs.versions.toml`**:
  - Remove unused version refs (`firebaseBom`, `workManager`, `credentials`, `googleid`, `hiltWork`, `googleServices`).
  - Remove unused library keys (`coroutines-play-services`, `firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`, `work-runtime-ktx`, `hilt-work`, `hilt-work-compiler`, `credentials`, `credentials-play-services-auth`, `googleid`, `coil-network-okhttp`).
  - Remove plugin key (`google-services`).

---

## 5. Verification Method

To verify these findings and the resulting clean build:
1. **File Inspection**:
   - Inspect `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`, `AndroidManifest.xml`, and `app/proguard-rules.pro`.
2. **Build Verification**:
   - Run `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows) to confirm 0 compilation or dependency resolution errors.
   - Run `./gradlew lint` or `./gradlew check` to verify no missing symbol errors or manifest merger failures occur.
3. **Invalidation Conditions**:
   - If any remaining Kotlin class in the offline architecture requires Google Play Tasks or background WorkManager jobs, the corresponding library removal would fail compilation. (Audit confirms none of the retained classes reference these APIs).
