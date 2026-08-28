# Milestone 1 Review Handoff Report: Security Hardening, Storage Safety & Core Architecture

**Target Project**: Scanly Android Document Scanner (`com.docscanner.app`)  
**Milestone**: Milestone 1 (M1)  
**Agent**: Reviewer 2 (`reviewer_m1_2`)  
**Recipient**: Orchestrator / Parent Agent (`e3b71026-e293-4baa-b88d-8f1a46310d8b`)  
**Date**: 2026-08-28  

---

## 1. Observation

Direct code analysis of the Milestone 1 deliverables was performed across all 17 modified files:

1. **Missing Import in `PdfGeneratorService.kt`**:
   - In `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:9-12`:
     ```kotlin
     import com.docscanner.app.domain.model.MarginPreset
     import com.docscanner.app.domain.model.Page
     import com.docscanner.app.domain.model.PageSize
     import com.docscanner.app.domain.model.PdfExportOptions
     ```
   - In `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:25-28`:
     ```kotlin
     val sampleSize = when (options.quality) {
         QualityLevel.COMPRESSED -> 2
         else -> 1
     }
     ```
     Observation: `QualityLevel` is referenced at line 26 but is not imported from `com.docscanner.app.domain.model`.

2. **Incomplete Trash Purging in `TrashViewModel.kt`**:
   - In `app/src/main/java/com/docscanner/app/presentation/trash/TrashViewModel.kt:42-46`:
     ```kotlin
     fun emptyTrash() {
         viewModelScope.launch {
             documentRepository.purgeOldTrash() // Alternatively empty entirely
         }
     }
     ```
   - In `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt:197`:
     ```kotlin
     val cutoff = System.currentTimeMillis() - Constants.TRASH_RETENTION_DAYS * 24L * 60 * 60 * 1000
     ```
     Observation: User-triggered "Empty Trash" only deletes documents trashed >30 days ago, failing to purge recently trashed documents.

3. **Zero Network Exposure & Manifest Hardening**:
   - In `app/src/main/AndroidManifest.xml:5-17`: Only `CAMERA`, `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `USE_BIOMETRIC`, and `POST_NOTIFICATIONS` are requested. Zero internet or network state permissions exist. `android:allowBackup="false"` and `android:usesCleartextTraffic="false"` are enforced.

4. **Sensitive Clipboard Flagging**:
   - In `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt:117-121`:
     ```kotlin
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         description.extras = PersistableBundle().apply {
             putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
         }
     }
     ```
     Observation: OCR clipboard copies on API 33+ are marked sensitive.

5. **Storage Persistence & Shredding**:
   - In `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt`: `persistImageFile` copies streams to `context.filesDir/documents/`. `shredPageFiles` deletes `originalImagePath`, `processedImagePath`, and `thumbnailPath` on disk. Multi-table mutations are wrapped in `appDatabase.withTransaction { ... }` with background execution via `withContext(Dispatchers.IO)`.

---

## 2. Logic Chain

1. **Compilation Blocker**:
   - `options.quality` has enum type `QualityLevel` (defined in `PdfExportOptions.kt`).
   - In Kotlin, referencing `QualityLevel.COMPRESSED` from another package (`com.docscanner.app.service.pdf`) requires importing `com.docscanner.app.domain.model.QualityLevel`.
   - Without this import, `./gradlew assembleDebug` will fail with `Unresolved reference: QualityLevel`.

2. **Trash Retention Discrepancy**:
   - `purgeOldTrash()` is designed for background automated cleanup of records older than 30 days.
   - When a user explicitly taps "Empty Trash", the user expectation is that all trashed documents are wiped immediately.
   - Calling `purgeOldTrash()` leaves all documents trashed in the last 30 days visible in the trash list, causing user confusion.

3. **Integrity and Security Assessment**:
   - No mock facades, hardcoded results, or integrity violations were detected.
   - Core security hardening (zero network, biometric gate, sensitive clipboard, scoped FileProvider paths, physical file shredding, memory management) is implemented with high quality.

---

## 3. Caveats

- Android SDK build execution was evaluated via strict static and semantic analysis.
- The `printPdf` function in `PdfGeneratorService` is currently a stub for future Milestone 3 expansion.

---

## 4. Conclusion

**Verdict**: **REQUEST_CHANGES**

Milestone 1 is nearly complete and demonstrates solid architecture and security hardening. However, the missing import in `PdfGeneratorService.kt` is a critical compilation blocker that must be resolved before proceeding to Milestone 2. Additionally, `TrashViewModel.emptyTrash` should be updated to purge all trashed items.

---

## 5. Verification Method

To verify the findings and fixes:
1. **Compilation Check**: Run `./gradlew assembleDebug` (or `.\gradlew.bat assembleDebug` on Windows) from the root. Verify that `PdfGeneratorService.kt` compiles once `import com.docscanner.app.domain.model.QualityLevel` is added.
2. **Code Inspection**:
   - Inspect `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt` lines 9–28.
   - Inspect `app/src/main/java/com/docscanner/app/presentation/trash/TrashViewModel.kt` lines 42–46.
