# DISPATCH

## 2026-08-27T11:55:00Z
Received assignment from Orchestrator:
Execute Milestone 1: Remove all unused architecture, Kotlin classes, packages, mock implementations, and UI elements related to legacy Firebase / Cloud Sync.

Scope:
1. Delete files:
   - `firestore.rules`, `storage.rules`, `app/google-services.json`
   - `app/src/main/java/com/docscanner/app/data/remote/` (delete entire directory: `FirebaseAuthService.kt`, `FirestoreService.kt`, `CloudStorageService.kt`, `DocumentSyncWorker.kt`, `SyncManager.kt`)
   - `app/src/main/java/com/docscanner/app/data/repository/AuthRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/data/repository/SyncRepositoryImpl.kt`
   - `app/src/main/java/com/docscanner/app/domain/repository/AuthRepository.kt`
   - `app/src/main/java/com/docscanner/app/domain/repository/SyncRepository.kt`
   - `app/src/main/java/com/docscanner/app/domain/model/UserProfile.kt`
   - `app/src/main/java/com/docscanner/app/presentation/auth/` (delete entire directory: `LoginScreen.kt`, `SignupScreen.kt`, `AuthViewModel.kt`)
   - `app/src/main/java/com/docscanner/app/presentation/storage/` (delete entire directory: `StorageScreen.kt`, `StorageViewModel.kt`)
   - `app/src/main/java/com/docscanner/app/presentation/common/DocumentCard.kt` (duplicate card; verify and retain `presentation/home/DocumentCard.kt`)

2. Adapt files cleanly:
   - `presentation/navigation/Screen.kt`: Remove `Login`, `Signup`, `Storage` screen routes.
   - `presentation/navigation/AppNavigation.kt`: Remove `Login`, `Signup`, `Storage` composables, navigation calls, and unused imports.
   - `presentation/settings/SettingsScreen.kt`: Remove Account section (login/logout/profile) and Cloud section (auto-sync/storage usage).
   - `presentation/settings/SettingsViewModel.kt`: Remove `AuthRepository` dependency, `currentUser`, `toggleAutoSync`, and `signOut()`.
   - `presentation/home/HomeViewModel.kt`: Remove `SyncRepository` dependency and `syncAll()` method.
   - `presentation/common/OnboardingDialog.kt`: Adapt the 4th onboarding page (replace "Cloud Backup" with "100% Offline & Private" or "Secure Local Storage").
   - `di/RepositoryModule.kt`: Remove `@Binds` for `AuthRepository` and `SyncRepository`.
   - `DocScannerApp.kt`: Remove unused Firebase imports.
   - `service/notification/NotificationService.kt`: Remove sync notification channels and sync progress/complete notification methods.
   - `util/Constants.kt`: Remove all cloud / sync constants (`FREE_STORAGE_LIMIT_BYTES`, `SYNC_WORK_NAME`, `SYNC_INTERVAL_HOURS`, `SYNC_CHANNEL_ID`, `FIREBASE_USERS_COLLECTION`, `FIREBASE_DOCUMENTS_COLLECTION`, `FIREBASE_STORAGE_DOCUMENTS`).
   - `data/local/entity/DocumentEntity.kt`: Remove `cloudPdfUrl`, `syncStatus`, and `Index("syncStatus")`.
   - `data/local/dao/DocumentDao.kt`: Remove `getUnsyncedDocuments()` and `updateSyncStatus()`.
   - `domain/model/Document.kt`: Remove `cloudPdfUrl`, `syncStatus`, and `SyncStatus` enum.
   - `domain/model/UserSettings.kt`: Remove `autoSyncEnabled`.
   - `data/repository/SettingsRepositoryImpl.kt`: Remove `AUTO_SYNC_ENABLED` and `autoSyncEnabled` mapping.
   - `data/mapper/EntityMappers.kt`: Remove `cloudPdfUrl` and `syncStatus` mapping.
   - `app/src/main/res/values/strings.xml`: Remove unused auth strings, cloud settings strings, storage strings, and sync content descriptions.
