# Milestone 2: Manifest & Proguard Audit Handoff Report

**Agent**: `explorer_m2_2` (Explorer 2)  
**Parent Agent**: `orchestrator_2` / `parent` (`ac023869-00d9-405d-96f1-afaf79b9e8c3`)  
**Working Directory**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_m2_2`  
**Target Milestone**: Milestone 2 — Dependency & Build Configuration Cleanup  
**Date**: 2026-08-28  

---

## 1. Observation

Direct inspections of the source code, build configuration, and resource files yielded the following verified facts:

### A. AndroidManifest Configuration (`app/src/main/AndroidManifest.xml`)
- **File Path**: `file:///C:/Users/DELL/.gemini/antigravity/scratch/docscanner_android/app/src/main/AndroidManifest.xml` (52 lines total).
- **Declared Permissions (Lines 5–12)**:
  ```xml
  <uses-permission android:name="android.permission.CAMERA" />
  
  <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
  
  <uses-permission android:name="android.permission.USE_BIOMETRIC" />
  <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
  ```
- **Excluded Permissions**:
  - `android.permission.INTERNET`: Absent from source manifest.
  - `android.permission.ACCESS_NETWORK_STATE`: Absent from source manifest.
  - `android.permission.RECEIVE_BOOT_COMPLETED`: Absent from source manifest.
  - `android.permission.FOREGROUND_SERVICE`: Absent from source manifest.
- **Provider Blocks (Lines 39–47)**:
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
  - `FirebaseInitProvider`: Absent from source manifest.
  - WorkManager `InitializationProvider`: Absent from source manifest.
- **Application & Activity (Lines 14–33)**:
  - `android:name=".DocScannerApp"`
  - `MainActivity` exported as `singleTop` with `MAIN`/`LAUNCHER` intent filter.
  - `meta-data`: `com.google.mlkit.vision.DEPENDENCIES` = `"ocr"`.

### B. Proguard Configuration (`app/proguard-rules.pro`)
- **File Path**: `file:///C:/Users/DELL/.gemini/antigravity/scratch/docscanner_android/app/proguard-rules.pro` (15 lines total).
- **Verbatim Content**:
  ```proguard
  # Room
  -keep class * extends androidx.room.RoomDatabase
  -dontwarn androidx.room.paging.**

  # Hilt
  -keep,allowobfuscation,allowshrinking @dagger.hilt.android.AndroidEntryPoint class *
  -keep,allowobfuscation,allowshrinking @dagger.hilt.android.HiltAndroidApp class *

  # Kotlinx Serialization
  -keepattributes *Annotation*, InnerClasses
  -dontnote kotlinx.serialization.AnnotationsKt
  -keep,allowcreations,allowoptimization class * {
      @kotlinx.serialization.Serializable *;
  }
  ```
- **Firebase Rules**: Zero Firebase rules (`-keepattributes Signature`, `-keepclassmembers class * { @com.google.firebase.database.IgnoreExtraProperties *; }`) exist in the file.

### C. FileProvider Cross-Reference Findings
- `app/src/main/res/xml/file_paths.xml`: Defines `<files-path name="documents" path="documents/" />`, `<files-path name="thumbnails" path="thumbnails/" />`, `<cache-path name="cache" path="/" />`, `<cache-path name="temp" path="temp/" />`.
- `app/src/main/java/com/docscanner/app/service/pdf/PdfGeneratorService.kt:65`: `FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)` matches the manifest authority `${applicationId}.fileprovider`.
- `app/src/main/java/com/docscanner/app/util/Extensions.kt:52`: `FileProvider.getUriForFile(this, "$packageName.provider", file)` uses `"$packageName.provider"` instead of `"$packageName.fileprovider"`.

---

## 2. Logic Chain

1. **Premise**: Scanly must function as a 100% offline, privacy-first document scanner without network access, cloud sync, or telemetry.
2. **Inference on Permissions**:
   - Camera access (`CAMERA`) and gallery access (`READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`) are mandatory for capturing documents via ML Kit and exporting files locally.
   - App lock (`USE_BIOMETRIC`) is mandatory for local BiometricPrompt authentication.
   - Scan completion notifications (`POST_NOTIFICATIONS`) are mandatory on API 33+ for local system alerts.
   - Network permissions (`INTERNET`, `ACCESS_NETWORK_STATE`) and background sync permissions (`RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`) are non-essential and correctly omitted.
3. **Inference on Manifest Providers**:
   - Because all Firebase libraries (`firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`) and WorkManager libraries (`work-runtime-ktx`, `hilt-work`) are deleted from the Gradle build configuration, `FirebaseInitProvider` and WorkManager initializers will not be merged into the build artifact.
   - `FileProvider` is required for sharing PDFs and exported images with external viewer applications and is properly configured with secure app-internal directories in `res/xml/file_paths.xml`.
4. **Inference on Proguard Rules**:
   - Retaining Room (`RoomDatabase`), Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`), and Kotlinx Serialization rules prevents runtime reflection failures and class-not-found exceptions in minified release builds.
   - Eliminating Firebase rules avoids R8 warnings regarding missing `@com.google.firebase.database.IgnoreExtraProperties` annotation classes.
   - AndroidX Security Crypto, Biometric, and ML Kit AARs contain embedded consumer rules, which R8 processes automatically.

---

## 3. Caveats

1. **Extensions.kt Authority Inconsistency**:
   - `Extensions.kt` line 52 contains `"$packageName.provider"`, which differs from `"${applicationId}.fileprovider"` in `AndroidManifest.xml` and `PdfGeneratorService.kt`. This must be updated during Milestone 3 source code refinements.
2. **Build Execution in Headless Environment**:
   - Terminal commands requiring interactive elevation should be run directly by the user or orchestrator (`gradlew assembleDebug`).

---

## 4. Conclusion

1. **`app/src/main/AndroidManifest.xml`**:
   - Fully compliant with the offline-first architecture.
   - Zero Firebase/WorkManager provider blocks.
   - Zero unused network/sync permissions (`INTERNET`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`).
   - Essential permissions (`CAMERA`, `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `USE_BIOMETRIC`, `POST_NOTIFICATIONS`), `FileProvider`, `MainActivity`, and OCR meta-data are perfectly configured.
2. **`app/proguard-rules.pro`**:
   - Fully compliant with release optimization and shrinking requirements.
   - Zero Firebase/Cloud rules.
   - Room, Hilt, and Kotlinx Serialization rules are intact and verified.

---

## 5. Verification Method

To independently verify this assessment:
1. **Inspect AndroidManifest**:
   - Open `app/src/main/AndroidManifest.xml`.
   - Verify permissions on lines 5–12 match the 6 required local permissions.
   - Verify `FileProvider` is declared on lines 39–47 with `${applicationId}.fileprovider`.
   - Verify absence of `INTERNET`, `ACCESS_NETWORK_STATE`, `FirebaseInitProvider`, and WorkManager providers.
2. **Inspect ProGuard Rules**:
   - Open `app/proguard-rules.pro`.
   - Verify lines 1–15 contain rules for Room, Hilt, and Kotlinx Serialization only.
3. **Build & Manifest Merge Verification**:
   - Run `.\gradlew.bat processDebugMainManifest` or `.\gradlew.bat assembleDebug`.
   - Inspect generated manifest at `app/build/intermediates/merged_manifests/debug/processDebugManifest/AndroidManifest.xml`.
   - Invalidation condition: If `FirebaseInitProvider` or `INTERNET` permission appears in the merged manifest, a transitive cloud dependency remains in `build.gradle.kts`.
