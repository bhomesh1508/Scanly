# Project: Scanly Android Design & Security Audit and Polish

## Architecture
- **Framework**: Android (Jetpack Compose, Kotlin 2.4.10, compileSdk 37, targetSdk 34)
- **Design System**: Material 3 with Dynamic Theming, Surface Container Hierarchy, Edge-to-Edge transparency
- **Architecture Pattern**: Clean Architecture (Presentation, Domain, Data) with MVVM, Hilt Dependency Injection, Coroutines & Flow
- **Data & Storage**: Room Database (SQLite), Jetpack DataStore Preferences, App-Internal Isolated Storage (`filesDir/documents`, `cacheDir/pdf_exports`), FileProvider for safe sharing
- **Services**: Google ML Kit (Document Scanner, On-Device Text Recognition OCR), AndroidX BiometricPrompt, Jetpack Security Crypto, Android Native PDF Engine

## Feature Inventory
| # | Feature | Description | Milestone | Source | Status |
|---|---------|-------------|-----------|--------|--------|
| F1 | FileProvider Authority Normalization | Match authority string `${packageName}.fileprovider` across Manifest, Extensions, and PDF service | M1 | Survey (Arch/Sec) | DONE |
| F2 | Singleton DataStore Resolution | Unify DataStore into a single `@Singleton` preference store via Hilt DI | M1 | Survey (Arch) | DONE |
| F3 | Scanner Image Persistence Pipeline | Copy ML Kit temporary cache image streams to internal app files directory | M1 | Survey (Arch/Sec) | DONE |
| F4 | Storage Leak & Physical Shredding | Physically delete image/thumbnail files upon permanent document/page deletion or trash purge | M1 | Survey (Sec/Arch) | DONE |
| F5 | PDF Export Storage & Sanitation | Replace root filesystem `File("dummy.pdf")` with timestamped sanitized file in cache dir | M1 | Survey (Sec/Arch) | DONE |
| F6 | OCR URI Resolution Fix | Fix InputImage loading from absolute file paths | M1 | Survey (Arch) | DONE |
| F7 | Biometric AppLock Integration | Connect `AppLockGate` dynamically to `settings.appLockEnabled` | M1 | Survey (Sec/Arch) | DONE |
| F8 | Manifest & Privacy Hardening | Set `allowBackup="false"`, `usesCleartextTraffic="false"`, verify 0 internet permissions | M1 | Survey (Sec) | DONE |
| F9 | Lockscreen Notification Privacy | Set `VISIBILITY_PRIVATE` and attach generic public notification for scan alerts | M1 | Survey (Sec) | DONE |
| F10 | Clipboard Sensitivity Flagging | Attach `ClipDescription.EXTRA_IS_SENSITIVE` on OCR text copy (API 33+) | M1 | Survey (Sec) | DONE |
| F11 | Encryption Check Heuristic Fix | Fix `isEncrypted` heuristic in `EncryptionService` to verify real header bytes | M1 | Survey (Sec) | DONE |
| F12 | Scoped FileProvider Paths | Narrow `file_paths.xml` cache paths to `pdf_exports/` and `temp/` | M1 | Survey (Sec) | DONE |
| F13 | Memory-Safe PDF & Image Processing | Add explicit `Bitmap.recycle()` and memory downsampling in PDF generation & filters | M1 | Survey (Arch) | DONE |
| F14 | Database Transaction Safety | Wrap composite multi-table queries in Room `@Transaction` or `withTransaction` | M1 | Survey (Arch) | DONE |
| F15 | ProGuard Rules & Log Stripping | Add ProGuard rules for log stripping, ML Kit, and Coil | M1 | Survey (Sec/Arch) | DONE |
| F16 | Edge-to-Edge & System Bar Polish | Remove `statusBarColor` override in `Theme.kt`, enable transparent system bars | M2 | Survey (UI) | DONE |
| F17 | Material 3 Color & Surface Hierarchy | Add complete M3 surface containers and tonal palette roles | M2 | Survey (UI) | DONE |
| F18 | Navigation Bar & Scaffold Unification | Eliminate 56dp empty hole in `BottomNavBar`, resolve duplicate Scaffold/FAB | M2 | Survey (UI/Arch) | DONE |
| F19 | Screen Navigation Transitions | Implement smooth Material 3 enter/exit transitions in `NavHost` | M2 | Survey (UI) | DONE |
| F20 | HomeScreen Modernization | Add M3 search bar, localized sort labels, rich metadata cards, card overflow menu | M2 | Survey (UI) | DONE |
| F21 | Folders & Detail Screen Polish | Add styled folder badges, color palette picker dialog, rich folder detail list | M2 | Survey (UI) | DONE |
| F22 | SearchScreen Enhancement | Hide internal UUIDs, show rich search cards with page counts and dates | M2 | Survey (UI) | DONE |
| F23 | SettingsScreen Categorization | Group settings into M3 cards, working theme dialog (System/Light/Dark), clear cache feedback | M2 | Survey (UI) | DONE |
| F24 | TrashScreen Confirmation & Cards | Add confirmation dialogs for empty trash / delete, rich item cards with days left | M2 | Survey (UI) | DONE |
| F25 | EditorScreen Tabbed Tool Panel | Implement 3-tab bottom toolbar (Filters, Adjustments, Pages & Organize) | M3 | Survey (UI) | DONE |
| F26 | Editor Page Management Controls | Implement Rotate, Delete Page, Duplicate Page, Add Pages in Editor UI | M3 | Survey (UI) | DONE |
| F27 | Live Filter & Adjustment Controls | Add smooth sliders with icon headers and interactive filter previews | M3 | Survey (UI) | DONE |
| F28 | ViewerScreen Action Bar Modernization | Expose primary actions (Edit, Export, OCR) and overflow menu with Delete confirmation | M3 | Survey (UI) | DONE |
| F29 | Viewer Pinch-to-Zoom & Pan Gestures | Add interactive transformable zoom and pan with double-tap reset on page images | M3 | Survey (UI) | DONE |
| F30 | Complete Material 3 PDF Export Dialog | Implement interactive configuration dialog (Title, Page Size, Margins, Quality) | M3 | Survey (UI) | DONE |
| F31 | Polished OCR Result Bottom Sheet | Formatted text preview, character count, copy to clipboard, and share text actions | M3 | Survey (UI) | DONE |
| F32 | Final Build & Agent-As-Judge Verification | Verify `./gradlew assembleDebug` (0 errors), Security Judge, UI Judge, Forensic Audit | M4 | Acceptance Criteria | DONE |

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| M1 | Security Hardening, Storage Safety & Core Architecture | F1–F15 (FileProvider, DataStore, Persistence, Shredding, PDF Export, AppLock, Manifest, Notifications, Cryptography, Memory, DB Transactions) | none | DONE |
| M2 | UI/UX Material 3 Polish: Theming, Navigation & Primary Screens | F16–F24 (Theme, Surface Tokens, BottomNavBar, NavTransitions, HomeScreen, Folders, Search, Settings, Trash) | M1 | DONE |
| M3 | UI/UX Deep Polish: Editor, Viewer, Gestures & Dialogs | F25–F31 (Editor 3-tab layout, page tools, Viewer zoom/pan, PDF Export Dialog, OCR Sheet) | M1, M2 | DONE |
| M4 | Final Build Verification & Acceptance Gate Reviews | F32 (assembleDebug build validation, Security Judge, UI/UX Judge, Forensic Auditor) | M1, M2, M3 | DONE |

## Interface Contracts
### DocumentRepository ↔ UI / ViewModels
- `permanentlyDelete(docId: String)`: Atomically deletes DB rows and all associated image/thumbnail files on disk.
- `emptyAllTrash()`: Atomically wipes all trashed records and shreds physical image files.
- `createDocument(title: String, pageUris: List<Uri>, pdfUri: Uri?)`: Copies temporary URIs into app-internal storage before DB record insertion.
- `updateDocument(document: Document)`: Non-blocking, safe null handling.

### SettingsRepository ↔ AppNavigation & SettingsScreen
- `settings: Flow<UserSettings>`: Shared single-source-of-truth DataStore stream for `appLockEnabled`, `themeMode`, `defaultFilter`, `pdfQuality`.

### PdfGeneratorService ↔ ViewerViewModel / ExportDialog
- `generatePdf(pages: List<Page>, options: PdfExportOptions, outputFile: File): Result<File>`: Memory-safe generation with per-page bitmap recycling and safe closing.

## Code Layout
- `app/src/main/java/com/docscanner/app/`
  - `data/local/` (Database, DAOs, Entities)
  - `data/repository/` (Repository implementations)
  - `di/` (Hilt Modules)
  - `domain/model/` & `domain/repository/` (Models and interfaces)
  - `presentation/theme/` (Color, Theme, Type, Shape)
  - `presentation/navigation/` (AppNavigation, BottomNavBar, AppLockGate)
  - `presentation/home/`, `editor/`, `viewer/`, `folders/`, `search/`, `settings/`, `trash/`, `common/`
  - `service/` (Encryption, Filter, Notification, PDF)
  - `util/` (Constants, Extensions, DateUtils)
- `app/src/main/res/` (Layouts, Drawables, Values, XML)
- `app/proguard-rules.pro` & `app/build.gradle.kts`
