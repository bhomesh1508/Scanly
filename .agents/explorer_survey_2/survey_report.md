# Scanly Android - Dependency & Build Configuration Audit Report

**Date**: 2026-08-27  
**Auditor**: Explorer 2 (Dependency & Build Configuration Auditor)  
**Project Root**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android`  
**Working Directory**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_survey_2`  

---

## 1. Executive Summary

This report delivers a thorough audit of all Gradle build files, Gradle plugins, and every dependency declared in the Scanly Android codebase. The primary objective is to identify all legacy cloud, Firebase, authentication, and dead/unused dependencies to transform Scanly into a lean, 100% self-contained offline application.

### Key Audit Findings:
1. **Total Declared Libraries in Catalog**: 34 library definitions (and BOMs).
2. **Total Dependencies Declared in `app/build.gradle.kts`**: 32 dependency statements.
3. **Dead / Cloud Dependencies to Eliminate**: **12 dependencies** + **1 Gradle plugin** + **2 AndroidManifest provider blocks** + **1 Proguard rule block**.
   - **Firebase Stack**: `firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics` (5 items).
   - **Google Identity / Credential Manager**: `credentials`, `credentials-play-services-auth`, `googleid` (3 items).
   - **Play Services Coroutines**: `coroutines-play-services` (1 item).
   - **WorkManager & Hilt Work**: `work-runtime-ktx`, `hilt-work`, `hilt-work-compiler` (3 items, only used for cloud sync background worker).
   - **Dead Network Engine**: `coil-network-okhttp` (1 item, app is 100% offline, Coil local file loader is sufficient).
4. **Plugins to Remove**: `com.google.gms.google-services` (`google-services`) in root `build.gradle.kts` and `libs.versions.toml`.
5. **No Existing Unit/Instrumentation Tests**: `src/test` and `src/androidTest` directories do not exist, though test libraries (`junit`, `mockk`, `turbine`, `room-testing`, `compose-ui-test-junit4`, `compose-ui-test-manifest`) are declared in the build script.

---

## 2. Gradle Files Inventory & Configuration Analysis

### 2.1 File Inventory

| File Path | Role / Description |
|---|---|
| `settings.gradle.kts` | Repository resolution management and module inclusion (`:app`). |
| `build.gradle.kts` (root) | Top-level plugin management with `apply false`. |
| `gradle.properties` | JVM args (`-Xmx4096m`), AndroidX, non-transitive R classes, KSP fastInit. |
| `gradle/wrapper/gradle-wrapper.properties` | Gradle 9.7.1 distribution binary wrapper. |
| `gradle/libs.versions.toml` | Centralized version catalog for versions, libraries, and plugins. |
| `app/build.gradle.kts` | Application module build configuration, SDK targets, compile options, and dependency declarations. |
| `app/proguard-rules.pro` | Proguard/R8 shrinking and obfuscation rules. |

---

### 2.2 Detailed Build Configuration

- **Gradle Wrapper**: Gradle `9.7.1` (`https://services.gradle.org/distributions/gradle-9.7.1-bin.zip`).
- **Android Gradle Plugin (AGP)**: `9.3.1`.
- **Kotlin Version**: `2.4.10`.
- **KSP Version**: `2.3.11`.
- **Java / JVM Compatibility**:
  - `sourceCompatibility = JavaVersion.VERSION_21`
  - `targetCompatibility = JavaVersion.VERSION_21`
- **Android SDK Levels**:
  - `compileSdk = 37`
  - `minSdk = 24`
  - `targetSdk = 34`
- **Application ID & Namespace**: `com.docscanner.app`
- **Version**: Code `1`, Name `"1.0.0"`
- **Build Features**: `compose = true` (using Kotlin Compose Compiler plugin `2.4.10`)
- **KSP Arguments**:
  - `room.schemaLocation = "$projectDir/schemas"`
  - `room.incremental = "true"`
  - `room.expandProjection = "true"`

---

## 3. Plugins Audit

| Plugin Catalog Alias | Plugin ID | Configured Location | Status | Action / Rationale |
|---|---|---|---|---|
| `libs.plugins.android.application` | `com.android.application` | Root & `app` | **Active / Required** | Core Android Application plugin. |
| `libs.plugins.kotlin.android` | `org.jetbrains.kotlin.android` | `libs.versions.toml` | Inactive (alias not applied) | Keep in catalog or clean up. |
| `libs.plugins.compose.compiler` | `org.jetbrains.kotlin.plugin.compose` | Root & `app` | **Active / Required** | Kotlin 2.x Compose compiler plugin. |
| `libs.plugins.hilt` | `com.google.dagger.hilt.android` | Root & `app` | **Active / Required** | Dependency injection plugin. |
| `libs.plugins.ksp` | `com.google.devtools.ksp` | Root & `app` | **Active / Required** | Annotation processor for Room and Hilt. |
| `libs.plugins.google.services` | `com.google.gms.google-services` | Root (`apply false`) & catalog | **DEAD / OBSOLETE** | **REMOVE**. No Google Services/Firebase in offline app. |

---

## 4. Comprehensive Dependency-by-Dependency Audit

Every dependency declared in `app/build.gradle.kts` and `gradle/libs.versions.toml` was traced across all source files (`.kt`), AndroidManifest, resources, and configuration files.

| # | Catalog Reference | Maven Coordinates / BOM | Declared In | Actual Code Usages / References | Status | Action |
|---|---|---|---|---|---|---|
| 1 | `libs.core.ktx` | `androidx.core:core-ktx:1.16.0` | `implementation` | `FileProvider`, `NotificationCompat`, `ContextCompat` across app | **Active Local** | **Keep** |
| 2 | `libs.lifecycle.runtime.ktx` | `androidx.lifecycle:lifecycle-runtime-ktx:2.9.1` | `implementation` | Coroutines lifecycle runtime, Compose lifecycle integration | **Active Local** | **Keep** |
| 3 | `libs.lifecycle.viewmodel.compose` | `androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1` | `implementation` | ViewModels across UI layer (`viewModelScope`, Compose integration) | **Active Local** | **Keep** |
| 4 | `libs.coroutines.android` | `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2` | `implementation` | `Dispatchers.Main`, flows, stateIn, UI coroutines | **Active Local** | **Keep** |
| 5 | `libs.coroutines.play.services` | `org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2` | `implementation` | `FirebaseAuthService.kt`, `FirestoreService.kt`, `CloudStorageService.kt` (`.await()` on Tasks) | **Dead Cloud** | **Remove** |
| 6 | `libs.compose.bom` | `androidx.compose:compose-bom:2026.08.00` | `platform` | Compose BOM for all Compose UI dependencies | **Active Local** | **Keep** |
| 7 | `libs.activity.compose` | `androidx.activity:activity-compose:1.10.1` | `implementation` | `MainActivity.kt` (`setContent`, `enableEdgeToEdge`), `ScannerScreen.kt` | **Active Local** | **Keep** |
| 8 | `libs.compose.ui` | `androidx.compose.ui:ui` | `implementation` | Core UI elements, Modifiers, Canvas, layout | **Active Local** | **Keep** |
| 9 | `libs.compose.ui.graphics` | `androidx.compose.ui:ui-graphics` | `implementation` | Colors, graphics drawing | **Active Local** | **Keep** |
| 10 | `libs.compose.ui.tooling.preview` | `androidx.compose.ui:ui-tooling-preview` | `implementation` | Preview annotations | **Active Local** | **Keep** |
| 11 | `libs.compose.material3` | `androidx.compose.material3:material3` | `implementation` | Material3 components (`Scaffold`, `TopAppBar`, `Card`, etc.) | **Active Local** | **Keep** |
| 12 | `libs.compose.foundation` | `androidx.compose.foundation:foundation` | `implementation` | Scroll states, clicks, lazy columns/grids | **Active Local** | **Keep** |
| 13 | `libs.compose.animation` | `androidx.compose.animation:animation` | `implementation` | UI animations and transitions | **Active Local** | **Keep** |
| 14 | `libs.compose.material.icons.extended` | `androidx.compose.material:material-icons-extended` | `implementation` | Icons used in UI (`Icons.Default.Lock`, `DocumentScanner`, `Folder`, etc.) | **Active Local** | **Keep** |
| 15 | `libs.navigation.compose` | `androidx.navigation:navigation-compose:2.9.0` | `implementation` | `AppNavigation.kt` (`NavHost`, `composable`, `rememberNavController`) | **Active Local** | **Keep** |
| 16 | `libs.hilt.android` | `com.google.dagger:hilt-android:2.60.1` | `implementation` | Dependency Injection (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@Inject`, `@Module`) | **Active Local** | **Keep** |
| 17 | `libs.hilt.compiler` | `com.google.dagger:hilt-compiler:2.60.1` | `ksp` | KSP code generator for Hilt | **Active Local** | **Keep** |
| 18 | `libs.hilt.navigation.compose` | `androidx.hilt:hilt-navigation-compose:1.4.0` | `implementation` | `hiltViewModel()` calls across Compose screens | **Active Local** | **Keep** |
| 19 | `libs.hilt.work` | `androidx.hilt:hilt-work:1.2.0` | `implementation` | `@HiltWorker` in `DocumentSyncWorker.kt` | **Dead Cloud** | **Remove** |
| 20 | `libs.hilt.work.compiler` | `androidx.hilt:hilt-compiler:1.2.0` | `ksp` | Hilt worker KSP annotation processor | **Dead Cloud** | **Remove** |
| 21 | `libs.room.runtime` | `androidx.room:room-runtime:2.7.2` | `implementation` | Local SQLite Room Database runtime (`AppDatabase.kt`) | **Active Local** | **Keep** |
| 22 | `libs.room.ktx` | `androidx.room:room-ktx:2.7.2` | `implementation` | Room Coroutines & Flow DAO extensions | **Active Local** | **Keep** |
| 23 | `libs.room.compiler` | `androidx.room:room-compiler:2.7.2` | `ksp` | KSP code generator for Room DAOs and DB | **Active Local** | **Keep** |
| 24 | `libs.room.testing` | `androidx.room:room-testing:2.7.2` | `testImplementation` | No unit tests currently exist | **Unused Test** | **Remove / Retain for future tests** |
| 25 | `libs.firebase.bom` | `com.google.firebase:firebase-bom:34.18.0` | `platform` | Firebase BOM | **Dead Cloud** | **Remove** |
| 26 | `libs.firebase.auth` | `com.google.firebase:firebase-auth` | `implementation` | `FirebaseAuthService.kt` | **Dead Cloud** | **Remove** |
| 27 | `libs.firebase.firestore` | `com.google.firebase:firebase-firestore` | `implementation` | `FirestoreService.kt`, unused imports in `DocScannerApp.kt` | **Dead Cloud** | **Remove** |
| 28 | `libs.firebase.storage` | `com.google.firebase:firebase-storage` | `implementation` | `CloudStorageService.kt` | **Dead Cloud** | **Remove** |
| 29 | `libs.firebase.analytics` | `com.google.firebase:firebase-analytics` | `implementation` | Not imported or used anywhere in codebase (0 usages) | **Dead Cloud / Unused** | **Remove** |
| 30 | `libs.mlkit.docscanner` | `com.google.android.gms:play-services-mlkit-document-scanner:16.0.0` | `implementation` | `ScannerScreen.kt`, `ScannerViewModel.kt` (Core scanning) | **Active Local** | **Keep** |
| 31 | `libs.mlkit.text.recognition` | `com.google.mlkit:text-recognition:16.0.1` | `implementation` | `ViewerViewModel.kt` (OCR text recognition) | **Active Local** | **Keep** |
| 32 | `libs.work.runtime.ktx` | `androidx.work:work-runtime-ktx:2.11.2` | `implementation` | `DocumentSyncWorker.kt`, `SyncRepositoryImpl.kt` (Cloud sync only) | **Dead Cloud** | **Remove** |
| 33 | `libs.biometric` | `androidx.biometric:biometric:1.1.0` | `implementation` | `AppLockGate.kt` (Local biometric app unlock) | **Active Local** | **Keep** |
| 34 | `libs.security.crypto` | `androidx.security:security-crypto:1.1.0` | `implementation` | `EncryptionService.kt` (`EncryptedFile`, `MasterKey` for local file encryption) | **Active Local** | **Keep** |
| 35 | `libs.credentials` | `androidx.credentials:credentials:1.6.0` | `implementation` | `FirebaseAuthService.kt` (Google Sign-In CredentialManager) | **Dead Cloud** | **Remove** |
| 36 | `libs.credentials.play.services.auth` | `androidx.credentials:credentials-play-services-auth:1.6.0` | `implementation` | `FirebaseAuthService.kt` (Play Services Auth Provider) | **Dead Cloud** | **Remove** |
| 37 | `libs.googleid` | `com.google.android.libraries.identity.googleid:googleid:1.2.0` | `implementation` | `FirebaseAuthService.kt` (`GetGoogleIdOption`, `GoogleIdTokenCredential`) | **Dead Cloud** | **Remove** |
| 38 | `libs.coil.compose` | `io.coil-kt.coil3:coil-compose:3.2.0` | `implementation` | `DocumentCard.kt` (`AsyncImage` for local thumbnail display) | **Active Local** | **Keep** |
| 39 | `libs.coil.network.okhttp` | `io.coil-kt.coil3:coil-network-okhttp:3.2.0` | `implementation` | Network HTTP engine for Coil (not needed for local images) | **Dead Unused** | **Remove** |
| 40 | `libs.datastore.preferences` | `androidx.datastore:datastore-preferences:1.1.4` | `implementation` | `SettingsRepositoryImpl.kt`, `AppModule.kt` (Local user preferences) | **Active Local** | **Keep** |
| 41 | `libs.junit` | `junit:junit:4.13.2` | `testImplementation` | Test runner (0 test files currently exist) | **Unused Test** | **Retain for test harness** |
| 42 | `libs.mockk` | `io.mockk:mockk:1.14.2` | `testImplementation` | Mocking library (0 test files currently exist) | **Unused Test** | **Retain for test harness** |
| 43 | `libs.turbine` | `app.cash.turbine:turbine:1.3.0` | `testImplementation` | Flow testing (0 test files currently exist) | **Unused Test** | **Retain for test harness** |
| 44 | `libs.compose.ui.test.junit4` | `androidx.compose.ui:ui-test-junit4` | `androidTestImplementation` | Compose UI test runner (0 test files currently exist) | **Unused Test** | **Retain for test harness** |
| 45 | `libs.compose.ui.tooling` | `androidx.compose.ui:ui-tooling` | `debugImplementation` | Compose preview / inspector tooling | **Active Local** | **Keep** |
| 46 | `libs.compose.ui.test.manifest` | `androidx.compose.ui:ui-test-manifest` | `debugImplementation` | Compose test manifest for previews and test activity | **Active Local** | **Keep** |

---

## 5. Detailed Breakdown of Dead / Cloud Dependencies to Remove

### Category A: Firebase & Cloud Services Stack (5 libraries + 1 plugin)
These dependencies are tied directly to Firebase Auth, Cloud Firestore, Firebase Cloud Storage, and Firebase Analytics. Since Scanly is transitioning to a 100% offline app, all of these must be removed:
1. `libs.firebase.bom` (`com.google.firebase:firebase-bom`)
2. `libs.firebase.auth` (`com.google.firebase:firebase-auth`)
3. `libs.firebase.firestore` (`com.google.firebase:firebase-firestore`)
4. `libs.firebase.storage` (`com.google.firebase:firebase-storage`)
5. `libs.firebase.analytics` (`com.google.firebase:firebase-analytics`)
6. Plugin: `alias(libs.plugins.google.services)` (`com.google.gms.google-services`)

### Category B: Google Identity & Credential Manager Stack (3 libraries + 1 coroutines adapter)
These libraries were introduced solely to facilitate Google Sign-In into Firebase:
1. `libs.credentials` (`androidx.credentials:credentials`)
2. `libs.credentials.play.services.auth` (`androidx.credentials:credentials-play-services-auth`)
3. `libs.googleid` (`com.google.android.libraries.identity.googleid:googleid`)
4. `libs.coroutines.play.services` (`org.jetbrains.kotlinx:kotlinx-coroutines-play-services` — used only for `.await()` on Firebase/Play Tasks)

### Category C: Background Sync & WorkManager Stack (3 libraries)
WorkManager and Hilt Work in this app exist solely for `DocumentSyncWorker.kt` (syncing documents to Cloud Storage & Firestore in the background). In an offline-only app with local Room database and local storage, no background sync worker is needed:
1. `libs.work.runtime.ktx` (`androidx.work:work-runtime-ktx`)
2. `libs.hilt.work` (`androidx.hilt:hilt-work`)
3. `libs.hilt.work.compiler` (`androidx.hilt:hilt-compiler:1.2.0`)

### Category D: Dead Network Engines (1 library)
1. `libs.coil.network.okhttp` (`io.coil-kt.coil3:coil-network-okhttp`)
   - Coil 3 modular architecture splits core image loading from network fetching. In an offline app where all images are local files / content URIs, OkHttp network transport is completely unused.

---

## 6. Manifest & Proguard Cleanup Requirements

Pruning dependencies necessitates corresponding cleanups in AndroidManifest.xml and Proguard rules to prevent build or runtime issues:

### 6.1 `AndroidManifest.xml` Cleanups:
1. **Remove Firebase Init Provider override**:
   ```xml
   <!-- REMOVE: -->
   <provider
       android:name="com.google.firebase.provider.FirebaseInitProvider"
       android:authorities="${applicationId}.firebaseinitprovider"
       tools:node="remove" />
   ```
2. **Remove WorkManager Initialization Provider override**:
   ```xml
   <!-- REMOVE: -->
   <provider
       android:name="androidx.startup.InitializationProvider"
       android:authorities="${applicationId}.androidx-startup"
       tools:node="remove">
       <meta-data
           android:name="androidx.work.WorkManagerInitializer"
           android:value="androidx.startup"
           tools:node="remove" />
   </provider>
   ```
3. **Unused Permissions Audit**:
   - `android.permission.INTERNET` (Can be removed or kept minimal; offline app does not need internet).
   - `android.permission.ACCESS_NETWORK_STATE` (Not needed in offline app).
   - `android.permission.RECEIVE_BOOT_COMPLETED` (Not needed without WorkManager periodic sync).
   - `android.permission.FOREGROUND_SERVICE` (Not needed without sync foreground worker).

### 6.2 `app/proguard-rules.pro` Cleanups:
1. **Remove Firebase Proguard rules**:
   ```proguard
   # REMOVE:
   # Firebase
   -keepattributes Signature
   -keepclassmembers class * {
     @com.google.firebase.database.IgnoreExtraProperties *;
   }
   ```

---

## 7. Version Catalog (`gradle/libs.versions.toml`) Cleanup Plan

### Versions to Remove:
- `firebaseBom`
- `workManager`
- `credentials`
- `googleid`
- `hiltWork`
- `googleServices`

### Libraries to Remove from `[libraries]`:
- `coroutines-play-services`
- `firebase-bom`
- `firebase-auth`
- `firebase-firestore`
- `firebase-storage`
- `firebase-analytics`
- `work-runtime-ktx`
- `hilt-work`
- `hilt-work-compiler`
- `credentials`
- `credentials-play-services-auth`
- `googleid`
- `coil-network-okhttp`

### Plugins to Remove from `[plugins]`:
- `google-services`

---

## 8. Summary of Retained Active Local Stack

The proposed clean, offline-only Scanly Android build configuration will consist exclusively of:

1. **Android & Kotlin Toolchain**: AGP 9.3.1, Kotlin 2.4.10, KSP 2.3.11, Java 21.
2. **Jetpack Compose UI**: Compose BOM 2026.08.00, Material3, Foundation, Animation, Icons Extended, Navigation Compose.
3. **Architecture & DI**: Hilt 2.60.1, Hilt Navigation Compose.
4. **Local Database & Storage**: Room 2.7.2 (Runtime, KTX, KSP Compiler), DataStore Preferences 1.1.4.
5. **Security & Cryptography**: AndroidX Biometric 1.1.0 (App Lock), AndroidX Security Crypto 1.1.0 (Encrypted Files / AES-GCM).
6. **Local ML Kit & Document Processing**: Play Services ML Kit Document Scanner 16.0.0, ML Kit Text Recognition 16.0.1, Android native `PdfDocument` / `Bitmap` image filtering.
7. **Local Image Loading**: Coil Compose 3.2.0 (`io.coil-kt.coil3:coil-compose`).
8. **Testing Harness**: JUnit 4.13.2, MockK 1.14.2, Turbine 1.3.0, Compose UI Test JUnit4.
