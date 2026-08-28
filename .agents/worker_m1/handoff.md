# Handoff Report — Milestone 1: Remove Unused Architecture & Cloud UI

**Agent**: `worker_m1`  
**Milestone**: Milestone 1 (Remove Unused Architecture & Cloud UI)  
**Date**: 2026-08-27  
**Type**: Hard (Task Complete)  

---

## 1. Observation

Direct code observations from inspecting the codebase prior to and after refactoring:

1. **Legacy Firebase & Remote Files**:
   - `firestore.rules` (lines 1–28) defined Firestore security rules for collections `users` and `documents`.
   - `storage.rules` (lines 1–24) defined Firebase Cloud Storage rules for `documents/{userId}/{docId}`.
   - `app/google-services.json` contained placeholder client definitions.
   - `data/remote/auth/FirebaseAuthService.kt`, `data/remote/firestore/FirestoreService.kt`, `data/remote/storage/CloudStorageService.kt`, `data/remote/sync/DocumentSyncWorker.kt`, and `data/remote/sync/SyncManager.kt` contained Firebase/WorkManager implementations.
   - `data/repository/AuthRepositoryImpl.kt` and `data/repository/SyncRepositoryImpl.kt` implemented `AuthRepository` and `SyncRepository`.
   - `domain/repository/AuthRepository.kt`, `domain/repository/SyncRepository.kt`, and `domain/model/UserProfile.kt` defined the remote domain contracts.
   - `presentation/auth/` (`LoginScreen.kt`, `SignupScreen.kt`, `AuthViewModel.kt`) and `presentation/storage/` (`StorageScreen.kt`, `StorageViewModel.kt`) contained UI screens for authentication and cloud storage quota.
   - `presentation/common/DocumentCard.kt` contained a duplicate `DocumentCard` with local mock models (`enum class SyncStatus`, `data class Document(...)`) and `SyncIcon`.

2. **Coupled Local Components**:
   - `Document.kt`: Lines 27-28 and 39-41 contained `cloudPdfUrl`, `syncStatus`, and `enum class SyncStatus`.
   - `UserSettings.kt`: Line 23 contained `val autoSyncEnabled: Boolean = true`.
   - `DocumentEntity.kt`: Lines 14, 24, 25 contained `Index("syncStatus")`, `cloudPdfUrl`, and `syncStatus`.
   - `DocumentDao.kt`: Lines 31-32 and 46-47 contained `getUnsyncedDocuments()` and `updateSyncStatus()`.
   - `Converters.kt`: Lines 5, 10-22 contained `SyncStatus` Room TypeConverters.
   - `EntityMappers.kt`: Lines 20-21 and 38-39 mapped `cloudPdfUrl` and `syncStatus`.
   - `SettingsRepositoryImpl.kt`: Lines 37, 50, 64 contained `AUTO_SYNC_ENABLED` preference key and mappings.
   - `DocumentRepositoryImpl.kt`: Lines 10, 61, 62, 138, 139 contained `SyncStatus` imports and instantiation parameters.
   - `Screen.kt`: Lines 18, 20, 21 contained `Storage`, `Login`, and `Signup` routes.
   - `AppNavigation.kt`: Lines 21-23, 39-40, 56-62, 194-202, 204-255 referenced auth/storage screens and ViewModels.
   - `SettingsScreen.kt`: Lines 36-55 (Account section) and 83-95 (Cloud section) referenced auth and sync.
   - `SettingsViewModel.kt`: Injected `AuthRepository`, exposed `currentUser`, and defined `toggleAutoSync` / `signOut`.
   - `HomeViewModel.kt`: Injected `SyncRepository` and defined `syncAll()`.
   - `OnboardingDialog.kt`: Page 4 referenced "Cloud Backup" with `Icons.Default.CloudUpload`.
   - `RepositoryModule.kt`: Bound `AuthRepository` and `SyncRepository`.
   - `DocScannerApp.kt`: Contained unused Firebase Firestore imports.
   - `NotificationService.kt`: Contained `sync_channel` and `showSyncProgressNotification` / `showSyncCompleteNotification`.
   - `Constants.kt`: Contained `FREE_STORAGE_LIMIT_BYTES`, `SYNC_WORK_NAME`, `SYNC_INTERVAL_HOURS`, `SYNC_CHANNEL_ID`, `FIREBASE_USERS_COLLECTION`, `FIREBASE_DOCUMENTS_COLLECTION`, `FIREBASE_STORAGE_DOCUMENTS`.
   - `strings.xml`: Contained `<!-- Auth -->`, `<!-- Storage -->`, cloud settings, and sync content descriptions.

---

## 2. Logic Chain

1. **Eliminating Dead Remote Layers**:
   - Because the target architecture for Scanly Android is a 100% offline, self-contained local scanner app without cloud backend requirements (Observation 1), the entire `data/remote/` package, `AuthRepositoryImpl`, `SyncRepositoryImpl`, `AuthRepository`, `SyncRepository`, `UserProfile`, `presentation/auth/`, `presentation/storage/`, `firestore.rules`, `storage.rules`, and `app/google-services.json` were removed.

2. **Cleaning Local Persistence & Models**:
   - With `SyncStatus` and cloud storage removed, `DocumentEntity`, `DocumentDao`, `Converters.kt`, `EntityMappers.kt`, `DocumentRepositoryImpl.kt`, and `Document.kt` were simplified to eliminate `cloudPdfUrl`, `syncStatus`, and unsynced query methods (Observation 2).
   - `UserSettings` and `SettingsRepositoryImpl` were updated to remove `autoSyncEnabled`.

3. **Pruning Presentation & Navigation**:
   - Removing the routes and composables from `Screen.kt` and `AppNavigation.kt` prevents navigating to non-existent auth and storage destinations.
   - Removing the Account and Cloud sections from `SettingsScreen.kt` and `SettingsViewModel.kt` presents a clean offline settings surface (Appearance, Security, Data, About).
   - Removing `SyncRepository` and `syncAll()` from `HomeViewModel.kt` keeps document management strictly local.
   - Updating `OnboardingDialog.kt` to showcase "100% Offline & Private" with `Icons.Default.Security` maintains a 4-page onboarding flow aligned with the app's privacy-first ethos.

4. **Pruning Services, DI, & Resources**:
   - Removing `AuthRepository` and `SyncRepository` from `RepositoryModule.kt` prevents Hilt dependency graph failure.
   - Removing sync notifications from `NotificationService.kt` and constants from `Constants.kt` eliminates dead code.
   - Removing unused strings from `strings.xml` ensures Android resource consistency.

---

## 3. Caveats

- **Gradle Build Dependencies**: The removal of dependencies from `app/build.gradle.kts` and `gradle/libs.versions.toml`, as well as `AndroidManifest.xml` provider cleanup, is scoped to Milestone 2 per the Project Plan (`PROJECT.md`).
- **Unit Tests**: Comprehensive unit tests for mappers, services, and repositories are scheduled for Milestone 3 per `PROJECT.md`.
- No other caveats.

---

## 4. Conclusion

Milestone 1 is completely executed:
- All 19 legacy cloud/sync files have been removed.
- All 19 remaining/dependent Kotlin source, configuration, and XML resource files have been cleanly adapted.
- Zero references to `data/remote`, `AuthRepository`, `SyncRepository`, `UserProfile`, `SyncStatus`, `LoginScreen`, `SignupScreen`, or `StorageScreen` exist in the codebase.
- The project is in a clean, consistent state ready for Milestone 2 (Dependency & Build Configuration Cleanup).

---

## 5. Verification Method

To independently verify Milestone 1 changes:

1. **Verify Deleted Files**:
   Inspect that the following files contain only deletion markers:
   - `firestore.rules`
   - `storage.rules`
   - `app/google-services.json`
   - `app/src/main/java/com/docscanner/app/data/remote/auth/FirebaseAuthService.kt`
   - `app/src/main/java/com/docscanner/app/data/remote/firestore/FirestoreService.kt`
   - `app/src/main/java/com/docscanner/app/data/remote/storage/CloudStorageService.kt`
   - `app/src/main/java/com/docscanner/app/data/remote/sync/DocumentSyncWorker.kt`
   - `app/src/main/java/com/docscanner/app/data/remote/sync/SyncManager.kt`
   - `app/src/main/java/com/docscanner/app/data/repository/AuthRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/data/repository/SyncRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/domain/repository/AuthRepository.kt`
   - `app/src/main/java/com/docscanner/app/domain/repository/SyncRepository.kt`
   - `app/src/main/java/com/docscanner/app/domain/model/UserProfile.kt`
   - `app/src/main/java/com/docscanner/app/presentation/auth/LoginScreen.kt`
   - `app/src/main/java/com/docscanner/app/presentation/auth/SignupScreen.kt`
   - `app/src/main/java/com/docscanner/app/presentation/auth/AuthViewModel.kt`
   - `app/src/main/java/com/docscanner/app/presentation/storage/StorageScreen.kt`
   - `app/src/main/java/com/docscanner/app/presentation/storage/StorageViewModel.kt`
   - `app/src/main/java/com/docscanner/app/presentation/common/DocumentCard.kt`

2. **Verify Adapted Files**:
   Inspect that the following files have clean implementations without cloud or sync references:
   - `app/src/main/java/com/docscanner/app/domain/model/Document.kt`
   - `app/src/main/java/com/docscanner/app/domain/model/UserSettings.kt`
   - `app/src/main/java/com/docscanner/app/data/local/entity/DocumentEntity.kt`
   - `app/src/main/java/com/docscanner/app/data/local/dao/DocumentDao.kt`
   - `app/src/main/java/com/docscanner/app/data/local/converter/Converters.kt`
   - `app/src/main/java/com/docscanner/app/data/mapper/EntityMappers.kt`
   - `app/src/main/java/com/docscanner/app/data/repository/SettingsRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/presentation/navigation/Screen.kt`
   - `app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt`
   - `app/src/main/java/com/docscanner/app/presentation/settings/SettingsScreen.kt`
   - `app/src/main/java/com/docscanner/app/presentation/settings/SettingsViewModel.kt`
   - `app/src/main/java/com/docscanner/app/presentation/home/HomeViewModel.kt`
   - `app/src/main/java/com/docscanner/app/presentation/common/OnboardingDialog.kt`
   - `app/src/main/java/com/docscanner/app/di/RepositoryModule.kt`
   - `app/src/main/java/com/docscanner/app/DocScannerApp.kt`
   - `app/src/main/java/com/docscanner/app/service/notification/NotificationService.kt`
   - `app/src/main/java/com/docscanner/app/util/Constants.kt`
   - `app/src/main/res/values/strings.xml`
