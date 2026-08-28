# Scanly Security & Privacy Audit Report

**Target**: Scanly Offline Android Document Scanner  
**Auditor**: Android Security & Privacy Specialist Explorer  
**Date**: 2026-08-28  
**Scope**: Codebase analysis covering Manifest, Storage, Zero-Leakage Privacy, Logging, and IPC/Intents.

---

## 1. Executive Summary

A comprehensive security, privacy, and architectural audit was performed on the Scanly offline Android application (`com.docscanner.app`). The application exhibits an excellent baseline offline design philosophy—containing **zero internet permissions**, **no third-party telemetry/tracking SDKs**, and on-device machine learning processing (ML Kit Document Scanner & Text Recognition).

However, our deep-dive analysis revealed several critical and high-severity security vulnerabilities, data leakage vectors, and logic flaws that must be remediated:
1. **Critical Storage Privacy Violation**: Permanent deletion and trash purging only remove SQLite metadata records while leaving raw document images and generated PDFs permanently unlinked and unencrypted on device storage.
2. **High Severity App Lock Bypass**: Biometric App Lock gate in navigation is hardcoded to `false`, rendering the user setting completely non-functional.
3. **High Severity FileProvider Crash/Mismatch**: FileProvider authority in `Extensions.kt` (`$packageName.provider`) differs from `AndroidManifest.xml` (`${applicationId}.fileprovider`), crashing the app on file share.
4. **High Severity Path Traversal/Storage Crash in PDF Export**: `ViewerViewModel.kt` invokes relative path `File("dummy.pdf")` resolving to root filesystem `/dummy.pdf`, causing runtime `FileNotFoundException / Permission Denied`.
5. **Medium Severity Data Privacy Exposures**: `allowBackup="true"` without backup rules, lock screen notification title leaks, clipboard sensitive flag omission on OCR export, and an invalid `isEncrypted` heuristic.

---

## 2. Findings Matrix

| ID | Category | Severity | Vulnerability / Issue | Location |
|---|---|---|---|---|
| **SEC-01** | Storage & Privacy | **CRITICAL** | Orphaned Sensitive Files on Delete / No Physical Shredding | `DocumentRepositoryImpl.kt:113-121` |
| **SEC-02** | Auth & Access | **HIGH** | Biometric App Lock Gate Hardcoded Disabled (`isEnabled = false`) | `AppNavigation.kt:55` |
| **SEC-03** | IPC & Components | **HIGH** | FileProvider Authority Mismatch Causing Runtime Crash | `Extensions.kt:52` vs `AndroidManifest.xml:41` |
| **SEC-04** | Storage & I/O | **HIGH** | Insecure Relative File Creation in PDF Export (`File("dummy.pdf")`) | `ViewerViewModel.kt:99` |
| **SEC-05** | Storage & Lifecycles | **HIGH** | Insecure Ephemeral Scanner URI Persistence | `ScannerViewModel.kt:45-48` |
| **SEC-06** | Privacy & Backup | **MEDIUM** | Unrestricted App Backup (`allowBackup="true"`) Leaking Unencrypted DB | `AndroidManifest.xml:16` |
| **SEC-07** | Privacy & UI | **MEDIUM** | Lockscreen Document Title Leakage via Notifications | `NotificationService.kt:31-40` |
| **SEC-08** | Privacy & Clipboard | **MEDIUM** | Sensitive Clipboard Exposure for OCR Text (Missing `EXTRA_IS_SENSITIVE`) | `ViewerViewModel.kt:91-96` |
| **SEC-09** | Cryptography | **MEDIUM** | Broken `isEncrypted` Detection Heuristic | `EncryptionService.kt:59-70` |
| **SEC-10** | Storage & IPC | **LOW** | Overly Broad FileProvider Cache Path Exposure | `file_paths.xml:10` |
| **SEC-11** | Build & Hardening | **LOW** | Missing ProGuard Log-Stripping and Network Cleartext Flag | `build.gradle.kts` / `AndroidManifest.xml` |

---

## 3. Deep-Dive Vulnerability Analysis & Hardening Patches

### SEC-01: Orphaned Sensitive Files on Delete & Trash Purging (CRITICAL)

#### Vulnerability Details
When a user permanently deletes a document or purges trash, `DocumentRepositoryImpl.kt` executes SQL delete queries:
```kotlin
override suspend fun permanentlyDelete(docId: String) = withContext(Dispatchers.IO) {
    documentDao.delete(docId)
    pageDao.deleteByDocument(docId)
}
```
**Impact**: The database rows are deleted, but the underlying physical files (`originalImagePath`, `processedImagePath`, `thumbnailPath`, PDF exports) remain on disk in internal storage (`files/documents/`, `files/thumbnails/`, `cache/`). On devices without full disk encryption or if temporary storage is inspected, sensitive documents (banking records, IDs, medical scans) remain indefinitely recoverable.

#### Recommended Remediation Patch
Before deleting the database records, retrieve all associated page entities and invoke `File(path).delete()` to physically wipe the files from internal storage.

```kotlin
// Target: app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt
override suspend fun permanentlyDelete(docId: String) = withContext(Dispatchers.IO) {
    val pages = pageDao.getPagesForDocumentSync(docId)
    pages.forEach { page ->
        runCatching { File(page.originalImagePath).delete() }
        runCatching { File(page.processedImagePath).delete() }
        runCatching { File(page.thumbnailPath).delete() }
    }
    documentDao.delete(docId)
    pageDao.deleteByDocument(docId)
}

override suspend fun deletePage(pageId: String) = withContext(Dispatchers.IO) {
    val page = pageDao.getPageById(pageId)
    page?.let {
        runCatching { File(it.originalImagePath).delete() }
        runCatching { File(it.processedImagePath).delete() }
        runCatching { File(it.thumbnailPath).delete() }
    }
    pageDao.delete(pageId)
}
```

---

### SEC-02: Biometric App Lock Gate Hardcoded Disabled (HIGH)

#### Vulnerability Details
In `AppNavigation.kt` line 55:
```kotlin
AppLockGate(isEnabled = false) {
    Scaffold(...) { ... }
}
```
**Impact**: Even if the user explicitly navigates to Settings and enables `App Lock` (`settings.appLockEnabled = true`), the gate is hardcoded with `isEnabled = false`. No biometric or device credential authentication is ever prompted, leaving the user's sensitive documents unprotected against unauthorized physical device access.

#### Recommended Remediation Patch
Observe the settings flow in `AppNavigation.kt` (or via a navigation-level ViewModel) and pass `settings.appLockEnabled` dynamically to `AppLockGate`:

```kotlin
// Target: app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt
@Composable
fun AppNavigation(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsState()
    ...
    AppLockGate(isEnabled = settings.appLockEnabled) {
        ...
    }
}
```

---

### SEC-03: FileProvider Authority Mismatch Causing Runtime Crash (HIGH)

#### Vulnerability Details
- `AndroidManifest.xml` line 41 declares: `android:authorities="${applicationId}.fileprovider"`
- `PdfGeneratorService.kt` line 65 uses: `FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)`
- `Extensions.kt` line 52 uses: `FileProvider.getUriForFile(this, "$packageName.provider", file)`

**Impact**: Calling `Context.shareFile()` invokes authority `$packageName.provider`, which does not exist in the manifest. Android throws `IllegalArgumentException: Couldn't find meta-data for provider with authority com.docscanner.app.provider`, immediately crashing the app.

#### Recommended Remediation Patch
Align all FileProvider authority references with `${context.packageName}.fileprovider` and define a single constant in `Constants.kt`.

```kotlin
// Target: app/src/main/java/com/docscanner/app/util/Constants.kt
const val FILE_PROVIDER_AUTHORITY_SUFFIX = ".fileprovider"

// Target: app/src/main/java/com/docscanner/app/util/Extensions.kt
fun Context.shareFile(file: File, mimeType: String) {
    val authority = "${packageName}.fileprovider"
    val uri = FileProvider.getUriForFile(this, authority, file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        clipData = android.content.ClipData.newRawUri("", uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Share File"))
}
```

---

### SEC-04: Insecure Relative File Creation in PDF Export (HIGH)

#### Vulnerability Details
In `ViewerViewModel.kt` lines 98–102:
```kotlin
fun exportPdf(options: PdfExportOptions): Result<File> {
    val file = File("dummy.pdf")
    pdfGeneratorService.generatePdf(_pages.value, options, file)
    return Result.success(file)
}
```
**Impact**: On Android, creating `File("dummy.pdf")` evaluates to the filesystem root path `/dummy.pdf`, which is non-writable by non-root applications. Attempting to write the PDF triggers a fatal `FileNotFoundException / Permission Denied` failure. Additionally, the file name is hardcoded rather than sanitized based on document title and timestamp.

#### Recommended Remediation Patch
Generate PDF export files in `context.cacheDir` or `context.filesDir` within a dedicated `pdf_exports` directory using a sanitized file name.

```kotlin
// Target: app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt
fun exportPdf(context: Context, options: PdfExportOptions): Result<File> {
    val currentDoc = _document.value
    val title = (currentDoc?.title ?: "Document").toSafeFileName()
    val exportDir = File(context.cacheDir, "pdf_exports").apply { mkdirs() }
    val outputFile = File(exportDir, "${title}_${System.currentTimeMillis()}.pdf")
    return pdfGeneratorService.generatePdf(_pages.value, options, outputFile)
}
```

---

### SEC-05: Insecure Ephemeral Scanner URI Persistence (HIGH)

#### Vulnerability Details
In `ScannerViewModel.kt` lines 42–53:
```kotlin
fun createDocument(title: String, onDocumentCreated: (String) -> Unit) {
    viewModelScope.launch {
        val pagePaths = _scannedPages.value.map { it.toString() }
        val pdfPath = _pdfUri.value?.toString()
        val document = documentRepository.createDocument(title, pagePaths, pdfPath)
        onDocumentCreated(document.id)
    }
}
```
**Impact**: `GmsDocumentScanningResult` returns temporary content URIs from the Google Play Services provider. If the app stores `uri.toString()` directly:
1. Temporary URI permissions granted by Play Services expire or are revoked upon app restart.
2. Downstream rendering and PDF generation (`BitmapFactory.decodeFile(page.processedImagePath)`) fail because the string is a content URI, not a local file path.

#### Recommended Remediation Patch
Copy the content from `scannedPages` URIs into the app's internal persistent directory (`context.filesDir/documents/`) as permanent JPEG files when creating documents.

---

### SEC-06: Unrestricted App Backup Leaking Unencrypted Data (MEDIUM)

#### Vulnerability Details
In `AndroidManifest.xml` line 16:
```xml
android:allowBackup="true"
```
**Impact**: With `allowBackup="true"` and no `android:fullBackupContent` or `android:dataExtractionRules` defined, Android Backup service and `adb backup` will copy the unencrypted Room SQLite database (`docscanner_db`), DataStore preferences, and internal files into cloud / local backups without user PIN/biometric authorization.

#### Recommended Remediation Patch
For a local, high-security, offline privacy scanner, disable cloud/ADB backup or restrict backup rules:

```xml
<!-- Target: app/src/main/AndroidManifest.xml -->
<application
    android:name=".DocScannerApp"
    android:allowBackup="false"
    android:usesCleartextTraffic="false"
    android:icon="@drawable/app_logo"
    android:label="@string/app_name"
    android:roundIcon="@drawable/app_logo"
    android:supportsRtl="true"
    android:theme="@style/Theme.DocScanner"
    tools:targetApi="31">
```

---

### SEC-07: Lockscreen Document Title Leakage via Notifications (MEDIUM)

#### Vulnerability Details
In `NotificationService.kt` lines 31–40:
```kotlin
fun showScanCompleteNotification(documentTitle: String) {
    val builder = NotificationCompat.Builder(context, "scan_channel")
        .setSmallIcon(android.R.drawable.ic_menu_camera)
        .setContentTitle("Scan Complete")
        .setContentText("Successfully scanned: $documentTitle")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(SCAN_NOTIFICATION_ID, builder.build())
}
```
**Impact**: Scanned documents often have sensitive titles (e.g. `"Medical Diagnosis - Dr Smith.pdf"`, `"Passport 2026.pdf"`, `"Divorce Agreement.pdf"`). Without `setVisibility(NotificationCompat.VISIBILITY_PRIVATE)`, notification text displays on device lockscreens by default, exposing confidential information to bystanders.

#### Recommended Remediation Patch
Add `setVisibility(NotificationCompat.VISIBILITY_PRIVATE)` and optionally attach a generic public notification for lockscreens:

```kotlin
// Target: app/src/main/java/com/docscanner/app/service/notification/NotificationService.kt
fun showScanCompleteNotification(documentTitle: String) {
    val publicNotification = NotificationCompat.Builder(context, Constants.SCAN_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_camera)
        .setContentTitle("Scan Complete")
        .setContentText("A new document was scanned successfully.")
        .build()

    val builder = NotificationCompat.Builder(context, Constants.SCAN_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_camera)
        .setContentTitle("Scan Complete")
        .setContentText("Successfully scanned: $documentTitle")
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .setPublicVersion(publicNotification)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notificationManager.notify(SCAN_NOTIFICATION_ID, builder.build())
}
```

---

### SEC-08: Sensitive Clipboard Exposure on OCR Text Copy (MEDIUM)

#### Vulnerability Details
In `ViewerViewModel.kt` lines 91–96:
```kotlin
fun copyOcrText(context: Context) {
    val text = _ocrText.value ?: return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("OCR Text", text)
    clipboard.setPrimaryClip(clip)
}
```
**Impact**: On Android 13+ (API 33+), copying sensitive text without the `ClipDescription.EXTRA_IS_SENSITIVE` extra causes Android's visual clipboard preview to render the full text content in plaintext on screen, and stores it unmasked in third-party clipboard managers.

#### Recommended Remediation Patch
Flag the ClipData with `ClipDescription.EXTRA_IS_SENSITIVE` on API 33+:

```kotlin
// Target: app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt
fun copyOcrText(context: Context) {
    val text = _ocrText.value ?: return
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("OCR Text", text).apply {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            description.extras = android.os.PersistableBundle().apply {
                putBoolean(android.content.ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }
    }
    clipboard.setPrimaryClip(clip)
}
```

---

### SEC-09: Broken `isEncrypted` Heuristic (MEDIUM)

#### Vulnerability Details
In `EncryptionService.kt` lines 59–70:
```kotlin
fun isEncrypted(file: File): Boolean {
    if (!file.exists()) return false
    return try {
        FileInputStream(file).use { fis ->
            val header = ByteArray(4)
            fis.read(header)
            header.isNotEmpty() // <-- BUG: Always true! ByteArray(4).isNotEmpty() == true
        }
    } catch (e: Exception) {
        false
    }
}
```
**Impact**: `ByteArray(4).isNotEmpty()` checks the allocated size of the array (`4`), not the actual bytes read (`read(header) > 0`) or encryption header magic numbers. Any existing non-zero-byte file is misclassified as encrypted.

#### Recommended Remediation Patch
Check `bytesRead > 0` and verify the cryptographic header or check against Jetpack Security's `EncryptedFile` metadata format:

```kotlin
// Target: app/src/main/java/com/docscanner/app/service/encryption/EncryptionService.kt
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

---

### SEC-10: Overly Broad FileProvider Cache Path Exposure (LOW)

#### Vulnerability Details
In `app/src/main/res/xml/file_paths.xml` line 10:
```xml
<!-- Cache directory for temporary files (PDF exports, etc.) -->
<cache-path name="cache" path="/" />
```
**Impact**: Exposing `path="/"}` grants potential access to all cached files within the application's cache directory if a URI is shared.

#### Recommended Remediation Patch
Narrow down `<cache-path>` to specific subdirectories:

```xml
<!-- Target: app/src/main/res/xml/file_paths.xml -->
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <!-- Internal app storage for documents -->
    <files-path name="documents" path="documents/" />

    <!-- Internal app storage for thumbnails -->
    <files-path name="thumbnails" path="thumbnails/" />

    <!-- Dedicated cache directory for PDF exports -->
    <cache-path name="pdf_exports" path="pdf_exports/" />

    <!-- Temp directory for scanner output -->
    <cache-path name="temp" path="temp/" />
</paths>
```

---

### SEC-11: ProGuard Rules & Release Build Hardening (LOW)

#### Vulnerability Details
In `app/proguard-rules.pro`, there are no explicit log-stripping rules (`-assumenosideeffects`), leaving the potential for debug logs or sensitive call traces to be included if future debug statements are added.

#### Recommended Remediation Patch
Add ProGuard stripping rules:

```proguard
# Target: app/proguard-rules.pro
# Strip Logcat invocations in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
```

---

## 4. Privacy & Zero-Leakage Audit Summary

| Privacy Metric | Status | Evaluation |
|---|---|---|
| **Internet Access** | **VERIFIED CLEAN** | No `android.permission.INTERNET` in `AndroidManifest.xml`. |
| **Tracking / Telemetry** | **VERIFIED CLEAN** | No third-party ad/analytics SDKs (no Firebase Analytics, Facebook, Adjust, AppsFlyer). |
| **ML Processing** | **VERIFIED CLEAN** | On-device Google ML Kit OCR and Document Scanner dependencies only. |
| **Storage Isolation** | **ACTION REQUIRED** | Implement physical file deletion in `DocumentRepositoryImpl` on trash purge / delete. |
| **Clipboard Security** | **ACTION REQUIRED** | Set `EXTRA_IS_SENSITIVE` on OCR copied text for Android 13+. |
| **Lockscreen Privacy** | **ACTION REQUIRED** | Set `VISIBILITY_PRIVATE` on document scan notifications. |

---

## 5. Concrete Action Items for Implementers

1. **Fix `DocumentRepositoryImpl.kt`**: Add physical `File.delete()` logic in `permanentlyDelete`, `deletePage`, and `purgeOldTrash`.
2. **Fix `AppNavigation.kt`**: Connect `AppLockGate` to `settings.appLockEnabled`.
3. **Fix `Extensions.kt`**: Correct FileProvider authority to `"${packageName}.fileprovider"` and include `ClipData.newRawUri`.
4. **Fix `ViewerViewModel.kt`**: Change `exportPdf` to use `context.cacheDir/pdf_exports/` and mark `copyOcrText` as sensitive.
5. **Fix `ScannerViewModel.kt`**: Persist scan result image streams into `context.filesDir/documents/`.
6. **Update `AndroidManifest.xml`**: Set `allowBackup="false"` and `usesCleartextTraffic="false"`.
7. **Update `NotificationService.kt`**: Set `VISIBILITY_PRIVATE` on scan complete notifications.
8. **Update `file_paths.xml`**: Scope cache path to `pdf_exports/`.
9. **Update `EncryptionService.kt`**: Fix `isEncrypted` logic.
10. **Update `proguard-rules.pro`**: Add Log stripping rules.
