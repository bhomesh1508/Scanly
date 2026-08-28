# Project: Scanly Android Offline Refactoring

## Architecture
Scanly Android is a Kotlin-based Jetpack Compose document scanning and management app. The target architecture is a **100% self-contained, offline-only, privacy-first local Android application** using:
- **UI Layer**: Jetpack Compose (Material3), Navigation Compose, ViewModels with StateFlow.
- **Local Persistence**: Room SQLite Database (`AppDatabase`, `DocumentDao`, `PageDao`, `FolderDao`), DataStore Preferences (`SettingsRepository`).
- **Dependency Injection**: Dagger Hilt 2.60.1.
- **Document Processing**:
  - Scanning: Google Play Services ML Kit Document Scanner (`play-services-mlkit-document-scanner:16.0.0`).
  - Text OCR: Google ML Kit Text Recognition on-device (`text-recognition:16.0.1`).
  - Image Pipeline: `ImageFilterService` (9 native ColorMatrix/Bitmap filter presets + contrast/brightness).
  - PDF Engine: Android native `android.graphics.pdf.PdfDocument` (`PdfGeneratorService`).
- **Security**: AndroidX Biometric (`BiometricPrompt` for App Lock), AndroidX Security Crypto (`EncryptedFile` + AES-256 GCM MasterKey).
- **Image Loading**: Coil 3 Compose (`io.coil-kt.coil3:coil-compose`).

---

## Feature Inventory
| # | Feature | Description | Milestone | Source |
|---|---------|-------------|-----------|--------|
| 1 | Document Scanning | Multi-page scanning via ML Kit Document Scanner API | M3 | survey |
| 2 | Image Filters & Enhancement | 9 ColorMatrix presets (Original, Magic, Grayscale, B&W, etc.) + Brightness & Contrast | M3 | survey |
| 3 | PDF Generation & Export | Custom page size (A4, Letter, Auto), margins, quality levels, password protection | M3 | survey |
| 4 | On-Device Text OCR | Text recognition from scanned pages via ML Kit on-device TextRecognizer | M3 | survey |
| 5 | Room Database Local Persistence | Offline SQLite entities, DAOs, relations for Documents, Pages, Folders | M1 | survey |
| 6 | Folder & Document Organization | Move to folder, create folder, search documents, batch selection | M3 | survey |
| 7 | 30-Day Trash Retention | Soft delete to trash, auto-purge calculation, restore, permanent deletion | M3 | survey |
| 8 | Biometric App Lock & Encryption | Biometric prompt on resume, AES-256 GCM file encryption | M3 | survey |
| 9 | User Preferences DataStore | Theme selection, auto-enhance, default page size, app lock state | M1 | survey |
| 10 | Legacy Cloud Sync Deletion | Delete `data/remote/*`, `firestore.rules`, `storage.rules`, `google-services.json` | M1 | request |
| 11 | Auth & Cloud UI Removal | Delete `presentation/auth/*`, `presentation/storage/*`, adapt Settings & Home | M1 | request |
| 12 | DI & Service Pruning | Remove Auth & Sync repos from `RepositoryModule.kt`, remove sync notifications | M1 | request |
| 13 | Gradle Plugin Cleanup | Remove `com.google.gms.google-services` plugin from root & catalog | M2 | request |
| 14 | Dead Dependency Cleanup | Remove 13 unused/cloud dependencies from `build.gradle.kts` and `libs.versions.toml` | M2 | request |
| 15 | AndroidManifest & Proguard Cleanup | Remove Firebase/WorkManager providers, unused permissions, and Firebase proguard rules | M2 | request |
| 16 | Core Unit Test Suite | Comprehensive unit tests for mappers, services, utils, repositories | M3 | request |
| 17 | Final Build & Lint Verification | `./gradlew assembleDebug` (0 errors), `./gradlew lint`, forensic integrity verification | M4 | request |

---

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Remove Unused Architecture & Cloud UI | Delete legacy remote package, auth/storage screens, rules files, prune entities/DAOs/DI/navigation/resources | none | PLANNED |
| 2 | Dependency & Build Configuration Cleanup | Prune build.gradle.kts, libs.versions.toml, root build.gradle.kts, AndroidManifest.xml, proguard-rules.pro | M1 | PLANNED |
| 3 | Core Offline Functionality & Unit Testing | Validate local components, fix FileProvider authority bug, write comprehensive unit tests | M2 | PLANNED |
| 4 | Verification, Forensic Audit & Build Checks | assembleDebug, lint checks, file/line count reduction measurement, forensic audit veto | M3 | PLANNED |

---

## Interface Contracts
### Clean Local Data Contracts
- `DocumentRepository`: `getAllDocuments()`, `getDocumentWithPages(id)`, `insertDocument()`, `updateDocument()`, `moveToTrash()`, `restoreFromTrash()`, `deletePermanently()`, `searchDocuments(query)`. (No `syncStatus` or `cloudPdfUrl`).
- `FolderRepository`: `getAllFolders()`, `getFolderById(id)`, `insertFolder()`, `deleteFolder()`.
- `SettingsRepository`: `userSettings: Flow<UserSettings>`, `setDarkMode()`, `setAutoEnhance()`, `setDefaultPageSize()`, `setAppLockEnabled()`, `setEncryptFiles()`. (No `autoSyncEnabled`).
- `PdfGeneratorService`: `generatePdf(document, pages, options): Result<File>`.
- `ImageFilterService`: `applyFilter(bitmap, filterType)`, `applyAdjustments(bitmap, brightness, contrast)`, `generateThumbnail(bitmap)`.
- `EncryptionService`: `encryptFile(source, dest)`, `decryptFile(source, dest)`.

---

## Code Layout
```
app/src/main/
├── java/com/docscanner/app/
│   ├── data/
│   │   ├── local/          # Room DB, DAOs, Entities, Converters
│   │   ├── mapper/         # Entity <-> Domain mappers
│   │   └── repository/     # DocumentRepositoryImpl, FolderRepositoryImpl, SettingsRepositoryImpl
│   ├── di/                 # AppModule, DatabaseModule, RepositoryModule
│   ├── domain/
│   │   ├── model/          # Document, Page, Folder, UserSettings, PdfExportOptions
│   │   └── repository/     # Repository interfaces
│   ├── presentation/
│   │   ├── common/         # AppLockGate, ConfirmationDialog, OnboardingDialog, etc.
│   │   ├── editor/         # EditorScreen, EditorViewModel, AdjustmentsPanel, FilterSelector
│   │   ├── folders/        # FoldersScreen, FoldersViewModel, FolderDetailScreen
│   │   ├── home/           # HomeScreen, HomeViewModel, DocumentGrid/List
│   │   ├── navigation/     # Screen, AppNavigation
│   │   ├── scanner/        # ScannerScreen, ScannerViewModel
│   │   ├── search/         # SearchScreen, SearchViewModel
│   │   ├── settings/       # SettingsScreen, SettingsViewModel
│   │   ├── theme/          # Color, Theme, Type
│   │   ├── trash/          # TrashScreen, TrashViewModel
│   │   └── viewer/         # ViewerScreen, ViewerViewModel, OcrResultSheet, PdfExportDialog
│   ├── service/            # ImageFilterService, PdfGeneratorService, EncryptionService, NotificationService
│   ├── util/               # Constants, DateUtils, Extensions, FileUtils
│   └── DocScannerApp.kt
├── res/                    # drawables, layout, values (strings.xml, colors.xml, themes.xml), xml
└── AndroidManifest.xml
```
