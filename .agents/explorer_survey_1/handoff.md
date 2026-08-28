# Handoff Report — Explorer Survey 1 (Architecture & Firebase/Cloud Sync Mapping)

**Author**: Explorer Subagent 1  
**Target Path**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_survey_1\handoff.md`  
**Report Reference**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_survey_1\survey_report.md`  
**Date**: 2026-08-27  

---

## 1. Observation

A full walkthrough of all 77 files in the repository at `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android` was conducted using `find_by_name`, `list_dir`, and `view_file`.

Specific direct observations with exact paths and lines include:

1. **Configuration & Rules Files**:
   - `firestore.rules` (Lines 1–40): Defines Firestore access rules for `/users/{userId}/documents/{docId}`.
   - `storage.rules` (Lines 1–33): Defines Firebase Storage rules for `/users/{userId}/...`.
   - `app/google-services.json` (Lines 1–30): Contains mock project `docscanner-placeholder` configuration.

2. **Remote Data Package (`data/remote/`)**:
   - `app/src/main/java/com/docscanner/app/data/remote/auth/FirebaseAuthService.kt` (Lines 1–124): Direct imports of `com.google.firebase.auth.FirebaseAuth`, `GoogleAuthProvider`, `androidx.credentials.CredentialManager`, and `com.google.android.libraries.identity.googleid.GetGoogleIdOption`.
   - `app/src/main/java/com/docscanner/app/data/remote/firestore/FirestoreService.kt` (Lines 1–159): Direct imports of `com.google.firebase.firestore.FirebaseFirestore` and collection methods for `users`, `documents`, `folders`, `settings`.
   - `app/src/main/java/com/docscanner/app/data/remote/storage/CloudStorageService.kt` (Lines 1–120): Direct imports of `com.google.firebase.storage.FirebaseStorage` for upload/download.
   - `app/src/main/java/com/docscanner/app/data/remote/sync/DocumentSyncWorker.kt` (Lines 1–45): `@HiltWorker` invoking `SyncManager.syncAll()` and `FirebaseAuthService.getCurrentUser()`.
   - `app/src/main/java/com/docscanner/app/data/remote/sync/SyncManager.kt` (Lines 1–41): Injects `DocumentDao`, `PageDao`, and updates sync status.

3. **Repositories & Models**:
   - `data/repository/AuthRepositoryImpl.kt` (Lines 1–53): Implements `AuthRepository` with in-memory `_currentUser` and mock `UserProfile`.
   - `data/repository/SyncRepositoryImpl.kt` (Lines 1–74): Enqueues WorkManager periodic/immediate requests for `DocumentSyncWorker`.
   - `domain/repository/AuthRepository.kt` (Lines 1–48) & `domain/repository/SyncRepository.kt` (Lines 1–36): Interfaces declaring auth and sync contracts.
   - `domain/model/UserProfile.kt` (Lines 1–23): Contains cloud quota metrics (`cloudStorageUsedBytes`, `cloudStorageLimitBytes`).
   - `domain/model/UserSettings.kt` (Line 23): Property `val autoSyncEnabled: Boolean = true`.
   - `domain/model/Document.kt` (Lines 27, 28, 39–41): Contains `cloudPdfUrl: String?`, `syncStatus: SyncStatus`, and `enum class SyncStatus`.
   - `data/local/entity/DocumentEntity.kt` (Lines 14, 24, 25) & `data/local/dao/DocumentDao.kt` (Lines 31–32, 46–47): Fields and queries for `syncStatus` and `cloudPdfUrl`.

4. **UI & Navigation**:
   - `presentation/auth/` (3 files: `AuthViewModel.kt`, `LoginScreen.kt`, `SignupScreen.kt`): Full UI for email/password and Google login/registration.
   - `presentation/storage/` (2 files: `StorageScreen.kt`, `StorageViewModel.kt`): UI for cloud storage quota gauge and upgrade plan button.
   - `presentation/settings/SettingsScreen.kt` (Lines 36–55, 83–95) & `SettingsViewModel.kt` (Lines 24, 34, 57, 65): "Account" section (showing email/user profile and sign out) and "Cloud" section (Auto Sync toggle and storage usage navigation).
   - `presentation/home/HomeViewModel.kt` (Lines 19, 78–82): Injects `SyncRepository` and provides `syncAll()`.
   - `presentation/navigation/Screen.kt` (Lines 18, 20, 21) & `AppNavigation.kt` (Lines 60–62, 198, 205–211, 223–255): Routes for `Login`, `Signup`, and `Storage`.
   - `presentation/common/DocumentCard.kt` (Lines 26, 137–158): Unused duplicate card with `SyncIcon` (`CloudDone`, `CloudSync`, `PhoneAndroid`, `Error`).
   - `presentation/common/OnboardingDialog.kt` (Line 51): Onboarding page for "Cloud Backup".

5. **Dependency Injection**:
   - `di/RepositoryModule.kt` (Lines 28–35): Binds `AuthRepositoryImpl` to `AuthRepository` and `SyncRepositoryImpl` to `SyncRepository`.

6. **Build & Manifest**:
   - `gradle/libs.versions.toml` & `app/build.gradle.kts`: Includes `firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`, `work-runtime-ktx`, `hilt-work`, `credentials`, `credentials-play-services-auth`, `googleid`, and plugin `google-services`.
   - `AndroidManifest.xml` (Lines 6, 7, 14, 15, 53–68): Contains internet/network/boot/foreground permissions, `WorkManagerInitializer` removal, and `FirebaseInitProvider` removal.

---

## 2. Logic Chain

1. **Self-Contained Offline Goal**: The requirement is to make Scanly completely offline-only, remove all unused code, dead dependencies, and legacy cloud/Firebase files while preserving scanning, local storage, image filtering, OCR, and PDF generation.
2. **Identification of Isolated Remote Layers**: 
   - `data/remote/` is completely dedicated to Firebase and WorkManager sync.
   - `presentation/auth/` and `presentation/storage/` are UI screens exclusively dedicated to cloud accounts and cloud quotas.
   - Neither of these packages is required for local scanning, local storage in Room, local PDF generation, OCR, or local app locking.
3. **Identification of Coupled Seams**:
   - `di/RepositoryModule.kt` wires `AuthRepository` and `SyncRepository`.
   - `SettingsScreen` and `SettingsViewModel` expose UI toggles for Account and Cloud.
   - `AppNavigation` has routes for `Login`, `Signup`, and `Storage`.
   - `HomeViewModel` calls `syncRepository.triggerImmediateSync()`.
   - `NotificationService` creates a `sync_channel`.
   - `OnboardingDialog` displays a 4th slide for "Cloud Backup".
4. **Resolution Strategy**:
   - **Delete** all isolated cloud/auth/storage files and packages.
   - **Adapt** the coupled seams to remove cloud references cleanly (e.g. prune the Account and Cloud sections from Settings, remove sync routes from Navigation, remove sync call from HomeViewModel).
   - **Clean build scripts** (`libs.versions.toml`, `build.gradle.kts`, `AndroidManifest.xml`) by removing Firebase, Google ID, Credential Manager, and WorkManager dependencies.
5. **No Regressions on Core Functionality**:
   - Local scanning (`ScannerViewModel`, ML Kit Document Scanner), image processing (`ImageFilterService`), PDF generation (`PdfGeneratorService`), OCR (`ViewerViewModel`), Room DB (`AppDatabase`, `PageDao`, `FolderDao`), Biometric app lock (`AppLockGate`), and AES-256 local encryption (`EncryptionService`) do NOT depend on Firebase or WorkManager and will continue to function properly.

---

## 3. Caveats

- **Room Database Schema**: `DocumentEntity` currently includes `syncStatus` and `cloudPdfUrl`. If these columns are removed, the database schema version would change. Since `fallbackToDestructiveMigration()` is active on `AppDatabase`, removing or retaining these columns locally won't crash the app, but keeping clean entities without unnecessary columns is recommended.
- **Internet Permission in Manifest**: ML Kit document scanner and Latin text recognition operate on-device via Play Services. If true zero-network operation is desired, `android.permission.INTERNET` can be removed from `AndroidManifest.xml`. If ML Kit dynamic model download is required on certain legacy devices, `INTERNET` could be retained if needed, but Scanly's ML Kit scanner is bundled locally via play services.
- **No Caveats on Core Scanning**: All core scanning and editing pipelines have zero cloud dependencies.

---

## 4. Conclusion

The Scanly Android codebase has a clear separation between its core local functionality and the legacy Firebase/Cloud sync architecture. 

A total of **15+ files/directories** are identified for outright deletion, **10+ files** require straightforward adaptation, and **12+ dependencies/plugins** in `libs.versions.toml` and `build.gradle.kts` can be safely removed.

The detailed, file-by-file breakdown is documented in `survey_report.md`.

---

## 5. Verification Method

To verify these findings independently:

1. **Inspect Survey Report**:
   - View `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_survey_1\survey_report.md`
2. **Verify File References**:
   - `view_file` on `app/src/main/java/com/docscanner/app/di/RepositoryModule.kt` to observe `AuthRepository` and `SyncRepository` bindings.
   - `view_file` on `app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt` to observe `Login`, `Signup`, and `Storage` routes.
   - `view_file` on `app/src/main/java/com/docscanner/app/presentation/settings/SettingsScreen.kt` to observe "Account" and "Cloud" UI sections.
   - `view_file` on `gradle/libs.versions.toml` to observe Firebase, WorkManager, and Credential Manager dependencies.
3. **Post-Refactoring Validation**:
   - Build project with `gradlew.bat assembleDebug` to confirm 0 compilation errors.
   - Run `gradlew.bat lint` or static checks to confirm 0 unresolved references.
