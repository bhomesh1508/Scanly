# Scanly Android: Comprehensive Architecture, Performance & Code Quality Survey Report

**Author**: Explorer Subagent (Android Architecture, Performance & Code Quality)  
**Date**: 2026-08-28  
**Target Codebase**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android`  
**Milestone**: Milestone 1 - Architectural & Code Quality Survey

---

## 1. Executive Summary

Scanly is an offline-first, privacy-focused Jetpack Compose document scanning and management Android application built with Kotlin, Room, DataStore, ML Kit Document Scanner & Text Recognition, AndroidX Biometrics, and AndroidX Security Crypto.

A comprehensive survey of the entire codebase revealed solid foundational architecture (Clean Architecture structure, Hilt DI, Material 3 UI), but identified **critical stability, memory, concurrency, and architectural defects** that must be addressed during implementation.

### Key Critical Findings Summary:
1. **App Crashes on File Sharing**: `Extensions.kt:52` uses authority `"$packageName.provider"`, while `AndroidManifest.xml:41` declares `${applicationId}.fileprovider`. This causes immediate `IllegalArgumentException` / `SecurityException` on file sharing.
2. **DataStore Dual Active Instance Conflict**: DataStore is instantiated twice as a delegate on `Context` (`AppModule.kt:17` as `"docscanner_settings"` and `SettingsRepositoryImpl.kt:23` as `"user_settings"`). This causes `IllegalStateException: Multiple DataStores active for the same file` and breaks DI.
3. **Severe Out-Of-Memory (OOM) Vulnerabilities**:
   - `PdfGeneratorService.kt:26`: In multi-page PDF generation, all page bitmaps are decoded into memory simultaneously via `BitmapFactory.decodeFile` without calling `recycle()` or downsampling (`inSampleSize`).
   - `ImageFilterService.kt:77,81`: The `SHARPEN` filter allocates two full-resolution `IntArray` buffers (approx. 96MB on heap for 12MP images) with unoptimized pure Kotlin CPU pixel loops.
   - `ImageFilterService.kt:17,22`: `applyFilter` allocates a new `Bitmap` on every call, immediately discarding it when filter is `ORIGINAL`.
4. **Temporary File Loss on Scanned Documents**: `ScannerViewModel.kt:45-47` stores ML Kit temporary cache URIs as strings without copying image files into the app's internal permanent storage (`context.filesDir/documents`). When Google Play Services purges cache, documents become corrupted/missing.
5. **Database Multi-Table Non-Transactional Operations**: `DocumentRepositoryImpl.kt` performs composite multi-table updates (creating documents, merging, splitting, page insertion) without Room `@Transaction` or `AppDatabase.runInTransaction`, risking corrupted database states on failures.
6. **Hardcoded Stubs and Null Pointer Crashes**:
   - `ViewerViewModel.kt:99` hardcodes `File("dummy.pdf")` in the root working directory.
   - `EditorViewModel.kt:116` force-unwraps `_document.value!!`.
   - `ViewerViewModel.kt:71` parses a local file path into a URI and passes it to `InputImage.fromFilePath(context, uri)`, failing URI resolution.
   - `AppNavigation.kt:55` hardcodes `AppLockGate(isEnabled = false)`.
7. **Absence of Unified UI State and Lifecycle-Aware Collection**: All ViewModels expose 4-7 separate `MutableStateFlow` instances rather than a unified immutable `UiState` data class/sealed interface. Composables use `collectAsState()` instead of lifecycle-aware `collectAsStateWithLifecycle()`.

---

## 2. Build Configuration & Dependencies Survey

### 2.1 File: `build.gradle.kts` (Project Level)
```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```
- **Observation**: Clean, minimal root plugin configuration using Gradle Version Catalog aliases (`libs.plugins.*`).

### 2.2 File: `app/build.gradle.kts`
- **SDK Versions**:
  - `compileSdk = 37` (Line 10)
  - `minSdk = 24` (Line 14)
  - `targetSdk = 34` (Line 15)
- **Toolchain & Java Compatibility**:
  - `JavaVersion.VERSION_21` (Lines 41-42)
  - Kotlin 2.4.10 with Jetpack Compose Compiler Gradle Plugin (`org.jetbrains.kotlin.plugin.compose`)
- **Room Annotation Processor Configuration**:
  ```kotlin
  ksp {
      arg("room.schemaLocation", "$projectDir/schemas")
      arg("room.incremental", "true")
      arg("room.expandProjection", "true")
  }
  ```
  Schema exports are properly enabled, verified at `app/schemas/com.docscanner.app.data.local.db.AppDatabase/1.json`.
- **R8 / Minification**:
  ```kotlin
  buildTypes {
      release {
          isMinifyEnabled = true
          proguardFiles(
              getDefaultProguardFile("proguard-android-optimize.txt"),
              "proguard-rules.pro"
          )
      }
  }
  ```
- **Dependency Issues & Missing Rules**:
  - `app/proguard-rules.pro` contains rules for Room, Hilt, and Kotlinx Serialization, but lacks rules for:
    - ML Kit Document Scanner / Vision models
    - Coil 3 image loader reflection rules
    - Proguard log stripping rules (`-assumenosideeffects class android.util.Log { ... }`)
  - No `lifecycle-runtime-compose` dependency is imported for `collectAsStateWithLifecycle()` (only `lifecycle-runtime-ktx` and `lifecycle-viewmodel-compose` are in `build.gradle.kts`).

---

## 3. Architecture & Data Flow Survey

### 3.1 Clean Architecture Layering
```
com.docscanner.app/
├── data/
│   ├── local/ (db, dao, entity, converter)
│   ├── mapper/ (EntityMappers.kt)
│   └── repository/ (DocumentRepositoryImpl, FolderRepositoryImpl, SettingsRepositoryImpl)
├── domain/
│   ├── model/ (Document, Page, Folder, UserSettings, PdfExportOptions)
│   └── repository/ (DocumentRepository, FolderRepository, SettingsRepository)
├── di/ (AppModule, DatabaseModule, RepositoryModule)
├── presentation/ (home, editor, viewer, scanner, search, folders, settings, trash, common, navigation, theme)
├── service/ (encryption, filter, notification, pdf)
└── util/ (Constants, DateUtils, Extensions)
```

### 3.2 State Modeling Deficiencies
Across all presentation ViewModels, state is fragmented into multiple independent `MutableStateFlow` streams instead of a single, immutable `StateFlow<UiState>` modeled via a data class or sealed interface:

| ViewModel | Current State Modeling | Defect / Impact | Proposed Pattern |
|---|---|---|---|
| `HomeViewModel` | 4 flows: `_documents` (dead), `_searchQuery`, `_sortOrder`, `_viewType` + `filteredDocuments` | Dead state variable `_documents`; multiple state emissions cause recomposition jitter. | `HomeUiState(val documents: List<Document>, val searchQuery: String, val sortOrder: SortOrder, val viewType: ViewType, val isLoading: Boolean)` |
| `EditorViewModel` | 7 flows: `_document`, `_pages`, `_selectedPageIndex`, `_currentFilter`, `_brightness`, `_contrast`, `_previewBitmap` | Multiple `viewModelScope.launch` in `init`; state updates are uncoordinated. `_document.value!!` crashes if called before load. | `EditorUiState(val document: Document?, val pages: List<Page>, val selectedPageIndex: Int, val filter: FilterType, val brightness: Float, val contrast: Float, val isSaving: Boolean, val error: String?)` |
| `ViewerViewModel` | 5 flows: `_document`, `_pages`, `_currentPageIndex`, `_ocrText`, `_ocrLoading` | Mixed async operations; OCR text extraction triggered directly with `Context` parameter. | `ViewerUiState(val document: Document?, val pages: List<Page>, val currentPage: Int, val ocrState: OcrState, val isExporting: Boolean)` |
| `ScannerViewModel` | 3 flows: `_scanState`, `_scannedPages`, `_pdfUri` | Uses enum `ScanState` alongside loose URI lists; no sealed error modeling. | `ScannerUiState(val state: ScannerStatus, val pages: List<Uri>, val error: String?)` |
| `FoldersViewModel` | 1 flow: `_folders` | Simple collect in `init` rather than declarative `stateIn()`. | `FoldersUiState(val folders: List<Folder>, val isLoading: Boolean)` |

### 3.3 Dependency Injection & Scoping Defects
1. **DataStore Duplicate Instantiation**:
   - `AppModule.kt:17`: `private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)` (where `Constants.DATASTORE_NAME = "docscanner_settings"`)
   - `SettingsRepositoryImpl.kt:23`: `private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")`
   - `AppModule.kt:37` provides `@Singleton fun provideDataStore(app: Application): DataStore<Preferences>`, but `SettingsRepositoryImpl` injects `Application` directly and accesses its private top-level delegate on `Context`.
   - **Risk**: Violates Singleton pattern, causes DataStore file lock exceptions (`IllegalStateException`), and reads/writes to different files!
2. **Missing Repository Injection for Services**:
   - `PdfGeneratorService` and `ImageFilterService` are `@Singleton` without repository or dispatcher abstractions, which is acceptable, but methods in `ViewerViewModel` call heavy blocking PDF generation directly on ViewModel caller threads.

### 3.4 UI & Navigation Architecture Issues
1. **`AppLockGate` Bypassed**:
   - `AppNavigation.kt:55`: `AppLockGate(isEnabled = false)` hardcodes `isEnabled = false`, completely disabling the biometric lock feature regardless of user preference in DataStore.
2. **Double Scaffold / Overlapping FABs**:
   - `AppNavigation.kt:56-80` defines an outer `Scaffold` with a bottom bar and a `FloatingActionButton`.
   - `HomeScreen.kt:39-98` defines an inner `Scaffold` with its own `ExtendedFloatingActionButton` and top bar.
   - Result: Two Scaffolds are rendered simultaneously, stacking two FABs on top of each other.
3. **Broken Extension Function in `FolderDetailScreen.kt`**:
   - `FolderDetailScreen.kt:65-67`:
     ```kotlin
     private fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(
         Modifier.clickable { onClick() }
     )
     ```
     This shadows Compose's standard `Modifier.clickable` and can cause infinite recursion or runtime stack overflow!
4. **Duplicate UI Components**:
   - `EmptyState`: Defined in `HomeScreen.kt:131` and also in `presentation/common/EmptyState.kt:22`.
   - `DocumentCard`: Defined in `HomeScreen.kt:160` and was stubbed out in `presentation/common/DocumentCard.kt:1`.

---

## 4. Memory & Resource Management Survey

### 4.1 Bitmap Lifecycle & Leaks in Image Processing
1. **Bitmap Leak in `ImageFilterService.kt`**:
   - Lines 17 & 22:
     ```kotlin
     fun applyFilter(bitmap: Bitmap, filterType: FilterType): Bitmap {
         val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
         // ...
         when (filterType) {
             FilterType.ORIGINAL -> return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
             // ...
         }
     }
     ```
     When `filterType == ORIGINAL`, `result` is allocated and immediately orphaned without being recycled or garbage collected.
2. **Heap Memory Explosion in `SHARPEN` Filter (`ImageFilterService.kt:76-109`)**:
   - Lines 77, 81:
     ```kotlin
     val pixels = IntArray(bitmap.width * bitmap.height)
     bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
     val newPixels = IntArray(width * height)
     ```
     For a standard 12MP document photo ($4000 \times 3000$ pixels):
     - `pixels` = $12,000,000 \times 4\text{ bytes} = 48\text{ MB}$
     - `newPixels` = $12,000,000 \times 4\text{ bytes} = 48\text{ MB}$
     - Total allocation = **96 MB of contiguous heap memory** in a single function call!
   - Nested pixel loop running pure Kotlin interpreted loops on $12\text{M}$ iterations will block the CPU for multiple seconds, causing massive jank and frame drops.
3. **No Intermediate Bitmap Recycling**:
   - When users adjust filters and brightness/contrast in `EditorScreen`, multiple bitmaps are generated sequentially (`applyFilter` -> `applyAdjustments`). Previous bitmaps are never recycled (`bitmap.recycle()`), causing heap fragmentation and GC pressure.

### 4.2 PDF Generation Memory Hazard (`PdfGeneratorService.kt:21-62`)
- Lines 25-51:
  ```kotlin
  pages.forEachIndexed { index, page ->
      val bitmap = BitmapFactory.decodeFile(page.processedImagePath) ?: return@forEachIndexed
      // ...
      canvas.drawBitmap(bitmap, null, android.graphics.RectF(...), null)
      document.finishPage(pdfPage)
  }
  ```
  - `bitmap` is never recycled after drawing to canvas!
  - If a user exports a 20-page document where each page is a 5MB JPEG ($3000 \times 4000 \times 4\text{ bytes} = 48\text{MB}$ uncompressed), keeping multiple un-recycled decoded bitmaps on the heap will immediately trigger `java.lang.OutOfMemoryError: Failed to allocate a ... byte allocation with ... free bytes`.
  - Fix: Add `bitmap.recycle()` immediately after `document.finishPage(pdfPage)` and use `BitmapFactory.Options.inSampleSize` when page dimensions exceed standard PDF DPI targets.
- **Resource Leak on Failure**:
  - `document.close()` (line 56) is located after `FileOutputStream(outputFile).use { ... }`. If an I/O exception occurs during file writing, `document.close()` is never called, leaking the underlying native Skia PDF document handle.
  - Fix: Wrap `PdfDocument` in a `try-finally` block.

### 4.3 ML Kit Resource Leaks (`ViewerViewModel.kt:73`)
- Line 73: `val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)` creates a new C++ ML Kit TextRecognizer native instance on every click of "Extract Text" and never calls `recognizer.close()`.

### 4.4 Temporary Cache URI Data Loss (`ScannerViewModel.kt:45-47`)
- ML Kit Document Scanner returns temporary file URIs stored in Google Play Services cache.
- In `ScannerViewModel.kt`:
  ```kotlin
  val pagePaths = _scannedPages.value.map { it.toString() }
  ```
  The app stores `content://...` or temporary `/data/user/0/.../cache/...` strings into Room DB directly without copying the actual image bytes to `context.filesDir/documents/${docId}_page_${index}.jpg`.
- When the system cleans cache or the app restarts, the image files are deleted by the OS, leaving permanent blank/broken pages in the user's documents.

---

## 5. Error Handling & Robustness Survey

### 5.1 Critical Crash Hazards & Bugs

| Hazard # | File & Location | Code Snippet | Root Cause & Impact | Fix |
|---|---|---|---|---|
| **C1** | `util/Extensions.kt:52` | `FileProvider.getUriForFile(this, "$packageName.provider", file)` | Authority mismatch with `AndroidManifest.xml:41` (`${applicationId}.fileprovider`). Causes `IllegalArgumentException` / crash when sharing files. | Change authority to `"${packageName}.fileprovider"`. |
| **C2** | `presentation/viewer/ViewerViewModel.kt:99` | `val file = File("dummy.pdf")` | Hardcodes relative path `dummy.pdf` in process root instead of app cache dir. Fails with `IOException` on export. | Pass target file in `context.cacheDir` or `context.filesDir`. |
| **C3** | `presentation/viewer/ViewerViewModel.kt:71` | `val uri = Uri.parse(page.processedImagePath); InputImage.fromFilePath(context, uri)` | `page.processedImagePath` is an absolute file path (`/data/...`), not a content URI. `InputImage.fromFilePath` crashes on invalid scheme. | Use `InputImage.fromFilePath(context, Uri.fromFile(File(path)))` or `InputImage.fromBitmap`. |
| **C4** | `presentation/editor/EditorViewModel.kt:116` | `documentRepository.updateDocument(_document.value!!)` | Force unwrap `!!` on `_document.value` crashes with `NullPointerException` if user clicks save while document is loading. | Safe unwrap: `_document.value?.let { documentRepository.updateDocument(it) }`. |
| **C5** | `data/repository/SettingsRepositoryImpl.kt:43-50` | `ThemeMode.valueOf(...)`, `FilterType.valueOf(...)` | Unsafe `Enum.valueOf` without try-catch causes `IllegalArgumentException` and crashes app on startup if corrupt/unknown key exists. | Add safe parsing fallback (e.g. `try { ThemeMode.valueOf(...) } catch (e: Exception) { ThemeMode.SYSTEM }`). |
| **C6** | `data/repository/DocumentRepositoryImpl.kt:180` | `Pair(documentDao.getDocumentByIdSync(doc1.id)!!.toDomain(), ...)` | Double force unwrap `!!` on DB fetch during split document operation. | Return non-null created models or use safe navigation. |
| **C7** | `data/repository/DocumentRepositoryImpl.kt:196-203` | `duplicatePage` | Inserts duplicate page with `pageNumber = original.pageNumber + 1` without incrementing subsequent pages, corrupting page ordering. | Re-index all pages with `pageNumber >= newPageNumber`. |
| **C8** | `data/repository/DocumentRepositoryImpl.kt:173-174` | `splitDocument` | Passes only `map { it.originalImagePath }` to `createDocument`, discarding rotation, filters, brightness, contrast, and OCR text from split pages. | Preserve all `Page` attributes across split documents. |

### 5.2 Room Database Transaction Safety
In `DocumentRepositoryImpl.kt`, multiple operations perform composite database queries that MUST be atomic:
1. `createDocument` (Lines 49-91): Inserts `DocumentEntity` then loops inserting `PageEntity`s. If an error occurs midway, an orphan Document exists without pages.
2. `mergeDocuments` (Lines 123-163): Creates new document, reads all source pages, inserts new pages, and updates doc count.
3. `splitDocument` (Lines 165-181): Creates two new documents, reassigns folder, deletes old document.
4. `addPages` (Lines 211-239): Inserts pages and updates document page count.

**Defect**: None of these operations use `@Transaction` in DAOs or `appDatabase.withTransaction { ... }` in Repository.
**Recommendation**: Wrap all composite operations inside `appDatabase.withTransaction { ... }` or declare DAO helper methods with `@Transaction`.

### 5.3 Storage Leak on Permanent Deletion (`DocumentRepositoryImpl.kt:114-116`)
```kotlin
override suspend fun permanentlyDelete(docId: String) = withContext(Dispatchers.IO) {
    documentDao.delete(docId)
    pageDao.deleteByDocument(docId)
}
```
**Defect**: Deleting rows from Room database leaves the physical files on disk (`originalImagePath`, `processedImagePath`, `thumbnailPath`, exported PDFs). Over time, deleted scans accumulate in internal storage and cannot be reclaimed by the user except via "Clear App Data".
**Fix**: Query page image paths before deleting entities, and call `File(path).delete()` for all associated image files and thumbnails.

---

## 6. Logging & Observability Architecture Survey

### 6.1 Current Logging Status
- Currently, the codebase has **zero structured logging**:
  - No `Timber` or centralized logging facade.
  - Exceptions across repositories, services, and viewmodels are caught with empty blocks (`catch (e: Exception) {}`) or simple `Result.failure(e)`.
  - No diagnostic logging or breadcrumbs for scan failures, OCR failures, or database migration errors.
- Examples of swallowed errors:
  - `EncryptionService.kt:34, 54, 67`
  - `PdfGeneratorService.kt:59`
  - `Extensions.kt:24`
  - `ViewerViewModel.kt:83`
  - `ScannerViewModel.kt:37, 49`

### 6.2 Proguard / Release Build Log Stripping
`app/proguard-rules.pro` does not contain R8 rules to strip debug logs in release builds:
```proguard
# Recommended Log Stripping Rule for Release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}
```

---

## 7. Concrete Enhancement Proposals & Blueprint

### 7.1 Architecture & State Flow Refactoring Blueprint

#### Proposed Unified UI State Pattern (Example: `EditorUiState`)
```kotlin
sealed interface EditorUiState {
    data object Loading : EditorUiState
    data class Success(
        val document: Document,
        val pages: List<Page>,
        val selectedPageIndex: Int = 0,
        val activeFilter: FilterType = FilterType.ORIGINAL,
        val brightness: Float = 0f,
        val contrast: Float = 0f,
        val previewBitmap: Bitmap? = null,
        val isSaving: Boolean = false
    ) : EditorUiState
    data class Error(val message: String) : EditorUiState
}
```

#### Proposed `EditorViewModel` using Declarative State Flow
```kotlin
@HiltViewModel
class EditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentRepository: DocumentRepository,
    private val imageFilterService: ImageFilterService
) : ViewModel() {
    val documentId: String = checkNotNull(savedStateHandle["documentId"])

    val uiState: StateFlow<EditorUiState> = combine(
        documentRepository.getDocumentById(documentId),
        documentRepository.getPages(documentId)
    ) { doc, pages ->
        if (doc != null && pages.isNotEmpty()) {
            EditorUiState.Success(document = doc, pages = pages)
        } else {
            EditorUiState.Error("Document not found")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EditorUiState.Loading
    )
}
```

### 7.2 Memory-Safe Image Processing Blueprint

#### Optimized Sharpen & Filter Pipeline (`ImageFilterService.kt`)
1. **Downsample for UI Previews**: Generate preview bitmaps at viewport resolution ($800 \times 600$ max) instead of full $12\text{MP}$.
2. **Eliminate 96MB Heap Allocation**: Use ColorMatrix-based sharpen convolutions where possible or native/RenderScript-replacement buffers.
3. **Bitmap Recycling**:
```kotlin
fun generatePdfSafely(pages: List<Page>, options: PdfExportOptions, outputFile: File): Result<File> {
    val document = PdfDocument()
    try {
        pages.forEachIndexed { index, page ->
            val decodeOptions = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.RGB_565 // 50% memory reduction for document photos
            }
            val bitmap = BitmapFactory.decodeFile(page.processedImagePath, decodeOptions) ?: return@forEachIndexed
            try {
                val (docWidth, docHeight) = getDimensions(options.pageSize, bitmap.width, bitmap.height)
                val pageInfo = PdfDocument.PageInfo.Builder(docWidth, docHeight, index + 1).create()
                val pdfPage = document.startPage(pageInfo)
                // Draw bitmap to canvas...
                document.finishPage(pdfPage)
            } finally {
                bitmap.recycle() // Immediate reclamation of native memory
            }
        }
        FileOutputStream(outputFile).use { fos ->
            document.writeTo(fos)
        }
        return Result.success(outputFile)
    } catch (e: Exception) {
        return Result.failure(e)
    } finally {
        document.close() // Guaranteed native handle cleanup
    }
}
```

### 7.3 DataStore & Dependency Injection Normalization Blueprint
1. In `AppModule.kt`:
   ```kotlin
   @Provides
   @Singleton
   fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
       return PreferenceDataStoreFactory.create(
           corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
           produceFile = { context.preferencesDataStoreFile(Constants.DATASTORE_NAME) }
       )
   }
   ```
2. In `SettingsRepositoryImpl.kt`:
   ```kotlin
   @Singleton
   class SettingsRepositoryImpl @Inject constructor(
       private val dataStore: DataStore<Preferences>
   ) : SettingsRepository { ... }
   ```
   Inject the singleton `DataStore<Preferences>` directly into `SettingsRepositoryImpl` and remove the top-level property delegate.

### 7.4 Permanent File Storage Pipeline for ML Kit Document Scanner
In `ScannerViewModel.kt` or `DocumentRepositoryImpl.kt`:
```kotlin
suspend fun copyScanUrisToStorage(context: Context, uris: List<Uri>, docId: String): List<String> = withContext(Dispatchers.IO) {
    val documentsDir = File(context.filesDir, Constants.DOCUMENTS_DIR).apply { mkdirs() }
    uris.mapIndexed { index, uri ->
        val targetFile = File(documentsDir, "${docId}_page_${index + 1}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    }
}
```

---

## 8. Prioritized Implementation Action Items

| Priority | Area | Action Item | Target Files |
|---|---|---|---|
| **P0 (Blocker)** | Bug Fix / Security | Fix FileProvider authority string to match Manifest (`${packageName}.fileprovider`). | `util/Extensions.kt:52` |
| **P0 (Blocker)** | Data Safety | Fix ML Kit temp cache URI loss: copy images to internal storage. | `presentation/scanner/ScannerViewModel.kt`, `data/repository/DocumentRepositoryImpl.kt` |
| **P0 (Blocker)** | DI / Concurrency | Remove duplicate DataStore declaration; inject singleton `DataStore<Preferences>`. | `di/AppModule.kt`, `data/repository/SettingsRepositoryImpl.kt` |
| **P0 (Blocker)** | Memory / OOM | Fix bitmap recycling in `PdfGeneratorService` and `ImageFilterService`. | `service/pdf/PdfGeneratorService.kt`, `service/filter/ImageFilterService.kt` |
| **P1 (High)** | Robustness | Wrap multi-table DB ops in `appDatabase.withTransaction`. | `data/repository/DocumentRepositoryImpl.kt` |
| **P1 (High)** | Robustness | Fix file deletion on permanent delete (remove image files on disk). | `data/repository/DocumentRepositoryImpl.kt:114` |
| **P1 (High)** | Bug Fix | Fix `ViewerViewModel.kt` hardcoded `File("dummy.pdf")` and URI parsing. | `presentation/viewer/ViewerViewModel.kt:71,99` |
| **P1 (High)** | Bug Fix | Fix `AppNavigation.kt` hardcoded `AppLockGate(isEnabled = false)`. | `presentation/navigation/AppNavigation.kt:55` |
| **P1 (High)** | UI / Polish | Fix nested Scaffold / duplicate FAB in `AppNavigation` + `HomeScreen`. | `presentation/navigation/AppNavigation.kt`, `presentation/home/HomeScreen.kt` |
| **P2 (Medium)** | Architecture | Refactor ViewModels to unified `UiState` sealed interfaces and UDF. | `presentation/*/*ViewModel.kt` |
| **P2 (Medium)** | Logging | Add centralized `AppLogger` utility and Proguard log stripping rules. | `util/AppLogger.kt`, `app/proguard-rules.pro` |
| **P2 (Medium)** | Performance | Replace `collectAsState()` with `collectAsStateWithLifecycle()`. | `presentation/*/*Screen.kt` |
