# Milestone 1 Code Review & Adversarial Challenge Report

**Project**: Scanly Offline Android Document Scanner (`com.docscanner.app`)  
**Milestone**: Milestone 1 (M1: Security Hardening, Storage Safety & Core Architecture)  
**Reviewer**: Reviewer 1 (`reviewer_m1_1`)  
**Verdict**: **REQUEST_CHANGES**  
**Date**: 2026-08-28  

---

## 1. Review Summary

A comprehensive quality and adversarial review was conducted across all 17 modified files in Milestone 1. The worker has successfully implemented the core architecture, security hardening, file shredding, database transactions, singleton DataStore injection, and memory-safe PDF/image handling across F1 through F15. 

However, two findings require remediation before Milestone 1 can be formally approved:
1. **Compilation Blocker**: Missing import `import com.docscanner.app.domain.model.QualityLevel` in `PdfGeneratorService.kt` causing an unresolved reference error on `QualityLevel.COMPRESSED`.
2. **Cryptographic Validation Flaw**: `EncryptionService.isEncrypted(file: File)` reads 16 raw bytes from a standard `FileInputStream` and checks `bytesRead > 0`, which evaluates to `true` for **any** non-empty file regardless of whether it is encrypted or plain plaintext/JPEG.

---

## 2. Findings & Defects

### [Critical / Compilation Blocker] Finding 1: Unresolved Reference `QualityLevel` in `PdfGeneratorService.kt`
- **Location**: `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:26`
- **Observation**:
  ```kotlin
  val sampleSize = when (options.quality) {
      QualityLevel.COMPRESSED -> 2
      else -> 1
  }
  ```
  `PdfGeneratorService.kt` is in package `com.docscanner.app.service.pdf`. The imports list includes `MarginPreset`, `Page`, `PageSize`, and `PdfExportOptions`, but **omits** `import com.docscanner.app.domain.model.QualityLevel`.
- **Impact**: Compilation failure (`Unresolved reference: QualityLevel`).
- **Required Fix**: Add `import com.docscanner.app.domain.model.QualityLevel` to the import block of `PdfGeneratorService.kt`.

---

### [Major / Security Heuristic] Finding 2: False Positive `isEncrypted` in `EncryptionService.kt`
- **Location**: `app/src/main/java/com/docscanner/app/service/encryption/EncryptionService.kt:59-70`
- **Observation**:
  ```kotlin
  fun isEncrypted(file: File): Boolean {
      if (!file.exists() || file.length() == 0L) return false
      return try {
          FileInputStream(file).use { fis ->
              val header = ByteArray(16)
              val bytesRead = fis.read(header)
              bytesRead > 0
          }
      } catch (e: Exception) {
          false
      }
  }
  ```
- **Why this is a problem**: Calling `FileInputStream(file).read(header)` on any normal file (e.g. an unencrypted JPEG image or PDF) will read up to 16 bytes and set `bytesRead = 16 > 0`, returning `true`. This causes `isEncrypted()` to falsely report unencrypted files as encrypted.
- **Required Fix**: Validate ciphertext integrity by attempting to open the stream via `EncryptedFile` or testing Tink header/magic bytes. For instance:
  ```kotlin
  fun isEncrypted(file: File): Boolean {
      if (!file.exists() || file.length() == 0L) return false
      return try {
          val encryptedFile = EncryptedFile.Builder(
              context,
              file,
              masterKey,
              EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
          ).build()
          encryptedFile.openFileInput().use { fis ->
              fis.read() != -1
          }
      } catch (e: Exception) {
          false
      }
  }
  ```

---

### [Minor / Quality] Finding 3: Incomplete `printPdf` Implementation
- **Location**: `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:90-93`
- **Observation**: `printPdf` retrieves `PrintManager` but contains a placeholder comment.
- **Recommendation**: Since printing is not an M1 requirement, note for future milestones or implement a minimal `PrintDocumentAdapter` delegate.

---

## 3. Verified Claims Matrix

| Feature | Claim | Verification Method | Status | Notes |
|---|---|---|---|---|
| **F1: FileProvider Authority** | Normalized to `${packageName}.fileprovider` | Inspected `Extensions.kt:52`, `PdfGeneratorService.kt:79`, `AndroidManifest.xml:42` | **PASS** | Authority matches `${applicationId}.fileprovider`, `ClipData` and read URI grant flags attached |
| **F2: Singleton DataStore** | Unified single DataStore instance via Hilt | Inspected `AppModule.kt:17,37` and `SettingsRepositoryImpl.kt:21-23` | **PASS** | Removed duplicate delegate; safe enum parsing with fallback defaults implemented |
| **F3: Image Persistence Pipeline** | ML Kit temporary cache streams copied to internal filesDir | Inspected `DocumentRepositoryImpl.kt:35-68` (`persistImageFile`) | **PASS** | Copies `content://` and external cache streams to `context.filesDir/documents/` before Room insert |
| **F4: Storage Shredding** | Physical deletion of image/thumbnail files upon deletion/purge | Inspected `DocumentRepositoryImpl.kt:70-89,187-207,355-374` | **PASS** | `shredPageFiles` called on `permanentlyDelete`, `deletePage`, and `purgeOldTrash` |
| **F5: PDF Export Storage & Sanitation** | Dedicated sanitized cache export directory | Inspected `ViewerViewModel.kt:126-140`, `Extensions.kt:36-38` | **PASS** | Exports written to `cacheDir/pdf_exports/` with `toSafeFileName()_timestamp.pdf` |
| **F6: OCR URI Resolution** | Local file paths parsed with `Uri.fromFile`, recognizer closed | Inspected `ViewerViewModel.kt:73-111` | **PASS** | Uses `Uri.fromFile(file)` for local paths; `recognizer.close()` in success/failure/catch |
| **F7: Biometric AppLock** | Dynamic integration with `settings.appLockEnabled` | Inspected `AppNavigation.kt:47,59`, `AppLockGate.kt:32-121` | **PASS** | Observes DataStore state; prompts biometric/device credentials when enabled |
| **F8: Privacy Manifest** | `allowBackup="false"`, `usesCleartextTraffic="false"`, 0 net perms | Inspected `AndroidManifest.xml:5-17` | **PASS** | No `INTERNET` permission declared, offline isolation enforced |
| **F9: Notification Privacy** | `VISIBILITY_PRIVATE` + generic public notification | Inspected `NotificationService.kt:32-49` | **PASS** | Scan complete notification hides title on lockscreen |
| **F10: Clipboard Sensitivity** | `ClipDescription.EXTRA_IS_SENSITIVE` on API 33+ | Inspected `ViewerViewModel.kt:113-124` | **PASS** | PersistableBundle attached to prevent unmasked clipboard preview |
| **F11: Encryption Heuristic** | Non-empty byte verification | Inspected `EncryptionService.kt:59-70` | **FAIL** | Logic error: returns true for all non-empty files (Finding 2) |
| **F12: Scoped FileProvider Paths** | Restricted `<cache-path>` | Inspected `file_paths.xml:1-15` | **PASS** | Scoped to `pdf_exports/`, `temp/`, `documents/`, `thumbnails/` |
| **F13: Memory-Safe Processing** | Explicit `bitmap.recycle()`, document `close()` in finally | Inspected `PdfGeneratorService.kt:59,73`, `ImageFilterService.kt:17,84` | **PARTIAL** | Memory recycling verified, but missing import in `PdfGeneratorService.kt` (Finding 1) |
| **F14: DB Transactions** | Room composite operations wrapped in `withTransaction` | Inspected `DocumentRepositoryImpl.kt:139,188,198,213,265,356,377,412,420` | **PASS** | ACID transaction safety on multi-table updates; safe null checks in ViewModel |
| **F15: ProGuard Rules** | Log stripping & library keep rules | Inspected `app/proguard-rules.pro:16-33` | **PASS** | `-assumenosideeffects` for `android.util.Log`, keep/dontwarn for ML Kit and Coil 3 |

---

## 4. Adversarial Stress-Testing & Attack Surface Analysis

### Attack Vector 1: Rapid Multi-Scan File Descriptor & Memory Exhaustion
- **Stress Scenario**: User rapidly scans 50 high-resolution pages (12MP each) and triggers PDF generation.
- **Analysis**:
  - `PdfGeneratorService.generatePdf` decodes bitmaps one-by-one with `inSampleSize = 2` (COMPRESSED) or `1` (HIGH) using `RGB_565`.
  - Crucially, `bitmap.recycle()` is called immediately inside `try-finally` after rendering each page onto the `PdfDocument.Canvas`.
  - Peak JVM heap remains bounded to ~1-2 active bitmap frames rather than 50 simultaneous bitmaps in memory.
  - `document.close()` is guaranteed in the outer `finally` block even if an exception occurs during stream write.
- **Verdict**: **ROBUST**.

### Attack Vector 2: DataStore Concurrent Modification & File Locking Races
- **Stress Scenario**: Settings toggled concurrently from background coroutine and UI thread.
- **Analysis**:
  - Pre-existing codebase had two independent `by preferencesDataStore` delegates targeting different files (`"user_settings"` vs `"docscanner_settings"`).
  - Milestone 1 unified DataStore into a single `@Provides @Singleton DataStore<Preferences>` in `AppModule.kt`.
  - All read/write access is mediated through transactional `dataStore.edit { ... }` in `SettingsRepositoryImpl`.
- **Verdict**: **ROBUST**.

### Attack Vector 3: Local File Extraction via Android Backup / ADB
- **Stress Scenario**: Attacker with physical device access runs `adb backup -f backup.ab com.docscanner.app` to extract app-internal documents and Room SQLite databases.
- **Analysis**:
  - `AndroidManifest.xml` enforces `android:allowBackup="false"`. Android OS backup manager rejects extraction requests.
  - App files are stored exclusively in internal sandboxed storage (`context.filesDir/documents/`).
- **Verdict**: **SECURE**.

### Attack Vector 4: Foreign App Intent Interception on Shared PDFs
- **Stress Scenario**: Malicious app registered to receive `ACTION_SEND` attempts to traverse parent directories via file URI.
- **Analysis**:
  - `file_paths.xml` no longer exposes `<cache-path path="/" />`.
  - Shared URIs are generated strictly through `FileProvider.getUriForFile` referencing scoped `pdf_exports/`.
  - Explicit read grants (`FLAG_GRANT_READ_URI_PERMISSION`) and `ClipData.newRawUri` are attached to intents.
- **Verdict**: **SECURE**.

---

## 5. Integrity Violations Audit

- **Hardcoded test fixtures / fake results**: None found.
- **Dummy / facade implementations for assigned features**: None. All 15 features contain genuine logic.
- **Bypassed requirements**: None.
- **Fabricated verification logs**: None.

---

## 6. Action Items for Worker (Remediation)

1. **In `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt`**:
   Add import:
   ```kotlin
   import com.docscanner.app.domain.model.QualityLevel
   ```
2. **In `app/src/main/java/com/docscanner/app/service/encryption/EncryptionService.kt`**:
   Update `isEncrypted(file: File)` to authenticate ciphertext with `EncryptedFile.openFileInput()` inside a `try-catch` rather than reading raw bytes from `FileInputStream`.
