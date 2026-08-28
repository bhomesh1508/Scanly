# Changes Log — Milestone 1: Remove Unused Architecture & Cloud UI

**Date**: 2026-08-27  
**Milestone**: Milestone 1 (Remove Unused Architecture & Cloud UI)  
**Worker**: `worker_m1`  
**Status**: COMPLETE  

---

## 1. Overview of Changes

Milestone 1 transitions Scanly Android from a legacy hybrid cloud/mock model into a 100% self-contained, offline-only, local-first architecture. All obsolete remote networking, Firebase authentication, Firestore synchronization, Cloud Storage APIs, and associated mock implementations and UI screens were eliminated.

---

## 2. Deleted / Emptied Legacy Files

| File Path | Description of Removal |
|---|---|
| `firestore.rules` | Security rules for Firestore removed. |
| `storage.rules` | Security rules for Firebase Cloud Storage removed. |
| `app/google-services.json` | Placeholder Google Services JSON emptied. |
| `app/src/main/java/com/docscanner/app/data/remote/auth/FirebaseAuthService.kt` | Legacy Firebase Auth & Credential Manager service removed. |
| `app/src/main/java/com/docscanner/app/data/remote/firestore/FirestoreService.kt` | Legacy Firestore document/folder sync service removed. |
| `app/src/main/java/com/docscanner/app/data/remote/storage/CloudStorageService.kt` | Legacy Cloud Storage upload/download service removed. |
| `app/src/main/java/com/docscanner/app/data/remote/sync/DocumentSyncWorker.kt` | Legacy WorkManager sync worker removed. |
| `app/src/main/java/com/docscanner/app/data/remote/sync/SyncManager.kt` | Legacy sync coordinator stub removed. |
| `app/src/main/java/com/docscanner/app/data/repository/AuthRepositoryImpl.kt` | In-memory mock auth repository removed. |
| `app/src/main/java/com/docscanner/app/data/repository/SyncRepositoryImpl.kt` | Legacy WorkManager/SyncManager repository removed. |
| `app/src/main/java/com/docscanner/app/domain/repository/AuthRepository.kt` | Auth repository interface removed. |
| `app/src/main/java/com/docscanner/app/domain/repository/SyncRepository.kt` | Sync repository interface removed. |
| `app/src/main/java/com/docscanner/app/domain/model/UserProfile.kt` | Auth user profile data class removed. |
| `app/src/main/java/com/docscanner/app/presentation/auth/LoginScreen.kt` | Email/Google login UI screen removed. |
| `app/src/main/java/com/docscanner/app/presentation/auth/SignupScreen.kt` | Registration UI screen removed. |
| `app/src/main/java/com/docscanner/app/presentation/auth/AuthViewModel.kt` | Auth ViewModel removed. |
| `app/src/main/java/com/docscanner/app/presentation/storage/StorageScreen.kt` | Cloud storage quota screen removed. |
| `app/src/main/java/com/docscanner/app/presentation/storage/StorageViewModel.kt` | Storage ViewModel removed. |
| `app/src/main/java/com/docscanner/app/presentation/common/DocumentCard.kt` | Obsolete duplicate card with cloud sync icons removed (retained `presentation/home/DocumentCard.kt`). |

---

## 3. Adapted Files and Code Modifications

### 3.1 Domain & Entity Models
- **`domain/model/Document.kt`**:
  - Removed `cloudPdfUrl: String?` and `syncStatus: SyncStatus` properties.
  - Removed `enum class SyncStatus` (`LOCAL_ONLY`, `SYNCING`, `SYNCED`, `SYNC_FAILED`).
- **`domain/model/UserSettings.kt`**:
  - Removed `autoSyncEnabled: Boolean = true` property.
- **`data/local/entity/DocumentEntity.kt`**:
  - Removed `cloudPdfUrl: String?` and `syncStatus: String` fields.
  - Removed `Index("syncStatus")` from `@Entity(indices = [...])`.
- **`data/local/dao/DocumentDao.kt`**:
  - Removed `@Query("SELECT * FROM documents WHERE syncStatus != 'SYNCED'") suspend fun getUnsyncedDocuments()`.
  - Removed `@Query("UPDATE documents SET syncStatus = :status WHERE id = :docId") suspend fun updateSyncStatus(...)`.
- **`data/local/converter/Converters.kt`**:
  - Removed `@TypeConverter fun fromSyncStatus(value: SyncStatus): String`.
  - Removed `@TypeConverter fun toSyncStatus(value: String): SyncStatus`.
  - Removed unused `SyncStatus` import.
- **`data/mapper/EntityMappers.kt`**:
  - Removed `cloudPdfUrl` and `syncStatus` mappings from `toDomain()` and `toEntity()`.
  - Removed unused `SyncStatus` import.
- **`data/repository/SettingsRepositoryImpl.kt`**:
  - Removed `PreferencesKeys.AUTO_SYNC_ENABLED`.
  - Removed `autoSyncEnabled` DataStore preferences read and write mappings.
- **`data/repository/DocumentRepositoryImpl.kt`**:
  - Removed `cloudPdfUrl` and `syncStatus` fields when creating documents or merging documents.
  - Removed unused `SyncStatus` import.

### 3.2 Presentation & Navigation
- **`presentation/navigation/Screen.kt`**:
  - Removed `Screen.Login`, `Screen.Signup`, and `Screen.Storage` route objects.
- **`presentation/navigation/AppNavigation.kt`**:
  - Removed imports of `AuthViewModel`, `LoginScreen`, `SignupScreen`, `StorageScreen`, `StorageViewModel`.
  - Removed `Screen.Login.route` and `Screen.Signup.route` from `hideBottomBarRoutes`.
  - Removed `onNavigateToStorage` and `onNavigateToLogin` callback parameters from `SettingsScreen` invocation.
  - Removed composables for `Screen.Storage.route`, `Screen.Login.route`, and `Screen.Signup.route`.
- **`presentation/settings/SettingsScreen.kt`**:
  - Removed "Account" section (Profile / Sign in / Sign out).
  - Removed "Cloud" section (Auto Sync switch / Storage Usage link).
  - Removed `currentUser` state consumption.
  - Removed `onNavigateToStorage` and `onNavigateToLogin` callbacks.
  - Retained clean offline sections: Appearance, Security, Data, About.
- **`presentation/settings/SettingsViewModel.kt`**:
  - Removed `AuthRepository` constructor injection.
  - Removed `currentUser` StateFlow.
  - Removed `toggleAutoSync(enabled: Boolean)` and `signOut()`.
  - Simplified default state to `UserSettings()`.
- **`presentation/home/HomeViewModel.kt`**:
  - Removed `SyncRepository` constructor injection.
  - Removed `syncAll()` method.
- **`presentation/common/OnboardingDialog.kt`**:
  - Replaced 4th onboarding slide "Cloud Backup" with "100% Offline & Private" (`Icons.Default.Security`, "Your documents stay securely on your device. Complete privacy with local encryption.").

### 3.3 Dependency Injection, Services, & App Entry
- **`di/RepositoryModule.kt`**:
  - Removed `@Binds abstract fun bindAuthRepository(...)` and `@Binds abstract fun bindSyncRepository(...)`.
  - Removed unused imports of `AuthRepository`, `AuthRepositoryImpl`, `SyncRepository`, `SyncRepositoryImpl`.
- **`DocScannerApp.kt`**:
  - Removed unused Firebase imports `FirebaseFirestore` and `FirebaseFirestoreSettings`.
- **`service/notification/NotificationService.kt`**:
  - Removed "sync_channel" NotificationChannel creation and `SYNC_NOTIFICATION_ID`.
  - Removed `showSyncProgressNotification` and `showSyncCompleteNotification` methods.
- **`util/Constants.kt`**:
  - Removed `FREE_STORAGE_LIMIT_BYTES`, `SYNC_WORK_NAME`, `SYNC_INTERVAL_HOURS`, `SYNC_CHANNEL_ID`.
  - Removed `FIREBASE_USERS_COLLECTION`, `FIREBASE_DOCUMENTS_COLLECTION`, `FIREBASE_STORAGE_DOCUMENTS`.
- **`app/src/main/res/values/strings.xml`**:
  - Removed unused `<!-- Auth -->` string resources.
  - Removed `settings_account`, `settings_cloud`, `settings_auto_sync`, `settings_auto_sync_subtitle`, `settings_storage`.
  - Removed unused `<!-- Storage -->` string resources.
  - Removed cloud sync content descriptions (`cd_sync_status_synced`, etc.).
  - Updated onboarding slide 4 strings to `onboarding_offline_title` and `onboarding_offline_subtitle`.

---

## 4. Verification Summary

- Verified all 19 target files have been emptied/removed.
- Verified all 19 adapted source and resource files have 0 dangling references to removed symbols.
- Pure offline data contracts and UI flows are established and ready for Milestone 2 (Dependency & Build Configuration Cleanup).
