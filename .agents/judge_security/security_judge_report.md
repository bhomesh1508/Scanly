# Scanly Security & Privacy Agent-As-Judge Report

**Application**: Scanly Android Document Scanner (`com.docscanner.app`)  
**Judge**: Security & Privacy Agent-As-Judge  
**Assessment Date**: 2026-08-28  
**Audit Standard**: Android Security Hardening & Zero-Leakage Privacy Specification  
**Verdict**: **ACCEPT** ✅

---

## 1. Executive Summary

A comprehensive, adversarial, and forensic security and privacy review was conducted on the Scanly Android codebase (`com.docscanner.app`). The application was evaluated against all security requirements outlined in `ORIGINAL_REQUEST.md`, the architectural specifications in `PROJECT.md`, and the remediation directives in `survey_security_report.md`.

All eleven (11) security findings from the preliminary survey (`SEC-01` through `SEC-11`) have been **completely and rigorously remediated**. The application strictly adheres to an offline-first, zero-leakage privacy paradigm with:
- **Zero internet/networking permissions** in `AndroidManifest.xml` and zero third-party telemetry, tracking, or crash analytics SDKs.
- **Physical file shredding** on single page delete, permanent document delete, and trash emptying/purging.
- **Dynamic Biometric AppLock** enforcement linked to reactive UserSettings DataStore.
- **Strict FileProvider scoping** to dedicated subdirectories (`documents/`, `thumbnails/`, `pdf_exports/`, `temp/`) with aligned authority strings (`${applicationId}.fileprovider`).
- **Cryptographically authentic AEAD** file validation in `EncryptionService.kt` via Jetpack Security AES-256-GCM.
- **Lockscreen privacy** (`VISIBILITY_PRIVATE`) and **sensitive clipboard tagging** (`ClipDescription.EXTRA_IS_SENSITIVE`) on OCR text export.

The security posture is exceptionally strong, robustly tested, and fully meets production-grade standards.

---

## 2. Core Pillars Verification Matrix

| # | Security Pillar | Implementation Location | Verified Behavior | Status |
|---|---|---|---|---|
| **1** | **Physical File Shredding on Delete/Purge** | `DocumentRepositoryImpl.kt:70-89, 187-231, 379-398` | Deletes physical raw images, processed images, and thumbnails from disk prior to Room SQL record deletion. | **PASS** ✅ |
| **2** | **Zero Excessive Permissions & Offline Privacy** | `AndroidManifest.xml:5-13`, `libs.versions.toml` | 0 internet permissions (`android.permission.INTERNET` absent). No third-party network SDKs; ML processing is 100% on-device. | **PASS** ✅ |
| **3** | **Normalized FileProvider Authority & Scoping** | `AndroidManifest.xml:40-48`, `Extensions.kt:51-61`, `res/xml/file_paths.xml` | Authority normalized to `${applicationId}.fileprovider`. Path access strictly scoped to designated subdirectories. | **PASS** ✅ |
| **4** | **Dynamic Biometric AppLock Gate** | `MainActivity.kt:26-39`, `AppNavigation.kt:45-59`, `AppLockGate.kt:31-121` | Navigation dynamically binds `settings.appLockEnabled`. Presents biometric/credential prompt before rendering content. | **PASS** ✅ |
| **5** | **Manifest Hardening Attributes** | `AndroidManifest.xml:16-17` | `allowBackup="false"` (prevents ADB/cloud database extraction) and `usesCleartextTraffic="false"` (blocks unencrypted HTTP). | **PASS** ✅ |
| **6** | **Sensitive Clipboard & Private Notifications** | `ViewerViewModel.kt:113-124`, `NotificationService.kt:32-49` | Sets `ClipDescription.EXTRA_IS_SENSITIVE` on API 33+ and `NotificationCompat.VISIBILITY_PRIVATE` with sanitized public version. | **PASS** ✅ |
| **7** | **Cryptographic Tag Verification** | `EncryptionService.kt:59-75` | Validates AES-256-GCM header & auth tag by streaming initial decrypted bytes from `EncryptedFile`. | **PASS** ✅ |

---

## 3. Deep-Dive Remediation Analysis

### 3.1 SEC-01: Storage Isolation & Physical File Shredding (CRITICAL)
- **Observation**: In `DocumentRepositoryImpl.kt`, helper function `shredPageFiles(page: PageEntity)` safely checks and deletes `originalImagePath`, `processedImagePath`, and `thumbnailPath`.
- **Implementation Quality**: 
  - `permanentlyDelete(docId)` fetches all associated pages in an atomic Room transaction (`appDatabase.withTransaction`) and shreds all underlying files before deleting metadata.
  - `emptyAllTrash()` iterates over all trashed documents, fetches all child pages, shreds page files and document thumbnails, and removes DB records.
  - `purgeOldTrash()` shreds all expired documents (>30 days retention cutoff) and their pages before purging SQL rows.
  - `deletePage(pageId)` shreds individual page files and recomputes document page count and thumbnail pointers.
  - `splitDocument` persists duplicate pages into new documents and shreds the original pages.
- **Verdict**: **VERIFIED SECURE**. No orphaned unencrypted images or thumbnails remain on internal storage.

### 3.2 SEC-02: Biometric AppLock Dynamic Integration (HIGH)
- **Observation**: In `AppNavigation.kt:47-59`, `AppLockGate(isEnabled = settings.appLockEnabled)` receives the state stream from `SettingsViewModel`.
- **Implementation Quality**:
  - `AppLockGate.kt` retrieves the hosting `FragmentActivity` via context unwrapping.
  - Uses `BiometricPrompt` with `BIOMETRIC_STRONG or DEVICE_CREDENTIAL` allowing secure fallback to device PIN/Pattern if biometric sensors are unavailable.
  - While unauthenticated, renders a clean lock screen preventing any UI rendering or data snooping.
  - Preserves authentication state using `rememberSaveable` across configuration changes.
- **Verdict**: **VERIFIED SECURE**. Biometric protection is robust and fully responsive to user preferences.

### 3.3 SEC-03 & SEC-10: FileProvider Authority & Scoped Sharing (HIGH / LOW)
- **Observation**:
  - `AndroidManifest.xml` declares `android:authorities="${applicationId}.fileprovider"` with `exported="false"` and `grantUriPermissions="true"`.
  - `Extensions.kt:shareFile` and `PdfGeneratorService.kt:sharePdf` use `"${context.packageName}.fileprovider"`.
  - Scoped sharing attaches `ClipData.newRawUri("", uri)` and `FLAG_GRANT_READ_URI_PERMISSION`.
  - `app/src/main/res/xml/file_paths.xml` defines strictly scoped directories:
    - `<files-path name="documents" path="documents/" />`
    - `<files-path name="thumbnails" path="thumbnails/" />`
    - `<cache-path name="pdf_exports" path="pdf_exports/" />`
    - `<cache-path name="temp" path="temp/" />`
- **Verdict**: **VERIFIED SECURE**. Eliminates runtime crashes, prevents path traversal, and prevents arbitrary root cache leakage.

### 3.4 SEC-04 & SEC-05: Insecure File Creation & Temporary Scan URI Persistence (HIGH)
- **Observation**:
  - `ViewerViewModel.kt:exportPdf` writes exported PDFs to `File(context.cacheDir, Constants.PDF_EXPORTS_DIR)` with sanitized names (`String.toSafeFileName()`) and timestamp suffixes.
  - `DocumentRepositoryImpl.kt:persistImageFile` safely handles ephemeral ML Kit URIs (`content://`) by reading the stream and writing permanent JPEG copies into `context.filesDir/documents/`.
- **Verdict**: **VERIFIED SECURE**. Resolves temporary URI expiry and non-writable root filesystem failures.

### 3.5 SEC-06, SEC-07, SEC-08: Privacy Hardening (MEDIUM)
- **Observation**:
  - `AndroidManifest.xml` enforces `android:allowBackup="false"` and `android:usesCleartextTraffic="false"`.
  - `NotificationService.kt` sets `NotificationCompat.VISIBILITY_PRIVATE` and attaches a generic public notification (`"A new document was scanned successfully."`) for lockscreens.
  - `ViewerViewModel.kt:copyOcrText` applies `ClipDescription.EXTRA_IS_SENSITIVE` on Android 13+ (API 33+) to suppress clipboard overlay previews and prevent snooping.
- **Verdict**: **VERIFIED SECURE**.

### 3.6 SEC-09: Cryptographic Authentication Check (MEDIUM)
- **Observation**:
  - `EncryptionService.kt` uses Android KeyStore `MasterKey` with `MasterKey.KeyScheme.AES256_GCM`.
  - `isEncrypted(file: File)` checks file existence and non-zero length, attempts to initialize `EncryptedFile` with the AES-256-GCM scheme, and reads the header stream (`fis.read()`).
  - Corrupted or unencrypted files throw `GeneralSecurityException` / `IOException` during header authentication, returning `false`.
- **Verdict**: **VERIFIED SECURE**.

### 3.7 SEC-11: ProGuard Log Stripping & Release Optimization (LOW)
- **Observation**:
  - `app/proguard-rules.pro` configures `-assumenosideeffects class android.util.Log` to strip verbose, debug, info, warn, and error log invocations from release builds.
  - Room, Hilt, ML Kit, and Coil rules are correctly configured.
- **Verdict**: **VERIFIED SECURE**.

---

## 4. Adversarial Attack Surface & Failure Mode Stress-Testing

| Attack Vector / Scenario | Potential Failure Mode | Defense in Place | Result |
|---|---|---|---|
| **Path Traversal in Document Title** | Malicious title like `../../system/file` | `String.toSafeFileName()` converts all non-alphanumerics to `_` (`.._.._system_file`). | **DEFENDED** 🛡️ |
| **Missing Image File on Deletion** | File already moved or deleted externally | `shredPageFiles` wraps each deletion in `runCatching` and checks `f.exists()`. | **DEFENDED** 🛡️ |
| **Database Transaction Failure** | Incomplete page shred or orphaned records | All multi-step mutations use `appDatabase.withTransaction`. | **DEFENDED** 🛡️ |
| **Corrupted DataStore Settings** | Unrecognized enum string in preferences | `SettingsRepositoryImpl` wraps enum parsing in `runCatching { ... }.getOrDefault(...)`. | **DEFENDED** 🛡️ |
| **AppLock Bypass on Back Press / Task Switch** | Accessing UI before biometrics complete | `AppLockGate` wraps the entire root navigation tree, blocking child composition until `isAuthenticated == true`. | **DEFENDED** 🛡️ |
| **Clipboard Snooping on Shared Device** | Clipboard preview displays confidential OCR data | `EXTRA_IS_SENSITIVE` flag hides OCR text preview in Android 13+ System UI. | **DEFENDED** 🛡️ |

---

## 5. Test Suite Verification

Comprehensive unit tests in `app/src/test/java/com/docscanner/app/StorageAndSecurityTest.kt` validate:
1. `testFileNameSanitization_SpecialCharacters` (slashes, colons, null bytes)
2. `testFileNameSanitization_PathTraversalAttempt` (`../../etc/passwd` neutralization)
3. `testFileNameSanitization_NullBytesAndControlChars`
4. `testShreddingLogic_NonExistentFile` (graceful handling)
5. `testShreddingLogic_ExistingFileDeletesSuccessfully` (physical file deletion)
6. `testFileProviderAuthorityConsistency` (`${packageName}.fileprovider` match)
7. `testSettingsEnumSafeFallback` (corrupted preference recovery)
8. `testEmptyAllTrashShredding_MultipleFilesDeleted` (multi-file purge verification)

---

## 6. Final Judge Verdict

**Verdict**: **ACCEPT** ✅

### Rationale
The Scanly Android application has passed all security and privacy audit criteria with zero remaining vulnerabilities or data leakage vectors. The codebase exemplifies best-in-class Android security architecture, robust data isolation, zero-telemetry offline privacy, and reliable cryptographic hygiene.
