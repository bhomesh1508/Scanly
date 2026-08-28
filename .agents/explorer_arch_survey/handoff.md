# Handoff Report: Android Architecture, Performance & Code Quality Survey

**Subagent**: Explorer Arch Survey  
**Date**: 2026-08-28  
**Parent Orchestrator**: `e3b71026-e293-4baa-b88d-8f1a46310d8b`  
**Report File**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_arch_survey\survey_arch_report.md`

---

## 1. Observation

Direct code observations from inspecting `app/src/main/java/com/docscanner/app` and build configuration:

1. **FileProvider Authority Bug**:
   - `app/src/main/java/com/docscanner/app/util/Extensions.kt:52`:
     ```kotlin
     val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
     ```
   - `app/src/main/AndroidManifest.xml:41`:
     ```xml
     android:authorities="${applicationId}.fileprovider"
     ```
   - `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:65`:
     ```kotlin
     val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
     ```
   - *Result*: Calling `Context.shareFile(...)` crashes with `IllegalArgumentException: Failed to find configured root that contains ...` or security mismatch.

2. **DataStore Duplicate Instantiation Conflict**:
   - `app/src/main/java/com/docscanner/app/di/AppModule.kt:17`:
     ```kotlin
     private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)
     ```
   - `app/src/main/java/com/docscanner/app/data/repository/SettingsRepositoryImpl.kt:23`:
     ```kotlin
     private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")
     ```
   - *Result*: Two distinct DataStore instances on `Context` accessing different files (`docscanner_settings` vs `user_settings`), violating DI and risking `IllegalStateException`.

3. **Memory & Bitmap Leakage in PDF Generation**:
   - `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:26, 49`:
     ```kotlin
     val bitmap = BitmapFactory.decodeFile(page.processedImagePath) ?: return@forEachIndexed
     // ...
     canvas.drawBitmap(bitmap, null, android.graphics.RectF(left, top, left + scaledWidth, top + scaledHeight), null)
     document.finishPage(pdfPage)
     ```
   - Bitmaps are never recycled in loop; native Skia handles accumulate.
   - `document.close()` (line 56) is outside a `finally` block.

4. **Heap Allocation Bloat in Image Filtering**:
   - `app/src/main/java/com/docscanner/app/service/filter/ImageFilterService.kt:77, 81`:
     ```kotlin
     val pixels = IntArray(bitmap.width * bitmap.height)
     // ...
     val newPixels = IntArray(width * height)
     ```
   - For a 12MP image ($4000 \times 3000$), allocates $96\text{MB}$ heap memory with pure Kotlin CPU loops.
   - `applyFilter` (lines 17, 22) allocates an unneeded `Bitmap` when `filterType == ORIGINAL` and immediately discards it.

5. **Temporary File Cache Loss from Scanner**:
   - `app/src/main/java/com/docscanner/app/presentation/scanner/ScannerViewModel.kt:45-47`:
     ```kotlin
     val pagePaths = _scannedPages.value.map { it.toString() }
     val document = documentRepository.createDocument(title, pagePaths, pdfPath)
     ```
   - Stores ML Kit temporary cache URIs directly into Room DB without copying bytes to `context.filesDir/documents/`.

6. **Missing Room Transactions & Broken Page Duplicate/Split**:
   - `app/src/main/java/com/docscanner/app/data/repository/DocumentRepositoryImpl.kt:49, 123, 165, 196, 211`:
     - Composite multi-table modifications (`upsert` + multiple `insert`/`delete`) lack `@Transaction` or `appDatabase.withTransaction`.
     - `duplicatePage` (line 196) inserts `pageNumber + 1` without renumbering subsequent pages.
     - `splitDocument` (line 173) drops page rotation, filters, brightness, contrast, and OCR text.
     - `permanentlyDelete` (line 114) deletes database rows without deleting image files from internal storage.

7. **Hardcoded Stubs and Null Pointer Hazards**:
   - `app/src/main/java/com/docscanner/app/presentation/viewer/ViewerViewModel.kt:99`: `val file = File("dummy.pdf")`
   - `app/src/main/java/com/docscanner/app/presentation/editor/EditorViewModel.kt:116`: `_document.value!!`
   - `app/src/main/java/com/docscanner/app/presentation/navigation/AppNavigation.kt:55`: `AppLockGate(isEnabled = false)`
   - `app/src/main/java/com/docscanner/app/presentation/folders/FolderDetailScreen.kt:65-67`: Shadowed recursive `Modifier.clickable` extension.

---

## 2. Logic Chain

1. **Premise**: An offline document scanning application operates under memory-constrained conditions (large image decodes, PDF generation) and must guarantee data persistence and UI stability.
2. **Analysis of File Operations**:
   - `Extensions.kt` uses authority `"$packageName.provider"`, but `AndroidManifest.xml` configures `${applicationId}.fileprovider`. Any invocation of `shareFile` throws `IllegalArgumentException: Failed to find configured root`.
   - `ScannerViewModel` takes temporary cache URIs from ML Kit and stores them as strings in `PageEntity`. When Android or Google Play Services cleans the cache directory, files at those URIs are deleted.
3. **Analysis of Memory / Concurrency**:
   - Decoding 10-30 high-resolution bitmaps in a single loop in `PdfGeneratorService` without calling `recycle()` holds uncompressed pixel buffers in native/heap memory, causing `OutOfMemoryError`.
   - Creating multiple DataStore instances for different filenames breaks the single source of truth for preferences and risks file locking.
   - Performing multi-step database mutations without SQLite transactions risks orphaned document headers and mismatched page counts upon unexpected process death.

---

## 3. Caveats

- **Network Mode**: Investigation was performed read-only in the local repository workspace without modifying source files.
- **Build / Test Run**: Gradle build execution via `run_command` was not executed in this survey turn to avoid interactive prompt timeouts; code was verified through direct static code analysis.

---

## 4. Conclusion

The application is well-structured under Clean Architecture principles, but contains **7 critical bugs and performance bottlenecks** (authority mismatch, DataStore duplicate instantiation, bitmap recycling leaks, temporary cache URI loss, missing Room transactions, storage leak on delete, and fragmented UI states).

All issues have been cataloged with exact file locations, root causes, and refactoring blueprints in `survey_arch_report.md`.

---

## 5. Verification Method

To independently verify these findings:
1. **FileProvider Authority**: Inspect `app/src/main/java/com/docscanner/app/util/Extensions.kt:52` vs `app/src/main/AndroidManifest.xml:41`.
2. **DataStore Conflict**: Inspect `app/src/main/java/com/docscanner/app/di/AppModule.kt:17` vs `app/src/main/java/com/docscanner/app/data/repository/SettingsRepositoryImpl.kt:23`.
3. **Bitmap Recycling**: Inspect `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:26, 49` and `app/src/main/java/com/docscanner/app/service/filter/ImageFilterService.kt:77, 81`.
4. **Temporary File Retention**: Inspect `app/src/main/java/com/docscanner/app/presentation/scanner/ScannerViewModel.kt:45-47`.
5. **Build Verification**: Run `./gradlew assembleDebug` to verify compilation.
