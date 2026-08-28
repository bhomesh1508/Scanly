# Scanly Android — Codebase Survey & Firebase/Cloud Sync Mapping Report

**Date**: 2026-08-27  
**Explorer**: Explorer Subagent 1 (Codebase Architecture & Firebase/Cloud Sync Mapping)  
**Project Root**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android`  

---

## 1. Executive Summary

This survey provides a complete architectural map of the **Scanly Android** application, pinpointing all legacy Firebase, Cloud Sync, Remote Authentication, and Cloud Storage components.

The application was originally architected as a hybrid offline/cloud document scanner. However, the remote layer contains a mixture of real Firebase API integrations (in `data/remote/`) and mocked offline implementations (in `data/repository/` and `presentation/`). 

Eliminating these cloud/remote components will transform the app into a strictly self-contained, privacy-first, offline-only application without breaking core document scanning, image filtering, PDF generation, local search, local folder organization, OCR, biometric app lock, and local file encryption.

---

## 2. Inventory of Firebase, Auth & Cloud Sync Components

### 2.1 Configuration & Rules Files (Root & Module Level)
| File Path | Description | Recommended Action |
|---|---|---|
| `firestore.rules` | Security rules for Firebase Firestore (`cloud.firestore`) | **Delete** |
| `storage.rules` | Security rules for Firebase Cloud Storage (`firebase.storage`) | **Delete** |
| `app/google-services.json` | Placeholder Google Services config with mock project IDs & API keys | **Delete** |

---

### 2.2 Remote Data Layer (`app/src/main/java/com/docscanner/app/data/remote/`)
| Class / File | Key Dependencies / APIs | Role in Codebase | Recommended Action |
|---|---|---|---|
| `data/remote/auth/FirebaseAuthService.kt` | `com.google.firebase.auth.FirebaseAuth`, `GoogleAuthProvider`, `CredentialManager`, `GetGoogleIdOption`, `GoogleIdTokenCredential` | Performs Firebase Auth email/password, Google sign-in via AndroidX Credential Manager, auth state listener | **Delete** |
| `data/remote/firestore/FirestoreService.kt` | `com.google.firebase.firestore.FirebaseFirestore`, `SetOptions` | Synchronizes user documents, folders, settings, and profile to Firestore collections | **Delete** |
| `data/remote/storage/CloudStorageService.kt` | `com.google.firebase.storage.FirebaseStorage` | Uploads/downloads PDF files and thumbnails to Firebase Cloud Storage; calculates cloud storage byte usage | **Delete** |
| `data/remote/sync/DocumentSyncWorker.kt` | `androidx.hilt.work.HiltWorker`, `androidx.work.CoroutineWorker`, `FirebaseAuthService`, `SyncManager` | WorkManager periodic/one-time worker executing background document synchronization | **Delete** |
| `data/remote/sync/SyncManager.kt` | `DocumentDao`, `PageDao`, `SyncStatus` | Coordinates synchronization between Room DB and cloud (currently offline stub) | **Delete** |

*Note: The entire directory `app/src/main/java/com/docscanner/app/data/remote/` can be safely deleted.*

---

### 2.3 Repositories & Data Layer (`data/repository/`, `data/local/`, `data/mapper/`)
| Class / File | Code Location | Nature of Cloud/Remote Coupling | Recommended Action |
|---|---|---|---|
| `data/repository/AuthRepositoryImpl.kt` | Lines 1–53 | Implements `AuthRepository` with in-memory state and dummy `local_user` | **Delete** |
| `data/repository/SyncRepositoryImpl.kt` | Lines 1–74 | Implements `SyncRepository` with `WorkManager`, `DocumentSyncWorker`, `SyncManager`, and `AuthRepository` | **Delete** |
| `data/repository/SettingsRepositoryImpl.kt` | Lines 37, 50, 64 | `PreferencesKeys.AUTO_SYNC_ENABLED` and `autoSyncEnabled` in DataStore mapping | **Adapt**: Remove `autoSyncEnabled` key and property |
| `data/local/entity/DocumentEntity.kt` | Lines 24, 25 | Fields `cloudPdfUrl: String?`, `syncStatus: String`, and index `Index("syncStatus")` | **Adapt / Retain**: Clean up fields or keep locally |
| `data/local/dao/DocumentDao.kt` | Lines 31-32, 46-47 | `@Query` methods `getUnsyncedDocuments()`, `updateSyncStatus(docId, status)` | **Adapt**: Remove unsynced query methods |
| `data/mapper/EntityMappers.kt` | Lines 20, 21, 38, 39 | Mapping `cloudPdfUrl` and `syncStatus` between entity and domain model | **Adapt**: Remove or simplify |

---

### 2.4 Domain Layer (`domain/model/`, `domain/repository/`)
| Class / File | Code Location | Description | Recommended Action |
|---|---|---|---|
| `domain/repository/AuthRepository.kt` | Lines 1–48 | Interface declaring `currentUser`, `isAuthenticated`, `signInWithGoogle`, `signInWithEmail`, `signUpWithEmail`, `signOut`, `deleteAccount` | **Delete** |
| `domain/repository/SyncRepository.kt` | Lines 1–36 | Interface declaring `syncDocument`, `syncAll`, `getStorageUsage`, `schedulePeriodicSync`, `triggerImmediateSync` | **Delete** |
| `domain/model/UserProfile.kt` | Lines 1–23 | Data class for authenticated user profile (`uid`, `email`, `displayName`, `cloudStorageUsedBytes`, `cloudStorageLimitBytes`) | **Delete** |
| `domain/model/UserSettings.kt` | Line 23 | Property `autoSyncEnabled: Boolean = true` | **Adapt**: Remove property |
| `domain/model/Document.kt` | Lines 27, 28, 39–41 | Fields `cloudPdfUrl: String?`, `syncStatus: SyncStatus`, and `enum class SyncStatus` | **Adapt**: Remove cloud fields / enum |

---

### 2.5 Dependency Injection (`di/`)
| Class / File | Code Location | Bound Services / Providers | Recommended Action |
|---|---|---|---|
| `di/RepositoryModule.kt` | Lines 28–35 | `@Binds abstract fun bindAuthRepository(...)` and `@Binds abstract fun bindSyncRepository(...)` | **Adapt**: Remove these two `@Binds` methods and unused imports |
| `di/AppModule.kt` | Lines 1–45 | Room `AppDatabase`, DataStore preferences, Context | **Keep** (No cloud wiring) |
| `di/DatabaseModule.kt` | Lines 1–25 | Room DAOs (`DocumentDao`, `PageDao`, `FolderDao`) | **Keep** (No cloud wiring) |

---

### 2.6 Services & Utilities (`service/`, `util/`, `DocScannerApp.kt`)
| Class / File | Code Location | Cloud Reference | Recommended Action |
|---|---|---|---|
| `DocScannerApp.kt` | Lines 5–6 | Unused imports `FirebaseFirestore` and `FirebaseFirestoreSettings` | **Adapt**: Remove unused imports |
| `service/notification/NotificationService.kt` | Lines 19–26, 39–60, 78 | `sync_channel` ("Cloud Sync" notification channel), `showSyncProgressNotification`, `showSyncCompleteNotification`, `SYNC_NOTIFICATION_ID` | **Adapt**: Remove sync channel and sync notification methods |
| `util/Constants.kt` | Lines 12, 16–18, 19, 22–25 | `FREE_STORAGE_LIMIT_BYTES`, `SYNC_WORK_NAME`, `SYNC_INTERVAL_HOURS`, `SYNC_CHANNEL_ID`, `FIREBASE_USERS_COLLECTION`, `FIREBASE_DOCUMENTS_COLLECTION`, `FIREBASE_STORAGE_DOCUMENTS` | **Adapt**: Remove cloud & sync constants |

---

## 3. UI, Presentation & Navigation Mapping

### 3.1 Navigation Routes (`presentation/navigation/`)
| Route / Object | File | Path | Recommended Action |
|---|---|---|---|
| `Screen.Login` | `Screen.kt:20`, `AppNavigation.kt:60, 223–241` | `"auth/login"` | **Delete** route & composable |
| `Screen.Signup` | `Screen.kt:21`, `AppNavigation.kt:61, 244–255` | `"auth/signup"` | **Delete** route & composable |
| `Screen.Storage` | `Screen.kt:18`, `AppNavigation.kt:205–211` | `"storage"` (Cloud storage quota viewer) | **Delete** route & composable |

---

### 3.2 Screens & ViewModels to Remove
| Screen / ViewModel | File Path | UI Content / Purpose | Recommended Action |
|---|---|---|---|
| `LoginScreen` | `presentation/auth/LoginScreen.kt` | Email/password login form, Google Sign-in button, Skip button | **Delete** |
| `SignupScreen` | `presentation/auth/SignupScreen.kt` | Email/password/display name registration form | **Delete** |
| `AuthViewModel` | `presentation/auth/AuthViewModel.kt` | ViewModel managing auth states, form validation, sign-in flows | **Delete** |
| `StorageScreen` | `presentation/storage/StorageScreen.kt` | Circular cloud storage progress indicator (used/total bytes), upgrade plan button | **Delete** |
| `StorageViewModel` | `presentation/storage/StorageViewModel.kt` | ViewModel fetching cloud storage usage via `SyncRepository` & `AuthRepository` | **Delete** |
| `DocumentCard` (common) | `presentation/common/DocumentCard.kt` | Duplicate card component containing `SyncIcon` (`CloudDone`, `CloudSync`, etc.) | **Delete** |

*Note: `presentation/auth/` and `presentation/storage/` directories can be deleted completely.*

---

### 3.3 Screens & ViewModels to Adapt
| Screen / ViewModel | File Path | Elements to Adapt | Action Needed |
|---|---|---|---|
| `SettingsScreen` | `presentation/settings/SettingsScreen.kt` | 1. "Account" section (Lines 36–55) with user profile / sign out / login link.<br>2. "Cloud" section (Lines 83–95) with "Auto Sync" switch and "Storage Usage" item. | Remove "Account" and "Cloud" sections. Retain Appearance, Security (App Lock, Encrypt), Data (Trash, Clear Cache), About. |
| `SettingsViewModel` | `presentation/settings/SettingsViewModel.kt` | Injects `AuthRepository` (Line 24); exposes `currentUser` (Line 34); has `toggleAutoSync` (Line 57) and `signOut` (Line 65). | Remove `AuthRepository` dependency, `currentUser`, `toggleAutoSync`, and `signOut`. |
| `HomeScreen` & `HomeViewModel` | `presentation/home/HomeViewModel.kt` | Injects `SyncRepository` (Line 19); contains `syncAll()` (Lines 78–82). | Remove `SyncRepository` injection and `syncAll()` method. |
| `OnboardingDialog` | `presentation/common/OnboardingDialog.kt` | Page 4 (Line 51): `OnboardingPage(Icons.Default.CloudUpload, "Cloud Backup", "Keep your documents safe and synced across all your devices.")` | Change page count to 3 or replace 4th slide with "100% Offline & Private" / "Export & Share". |
| `AppNavigation` | `presentation/navigation/AppNavigation.kt` | References to Login, Signup, Storage screens, and imports of `AuthViewModel`, `LoginScreen`, `SignupScreen`, `StorageScreen`, `StorageViewModel`. | Remove these routes, composables, and imports. |

---

### 3.4 String Resources (`app/src/main/res/values/strings.xml`)
The following string sections are candidates for deletion:
- Lines 90–107: `<!-- Auth -->` (`auth_welcome`, `auth_sign_in`, `auth_sign_up`, `auth_google`, `auth_email_hint`, `auth_password_hint`, etc.)
- Lines 110, 125–128: `settings_account`, `settings_cloud`, `settings_auto_sync`, `settings_auto_sync_subtitle`, `settings_storage`
- Lines 138–143: `<!-- Storage -->` (`storage_title`, `storage_used`, `storage_free_tip`, `storage_upgrade`, `storage_not_signed_in`)
- Lines 162–163: `onboarding_cloud_title`, `onboarding_cloud_subtitle`
- Lines 197–200: `cd_sync_status_synced`, `cd_sync_status_syncing`, `cd_sync_status_local`, `cd_sync_status_failed`

---

## 4. Build Configuration & Dependency Cleanup

### 4.1 Root `build.gradle.kts`
- Remove line 7: `alias(libs.plugins.google.services) apply false`

### 4.2 Module `app/build.gradle.kts`
- Remove Google Services plugin if present.
- Remove from `dependencies`:
  - `implementation(libs.coroutines.play.services)` (Line 60)
  - `implementation(libs.hilt.work)` (Line 78)
  - `ksp(libs.hilt.work.compiler)` (Line 79)
  - `implementation(platform(libs.firebase.bom))` (Line 87)
  - `implementation(libs.firebase.auth)` (Line 88)
  - `implementation(libs.firebase.firestore)` (Line 89)
  - `implementation(libs.firebase.storage)` (Line 90)
  - `implementation(libs.firebase.analytics)` (Line 91)
  - `implementation(libs.work.runtime.ktx)` (Line 98)
  - `implementation(libs.credentials)` (Line 103)
  - `implementation(libs.credentials.play.services.auth)` (Line 104)
  - `implementation(libs.googleid)` (Line 105)

### 4.3 Version Catalog `gradle/libs.versions.toml`
- Remove versions:
  - `firebaseBom`
  - `workManager`
  - `credentials`
  - `googleid`
  - `hiltWork`
  - `googleServices`
- Remove libraries:
  - `coroutines-play-services`
  - `hilt-work`, `hilt-work-compiler`
  - `firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`
  - `work-runtime-ktx`
  - `credentials`, `credentials-play-services-auth`, `googleid`
- Remove plugins:
  - `google-services`

### 4.4 `AndroidManifest.xml`
- Remove permissions (if no longer needed):
  - `INTERNET` (Line 6) - *Note: ML Kit document scanner runs fully on-device; if no network operations exist, Internet permission can be removed completely for true offline compliance!*
  - `ACCESS_NETWORK_STATE` (Line 7)
  - `RECEIVE_BOOT_COMPLETED` (Line 14)
  - `FOREGROUND_SERVICE` (Line 15)
- Remove provider declarations:
  - `androidx.startup.InitializationProvider` / `WorkManagerInitializer` (Lines 53–62)
  - `FirebaseInitProvider` (Lines 64–68)

---

## 5. Summary of Deletion vs Adaptation Matrix

| Action | Category | Target Files / Directories |
|---|---|---|
| **DELETE** | Root / Config | `firestore.rules`, `storage.rules`, `app/google-services.json` |
| **DELETE** | Data Remote | `app/src/main/java/com/docscanner/app/data/remote/` (entire package, 5 files) |
| **DELETE** | Repositories | `AuthRepositoryImpl.kt`, `SyncRepositoryImpl.kt`, `AuthRepository.kt`, `SyncRepository.kt` |
| **DELETE** | Domain Models | `UserProfile.kt` |
| **DELETE** | UI Screens/VMs | `presentation/auth/` (3 files), `presentation/storage/` (2 files), `presentation/common/DocumentCard.kt` |
| **ADAPT** | Navigation | `Screen.kt`, `AppNavigation.kt` |
| **ADAPT** | UI Screens/VMs | `SettingsScreen.kt`, `SettingsViewModel.kt`, `HomeViewModel.kt`, `OnboardingDialog.kt` |
| **ADAPT** | Data / Local | `DocumentEntity.kt`, `DocumentDao.kt`, `EntityMappers.kt`, `SettingsRepositoryImpl.kt`, `UserSettings.kt`, `Document.kt` |
| **ADAPT** | DI | `RepositoryModule.kt` |
| **ADAPT** | App / Services | `DocScannerApp.kt`, `NotificationService.kt`, `Constants.kt` |
| **ADAPT** | Resources | `strings.xml` |
| **ADAPT** | Build & Manifest | `AndroidManifest.xml`, `build.gradle.kts` (root & app), `gradle/libs.versions.toml` |

---

## 6. Verification and Feasibility Analysis

- **Core Functionality Preserved**:
  - Scanning via Google ML Kit (`ScannerScreen.kt`, `ScannerViewModel.kt`)
  - Image editing and filter pipeline (`ImageFilterService.kt`, `EditorScreen.kt`)
  - PDF Generation and sharing (`PdfGeneratorService.kt`, `ViewerScreen.kt`)
  - On-device text recognition OCR (`ViewerViewModel.kt` Latin Text Recognizer)
  - Local SQLite persistence with Room (`AppDatabase.kt`, `DocumentDao.kt`, `FolderDao.kt`, `PageDao.kt`)
  - Biometric App Lock (`AppLockGate.kt`) and AES-256 local document encryption (`EncryptionService.kt`)
  - Multi-folder document organization (`FoldersScreen.kt`, `FolderDetailScreen.kt`)
  - Local search with debounce (`SearchScreen.kt`, `SearchViewModel.kt`)
  - Trash retention and purge (`TrashScreen.kt`, `TrashViewModel.kt`)

- **Expected Net Reduction**:
  - ~15+ source/config files completely deleted
  - ~12 external build dependencies / plugins eliminated
  - Substantial APK size and build time reduction
  - Zero dead code or mock cloud sync overhead
