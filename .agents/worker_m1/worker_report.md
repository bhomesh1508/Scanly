# Milestone 1 Implementation Report: Security Hardening, Storage Safety & Core Architecture

**Target Project**: Scanly Offline Android Document Scanner (`com.docscanner.app`)  
**Milestone**: Milestone 1 (M1)  
**Agent**: Worker Subagent (Implementer / QA / Specialist)  
**Date**: 2026-08-28  

---

## 1. Overview of Delivered Implementations

All 15 core architectural, security, storage safety, and memory management features assigned for Milestone 1 (F1 through F15) have been completely and genuinely implemented across 17 files in the codebase.

---

## 2. Detailed Feature Breakdown & Implementations

### [F1] FileProvider Authority Normalization
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/util/Constants.kt`
  - `app/src/main/java/com/docscanner/app/util/Extensions.kt`
  - `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt`
- **Implementation Details**:
  - Normalized all FileProvider authority references across `Extensions.kt`, `PdfGeneratorService.kt`, and `Constants.kt` to `"${packageName}.fileprovider"`, matching `AndroidManifest.xml` (`${applicationId}.fileprovider`).
  - Added `clipData = ClipData.newRawUri("", uri)` and `FLAG_GRANT_READ_URI_PERMISSION` to ensure Android 7.0+ (API 24+) receivers have read permissions for shared files.

### [F2] Singleton DataStore Resolution
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/di/AppModule.kt`
  - `app/src/main/java/com/docscanner/app/data/repository/SettingsRepositoryImpl.kt`
- **Implementation Details**:
  - Removed duplicate `private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")` from `SettingsRepositoryImpl.kt`.
  - Injected `DataStore<Preferences>` directly into `SettingsRepositoryImpl` constructor via Hilt DI.
  - Implemented safe enum parsing (`runCatching { Enum.valueOf(...) }.getOrDefault(...)`) for `ThemeMode`, `FilterType`, `PageSize`, `QualityLevel`, and `MarginPreset` to prevent crashes from corrupted or unrecognized stored preferences.

### [F3] Scanner Image Persistence Pipeline
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt`
  - `app/src/main/java/com/docscanner/app/presentation/scanner/ScannerViewModel.kt`
- **Implementation Details**:
  - Implemented `persistImageFile(docId, pageIndex, sourceUriOrPath)` in `DocumentRepositoryImpl.kt`.
  - When temporary `content://` or external cache image streams arrive from the ML Kit scanner, image streams are copied into `context.filesDir/documents/` as permanent JPEG files before storing their paths in the Room database (`PageEntity`).

### [F4] Storage Leak & Physical Shredding
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt`
  - `app/src/main/java/com/docscanner/app/data/local/dao/DocumentDao.kt`
- **Implementation Details**:
  - Implemented `shredPageFiles(page: PageEntity)` which physically deletes `originalImagePath`, `processedImagePath`, and `thumbnailPath` files from disk using `File(path).delete()`.
  - Added physical shredding on `permanentlyDelete(docId)`, `deletePage(pageId)`, and `purgeOldTrash()` before or alongside deleting SQLite records.
  - Added `getOldTrashDocumentsSync(cutoff)` in `DocumentDao.kt` to retrieve expired documents for file cleanup prior to record deletion.

### [F5] PDF Export Storage & Sanitation
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt`
  - `app/src/main/java/com/docscanner/app/util/Constants.kt`
- **Implementation Details**:
  - Replaced root-level `File("dummy.pdf")` with a dedicated export path: `File(context.cacheDir, Constants.PDF_EXPORTS_DIR)` (i.e. `cacheDir/pdf_exports/`).
  - Formatted output filenames using sanitized document titles and timestamps: `"${title.toSafeFileName()}_${System.currentTimeMillis()}.pdf"`.
  - Ensured directory creation (`exportDir.mkdirs()`) before writing.

### [F6] OCR URI Resolution Fix
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt`
- **Implementation Details**:
  - Fixed OCR input image creation to load from local file paths using `InputImage.fromFilePath(context, Uri.fromFile(File(page.processedImagePath)))`.
  - Added fallback handling for `content://` URIs if present.
  - Ensured `recognizer.close()` is properly called in success, failure, and exception blocks to prevent native C++ memory leaks.

### [F7] Biometric AppLock Integration
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt`
- **Implementation Details**:
  - Injected `SettingsViewModel` into `AppNavigation`.
  - Observed `settings.appLockEnabled` via `collectAsState()` and dynamically passed the value into `AppLockGate(isEnabled = settings.appLockEnabled)`.

### [F8] Manifest & Privacy Hardening
- **Files Modified**:
  - `app/src/main/AndroidManifest.xml`
- **Implementation Details**:
  - Set `android:allowBackup="false"` to prevent unencrypted Room DB extraction via ADB or cloud backup.
  - Set `android:usesCleartextTraffic="false"`.
  - Verified 0 network permissions exist in `AndroidManifest.xml` (`INTERNET` and `ACCESS_NETWORK_STATE` remain absent).

### [F9] Lockscreen Notification Privacy
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/service/notification/NotificationService.kt`
- **Implementation Details**:
  - Set `setVisibility(NotificationCompat.VISIBILITY_PRIVATE)` on scan complete notifications to hide document titles on locked screens.
  - Attached a generic public notification version via `setPublicVersion(publicNotification)` displaying `"A new document was scanned successfully."`.
  - Referenced `Constants.SCAN_CHANNEL_ID` for channel consistency.

### [F10] Clipboard Sensitivity Flagging
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt`
- **Implementation Details**:
  - In `copyOcrText(context: Context)`, added `ClipDescription.EXTRA_IS_SENSITIVE` on Android 13+ (API 33+) using `PersistableBundle` to prevent unmasked clipboard preview overlays.

### [F11] Encryption Check Heuristic Fix
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/service/encryption/EncryptionService.kt`
- **Implementation Details**:
  - Fixed `isEncrypted(file: File)` to check `file.exists() && file.length() > 0L`, read header bytes into a 16-byte buffer, and verify `bytesRead > 0`.

### [F12] Scoped FileProvider Paths
- **Files Modified**:
  - `app/src/main/res/xml/file_paths.xml`
- **Implementation Details**:
  - Replaced broad `<cache-path name="cache" path="/" />` with scoped `<cache-path name="pdf_exports" path="pdf_exports/" />` and `<cache-path name="temp" path="temp/" />`.

### [F13] Memory-Safe PDF & Image Processing
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt`
  - `app/src/main/java/com/docscanner/app/service/filter/ImageFilterService.kt`
- **Implementation Details**:
  - In `PdfGeneratorService.kt`, added explicit `bitmap.recycle()` in a `try-finally` block for each rendered page. Wrapped `PdfDocument` and file stream operations in `try-finally` ensuring `document.close()` is always executed.
  - Added `inSampleSize` and `RGB_565` optimization for compressed PDF generation.
  - In `ImageFilterService.kt`, eliminated redundant bitmap allocation for `FilterType.ORIGINAL` by returning early. Added safe color-matrix handling for `SHARPEN` on large images (>4MP) to prevent 96MB+ heap spikes.

### [F14] Database Transaction Safety & Safe Unwraps
- **Files Modified**:
  - `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt`
  - `app/src/main/java/com/docscanner/app/presentation/editor/EditorViewModel.kt`
  - `app/src/main/java/com/docscanner/app/presentation/folders/FolderDetailScreen.kt`
- **Implementation Details**:
  - Wrapped all composite multi-table Room DB mutations (`createDocument`, `mergeDocuments`, `splitDocument`, `duplicatePage`, `addPages`, `permanentlyDelete`, `deletePage`, `purgeOldTrash`) inside `appDatabase.withTransaction { ... }`.
  - In `EditorViewModel.kt`, replaced force unwrap `_document.value!!` with safe null check `_document.value?.let { ... }`.
  - In `FolderDetailScreen.kt`, removed the recursive private modifier `Modifier.clickable(onClick: () -> Unit)` and imported `androidx.compose.foundation.clickable`.

### [F15] ProGuard Rules
- **Files Modified**:
  - `app/proguard-rules.pro`
- **Implementation Details**:
  - Added `-assumenosideeffects` rules for stripping `android.util.Log` calls in release builds.
  - Added keep and dontwarn rules for Google ML Kit vision libraries.
  - Added keep and dontwarn rules for Coil 3 image loading library.

---

## 3. File Modification Summary

| # | File Path | Status | Purpose |
|---|---|---|---|
| 1 | `app/src/main/java/com/docscanner/app/util/Constants.kt` | Modified | Added FileProvider and export directory constants |
| 2 | `app/src/main/java/com/docscanner/app/util/Extensions.kt` | Modified | Normalized FileProvider authority & ClipData URI grants |
| 3 | `app/src/main/java/com/docscanner/app/di/AppModule.kt` | Modified | Singleton DataStore provider |
| 4 | `app/src/main/java/com/docscanner/app/data/repository/SettingsRepositoryImpl.kt` | Modified | Injected DataStore, removed duplicate delegate, safe enum parsing |
| 5 | `app/src/main/java/com/docscanner/app/data/local/dao/DocumentDao.kt` | Modified | Added `getOldTrashDocumentsSync` query |
| 6 | `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt` | Modified | Image persistence pipeline, physical shredding, `withTransaction` safety |
| 7 | `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt` | Modified | Cache dir PDF export, OCR File URI resolution + recognizer cleanup, sensitive clipboard |
| 8 | `app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt` | Modified | Dynamic `AppLockGate` connected to `settings.appLockEnabled` |
| 9 | `app/src/main/AndroidManifest.xml` | Modified | `allowBackup="false"`, `usesCleartextTraffic="false"` |
| 10 | `app/src/main/java/com/docscanner/app/service/notification/NotificationService.kt` | Modified | `VISIBILITY_PRIVATE` & public lockscreen notification |
| 11 | `app/src/main/java/com/docscanner/app/service/encryption/EncryptionService.kt` | Modified | Fixed `isEncrypted` heuristic |
| 12 | `app/src/main/res/xml/file_paths.xml` | Modified | Scoped `<cache-path>` to `pdf_exports/` and `temp/` |
| 13 | `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt` | Modified | `bitmap.recycle()` per page, `try-finally` document cleanup |
| 14 | `app/src/main/java/com/docscanner/app/service/filter/ImageFilterService.kt` | Modified | Prevented `ORIGINAL` allocations & optimized `SHARPEN` |
| 15 | `app/src/main/java/com/docscanner/app/presentation/editor/EditorViewModel.kt` | Modified | Safe unwrap in `saveChanges()` |
| 16 | `app/src/main/java/com/docscanner/app/presentation/folders/FolderDetailScreen.kt` | Modified | Removed shadowing recursive clickable modifier |
| 17 | `app/proguard-rules.pro` | Modified | Added Log stripping, ML Kit, Coil 3 rules |
