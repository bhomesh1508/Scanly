# Adversarial Challenge Report — Milestone 1: Security Hardening, Storage Safety & Core Architecture

**Target Project**: Scanly Android Document Scanner (`com.docscanner.app`)  
**Milestone**: Milestone 1 (M1)  
**Agent**: Challenger Subagent (`challenger_m1_1`)  
**Parent Agent**: Orchestrator (`e3b71026-e293-4baa-b88d-8f1a46310d8b`)  
**Date**: 2026-08-28  

---

## Challenge Summary

**Overall risk assessment**: **LOW** (All critical attack vectors and edge cases are safely handled and resilient).

**Verdict**: **APPROVE**

---

## Challenges

### [High] Challenge 1: Deletion of Non-Existent or Corrupted Files on Disk (`shredPageFiles`)

- **Assumption Challenged**: Physical file shredding (`shredPageFiles`) might throw unhandled `IOException`, `FileNotFoundException`, or `SecurityException` when operating on deleted, missing, blank, or invalid file paths, which could abort Room `@Transaction` blocks and prevent permanent deletion of documents/pages.
- **Attack Scenario**: 
  1. A document or page has its underlying image file removed by external storage cleaners, OS cache purges, or user file managers.
  2. The user initiates `permanentlyDelete(docId)`, `deletePage(pageId)`, `purgeOldTrash()`, or `splitDocument(docId)`.
- **Blast Radius**: If an exception escapes, the database transaction rolls back, leaving corrupted records permanently stuck in the database and causing UI crashes.
- **Observed Code**: `DocumentRepositoryImpl.kt` lines 70–89:
  ```kotlin
  private fun shredPageFiles(page: PageEntity) {
      runCatching {
          if (page.originalImagePath.isNotBlank()) {
              val f = File(page.originalImagePath)
              if (f.exists()) f.delete()
          }
      }
      runCatching {
          if (page.processedImagePath.isNotBlank()) {
              val f = File(page.processedImagePath)
              if (f.exists()) f.delete()
          }
      }
      runCatching {
          if (page.thumbnailPath.isNotBlank()) {
              val f = File(page.thumbnailPath)
              if (f.exists()) f.delete()
          }
      }
  }
  ```
- **Stress Test Findings**: 
  - Each path (`originalImagePath`, `processedImagePath`, `thumbnailPath`) is tested for `.isNotBlank()`.
  - `f.exists()` is verified before `f.delete()` is called.
  - Every individual deletion is isolated in its own `runCatching { ... }` block. If `originalImagePath` fails or does not exist, it will not prevent `processedImagePath` or `thumbnailPath` from being deleted.
  - No exceptions escape into the enclosing `appDatabase.withTransaction { ... }`.
- **Status**: **PASS / RESILIENT**

---

### [High] Challenge 2: FileProvider Authority and Intent Delegation to External Apps

- **Assumption Challenged**: Exposing internal files via `FileProvider` might allow external applications to access unauthorized directories, perform directory traversal, retain persistent write permissions, or fail at runtime due to authority mismatches.
- **Attack Scenario**: 
  1. Malicious application queries the `FileProvider` directly or intercepts share intents.
  2. Target sharing app attempts to write to or modify internal document storage.
- **Blast Radius**: Exposure of unencrypted local documents, thumbnails, or SQLite database.
- **Observed Code & Manifest**:
  - `AndroidManifest.xml` (lines 40–48):
    ```xml
    <provider
        android:name="androidx.core.content.FileProvider"
        android:authorities="${applicationId}.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
    ```
  - `file_paths.xml` (lines 1–15): Strictly scopes subdirectories: `<files-path path="documents/" />`, `<files-path path="thumbnails/" />`, `<cache-path path="pdf_exports/" />`, `<cache-path path="temp/" />`. Root cache (`path="/"`) is removed.
  - `Extensions.kt` (lines 51–61) & `PdfGeneratorService.kt` (lines 78–87):
    - Authority string is dynamically formed as `"${packageName}.fileprovider"`, matching `${applicationId}.fileprovider`.
    - Only `Intent.FLAG_GRANT_READ_URI_PERMISSION` is attached; `FLAG_GRANT_WRITE_URI_PERMISSION` is omitted.
    - `ClipData.newRawUri("", uri)` is explicitly attached for Android 7.0+ intent chooser propagation.
- **Stress Test Findings**: 
  - `android:exported="false"` prevents direct querying by other apps.
  - Only ephemeral read grants are delegated for the specific target file via the Chooser dialog.
  - Path traversal is blocked by Android's FileProvider path parser against `file_paths.xml`.
- **Status**: **PASS / SECURE**

---

### [Medium] Challenge 3: Scanner URI Persistence with Invalid, Empty, or Corrupted URIs

- **Assumption Challenged**: Passing empty strings, invalid schemes, missing files, or inaccessible `content://` URIs to `persistImageFile` could cause unhandled I/O exceptions during document creation or page addition.
- **Attack Scenario**: ML Kit scanner returns an invalid or expired temporary URI, or user passes an empty/malformed URI string.
- **Blast Radius**: Application crash on scan completion or document creation.
- **Observed Code**: `DocumentRepositoryImpl.kt` lines 35–68:
  ```kotlin
  private fun persistImageFile(docId: String, pageIndex: Int, sourceUriOrPath: String): String {
      return try {
          val documentsDir = File(context.filesDir, Constants.DOCUMENTS_DIR).apply { mkdirs() }
          val destFile = File(documentsDir, "${docId}_page_${pageIndex}_${System.currentTimeMillis()}.jpg")

          if (sourceUriOrPath.startsWith("content://")) {
              val uri = Uri.parse(sourceUriOrPath)
              context.contentResolver.openInputStream(uri)?.use { input ->
                  FileOutputStream(destFile).use { output ->
                      input.copyTo(output)
                  }
              }
              destFile.absolutePath
          } else {
              val sourceFile = File(sourceUriOrPath)
              if (sourceFile.exists() && sourceFile.absolutePath != destFile.absolutePath) {
                  sourceFile.copyTo(destFile, overwrite = true)
                  destFile.absolutePath
              } else if (sourceFile.exists()) {
                  sourceFile.absolutePath
              } else {
                  val uri = Uri.parse(sourceUriOrPath)
                  context.contentResolver.openInputStream(uri)?.use { input ->
                      FileOutputStream(destFile).use { output ->
                          input.copyTo(output)
                      }
                  }
                  if (destFile.exists() && destFile.length() > 0L) destFile.absolutePath else sourceUriOrPath
              }
          }
      } catch (e: Exception) {
          sourceUriOrPath
      }
  }
  ```
- **Stress Test Findings**:
  - Empty string `""`: Handled safely, returns fallback `""`.
  - Non-existent local file `"/fake/path.jpg"`: Handled safely, returns original path without crash.
  - Inaccessible `content://` URI: Caught by `catch (e: Exception)` if security/resolver error thrown; returns safely. Downstream components (Viewer OCR, PDF generator, Coil) check `File(path).exists()` and report "Image file not found" gracefully.
- **Status**: **PASS / RESILIENT**

---

### [High] Challenge 4: Special Character Sanitization in PDF Export (`exportPdf`)

- **Assumption Challenged**: Document titles containing slashes (`/`, `\`), colons (`:`), null bytes (`\u0000`), control characters (`\n`, `\r`), emojis, unicode, or path traversal tokens (`../../`) could cause path traversal outside `cacheDir/pdf_exports`, native file truncation, or filesystem I/O crashes.
- **Attack Scenario**: User exports a PDF with title `"../../etc/passwd"`, `"Invoice: 2026/08/28\u0000Confidential"`, or empty title `""`.
- **Blast Radius**: File creation error, directory traversal, or file overwrite outside intended cache.
- **Observed Code**: `Extensions.kt` line 36–38 & `ViewerViewModel.kt` line 131–135:
  ```kotlin
  fun String.toSafeFileName(): String {
      return this.replace(Regex("[^a-zA-Z0-9.-]"), "_")
  }
  ```
  ```kotlin
  val currentDoc = _document.value
  val title = (currentDoc?.title ?: "Document").toSafeFileName()
  val exportDir = File(ctx.cacheDir, Constants.PDF_EXPORTS_DIR).apply { mkdirs() }
  val outputFile = File(exportDir, "${title}_${System.currentTimeMillis()}.pdf")
  ```
- **Stress Test Findings**:
  - `../../etc/passwd` -> `.._.._etc_passwd` (Slashes stripped; strictly contained inside `exportDir`).
  - `Invoice: 2026/08/28\u0000Doc` -> `Invoice__2026_08_28_Doc` (Colons, slashes, null bytes stripped).
  - Empty string `""` -> `_${timestamp}.pdf` (Guaranteed non-empty and unique via timestamp).
  - Emoji & unicode `Scanned 📑 Notes & Taxes` -> `Scanned___Notes___Taxes`.
- **Status**: **PASS / ROBUST**

---

## Stress Test Results

| Scenario | Input / Action | Expected Behavior | Actual Behavior | Result |
|---|---|---|---|---|
| Non-existent file deletion | `shredPageFiles` on deleted file | No crash, silent graceful ignore | Handled safely via `runCatching` and `f.exists()` | **PASS** |
| FileProvider authority matching | `shareFile` / `sharePdf` | Authority matches `${packageName}.fileprovider` | Perfectly matches `${applicationId}.fileprovider` | **PASS** |
| Scoped file sharing | External app share intent | Read-only transient grant, scoped to `pdf_exports/` | `FLAG_GRANT_READ_URI_PERMISSION` + `ClipData` | **PASS** |
| Invalid URI in persistence | `persistImageFile("", ...)` | No uncaught exception, fallback string | Handled safely, 0 exceptions | **PASS** |
| Malicious PDF title | `../../etc/passwd:null\u0000` | Sanitized to `.._.._etc_passwd_null_` | Strict regex replacement to `_` | **PASS** |
| DataStore corrupted enum | Invalid string in Preferences | Fallback to default enum value | `runCatching { Enum.valueOf(...) }.getOrDefault(...)` | **PASS** |
| Lockscreen scan notification | Document scan completion | No title exposure on lockscreen | `VISIBILITY_PRIVATE` + generic public notification | **PASS** |
| Database ACID transactions | Multi-table delete / purge / split | Atomic commit or rollback | All wrapped in `appDatabase.withTransaction` | **PASS** |
| PDF bitmap allocation | Multi-page PDF generation | Bitmaps recycled, document closed | `bitmap.recycle()` and `try-finally document.close()` | **PASS** |

---

## Unchallenged Areas

- **Live Camera Hardware / ML Kit Model Download**: On-device physical camera scanner integration is verified structurally; live camera capture requires on-device hardware execution.
- **Biometric Hardware Keystore**: Tested via `BiometricPrompt` framework contracts with fallback to device credentials; physical biometric sensor verification occurs on-device.

---

## Recommendation & Verdict

**Verdict**: **APPROVE**  
Milestone 1 successfully fulfills all security, storage safety, and architectural hardening requirements. Ready to proceed to Milestone 2.
