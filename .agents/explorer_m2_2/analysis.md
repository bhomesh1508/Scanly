# Milestone 2: Manifest & Proguard Configuration Analysis

**Document**: Milestone 2 AndroidManifest & Proguard Deep-Dive Analysis  
**Project**: Scanly Android Offline Refactoring  
**Author**: Explorer 2 (`explorer_m2_2`)  
**Date**: 2026-08-28  
**Working Directory**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android\.agents\explorer_m2_2`  
**Project Root**: `C:\Users\DELL\.gemini\antigravity\scratch\docscanner_android`  

---

## 1. Executive Summary

This report delivers an exhaustive audit and line-by-line plan for `app/src/main/AndroidManifest.xml` and `app/proguard-rules.pro` to support Milestone 2 (Dependency & Build Configuration Cleanup) of the Scanly Android Refactoring project.

The ultimate objective of Scanly is a **100% self-contained, privacy-first, offline-only Android application**. All legacy cloud synchronization, Firebase backends, Google Identity authenticators, and background sync workers have been eliminated from the architecture. This report verifies that the Android Manifest and Proguard configurations reflect this offline-first architecture with zero residual cloud components, zero unneeded network permissions, and robust local execution definitions.

### Key Audit Findings:
1. **Manifest Permissions**: All 6 declared permissions (`CAMERA`, `READ_MEDIA_IMAGES`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `USE_BIOMETRIC`, `POST_NOTIFICATIONS`) directly serve essential local features. Zero network permissions (`INTERNET`, `ACCESS_NETWORK_STATE`) or background service permissions (`RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`) are declared in `app/src/main/AndroidManifest.xml`.
2. **Provider Blocks**:
   - `FirebaseInitProvider` and `WorkManager` `InitializationProvider` are completely eliminated.
   - `androidx.core.content.FileProvider` is properly declared with `${applicationId}.fileprovider` mapped to `@xml/file_paths`.
3. **Application & Activities**:
   - `DocScannerApp` (`@HiltAndroidApp`) initializes local notification channels.
   - `MainActivity` (`@AndroidEntryPoint`, `singleTop`) extends `FragmentActivity`, satisfying AndroidX Biometric requirements.
   - `com.google.mlkit.vision.DEPENDENCIES` is set to `ocr` to ensure local ML Kit model availability.
4. **Proguard Rules**:
   - Zero Firebase rules (`-keepattributes Signature`, `-keepclassmembers class * { @com.google.firebase.database.IgnoreExtraProperties *; }`) remain in `app/proguard-rules.pro`.
   - Explicit rules for Room Database (`RoomDatabase`, `-dontwarn androidx.room.paging.**`), Dagger Hilt entry points, and Kotlinx Serialization are intact.
   - AndroidX Security Crypto, Biometric, ML Kit, and Coroutines consumer rules are embedded directly in their respective AAR packages.

---

## 2. Line-by-Line Audit of `app/src/main/AndroidManifest.xml`

### 2.1 Full Source Manifest Content
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.CAMERA" />
    
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
    
    <uses-permission android:name="android.permission.USE_BIOMETRIC" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:name=".DocScannerApp"
        android:allowBackup="true"
        android:icon="@android:drawable/sym_def_app_icon"
        android:label="@string/app_name"
        android:roundIcon="@android:drawable/sym_def_app_icon"
        android:supportsRtl="true"
        android:theme="@style/Theme.DocScanner"
        tools:targetApi="31">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:theme="@style/Theme.DocScanner">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <meta-data
            android:name="com.google.mlkit.vision.DEPENDENCIES"
            android:value="ocr" />

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>

</manifest>
```

---

### 2.2 Permissions Audit & Rationale

| Permission | Scope / Level | Purpose in Offline App | Audit Assessment |
|---|---|---|---|
| `android.permission.CAMERA` | Normal / Runtime | Document scanning via Google Play Services ML Kit Document Scanner API | **Retain (Mandatory)** |
| `android.permission.READ_MEDIA_IMAGES` | Runtime (API 33+) | Importing photos/documents from device photo gallery on Android 13+ | **Retain (Mandatory)** |
| `android.permission.READ_EXTERNAL_STORAGE` | Runtime (`maxSdkVersion="32"`) | Reading legacy external storage files on Android 12 and below | **Retain (Mandatory)** |
| `android.permission.WRITE_EXTERNAL_STORAGE` | Runtime (`maxSdkVersion="28"`) | Writing exported PDFs/images to external storage on Android 9 and below | **Retain (Mandatory)** |
| `android.permission.USE_BIOMETRIC` | Normal | BiometricPrompt authentication in `AppLockGate.kt` for app privacy lock | **Retain (Mandatory)** |
| `android.permission.POST_NOTIFICATIONS` | Runtime (API 33+) | Local notifications via `NotificationService.kt` on scan completion | **Retain (Mandatory)** |

#### Unused / Cloud Permissions Audited:
1. `android.permission.INTERNET`:
   - **Rationale**: Scanly is 100% offline. All OCR, PDF generation, image filtering, database persistence, and file encryption occur strictly on-device.
   - **Verification**: Verified absent from `AndroidManifest.xml`. With Firebase and network dependencies removed, no AAR will inject this permission into the merged manifest.
2. `android.permission.ACCESS_NETWORK_STATE`:
   - **Rationale**: Used previously by legacy sync repositories to check network availability before triggering cloud uploads.
   - **Verification**: Verified absent from `AndroidManifest.xml`.
3. `android.permission.RECEIVE_BOOT_COMPLETED`:
   - **Rationale**: Used previously by WorkManager's `RescheduleReceiver` to reschedule cloud sync tasks on device reboot.
   - **Verification**: Verified absent from `AndroidManifest.xml`.
4. `android.permission.FOREGROUND_SERVICE`:
   - **Rationale**: Used previously by background upload workers. Local image processing and PDF creation are synchronous or coroutine-based within the user's active session.
   - **Verification**: Verified absent from `AndroidManifest.xml`.

---

### 2.3 Provider & Component Audit

#### 1. Removal of `FirebaseInitProvider`
- **Legacy Implementation**: When Firebase dependencies were present, `com.google.firebase.provider.FirebaseInitProvider` was merged into the application to auto-initialize FirebaseApp instances. In apps attempting to disable automatic initialization, an explicit override `<provider android:name="com.google.firebase.provider.FirebaseInitProvider" ... tools:node="remove" />` was declared.
- **Offline Assessment**: Because all Firebase dependencies (`firebase-bom`, `firebase-auth`, `firebase-firestore`, `firebase-storage`, `firebase-analytics`) and the `google-services` plugin have been completely removed from Gradle build scripts, `FirebaseInitProvider` is not present in the dependency tree or manifest. No `tools:node="remove"` stub is needed because the class does not exist.
- **Diff / Elimination Plan**:
  ```diff
  -        <!-- Legacy FirebaseInitProvider removed -->
  -        <provider
  -            android:name="com.google.firebase.provider.FirebaseInitProvider"
  -            android:authorities="${applicationId}.firebaseinitprovider"
  -            tools:node="remove" />
  ```

#### 2. Removal of WorkManager `InitializationProvider`
- **Legacy Implementation**: WorkManager utilized `androidx.startup.InitializationProvider` with `<meta-data android:name="androidx.work.WorkManagerInitializer" ... />`.
- **Offline Assessment**: WorkManager (`androidx.work:work-runtime-ktx`, `androidx.hilt:hilt-work`) has been removed from Gradle dependencies. Standard `androidx.startup.InitializationProvider` continues to run cleanly for AndroidX lifecycle and emoji components without WorkManager overhead.
- **Diff / Elimination Plan**:
  ```diff
  -        <!-- Legacy WorkManager InitializationProvider removed -->
  -        <provider
  -            android:name="androidx.startup.InitializationProvider"
  -            android:authorities="${applicationId}.androidx-startup"
  -            tools:node="remove">
  -            <meta-data
  -                android:name="androidx.work.WorkManagerInitializer"
  -                android:value="androidx.startup"
  -                tools:node="remove" />
  -        </provider>
  ```

#### 3. FileProvider Configuration
- **Declaration**:
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
- **XML Path Mapping (`app/src/main/res/xml/file_paths.xml`)**:
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <paths>
      <!-- Internal app storage for documents -->
      <files-path name="documents" path="documents/" />

      <!-- Internal app storage for thumbnails -->
      <files-path name="thumbnails" path="thumbnails/" />

      <!-- Cache directory for temporary files (PDF exports, etc.) -->
      <cache-path name="cache" path="/" />

      <!-- Temp directory for scanner output -->
      <cache-path name="temp" path="temp/" />
  </paths>
  ```
- **Critical Cross-Reference Finding**:
  - `AndroidManifest.xml` declares: `android:authorities="${applicationId}.fileprovider"`
  - `PdfGeneratorService.kt:65` uses: `FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)` (Matches Manifest ✅)
  - `Extensions.kt:52` uses: `FileProvider.getUriForFile(this, "$packageName.provider", file)` (Mismatch: uses `.provider` instead of `.fileprovider`).
  - *Action*: In Milestone 3, `Extensions.kt` line 52 must be corrected to `"$packageName.fileprovider"` to align with the manifest authority.

#### 4. Activity and Application Architecture
- `DocScannerApp`: Configured with `android:name=".DocScannerApp"`, initializes notification channels for scan alerts on startup.
- `MainActivity`: Configured with `android:name=".MainActivity"`, `android:exported="true"`, `android:launchMode="singleTop"`. Inherits from `FragmentActivity`, enabling `BiometricPrompt` host integration.
- `ML Kit Dependencies`: `<meta-data android:name="com.google.mlkit.vision.DEPENDENCIES" android:value="ocr" />` instructs Google Play Services to ensure OCR models are provisioned on device for fast on-device text recognition.

---

## 3. Line-by-Line Audit of `app/proguard-rules.pro`

### 3.1 Full Source Proguard Configuration
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

---

### 3.2 Proguard Rules Audit & Diff Plan

#### 1. Removal of Firebase Proguard Rules
- **Legacy Rules**:
  ```proguard
  # Firebase
  -keepattributes Signature
  -keepclassmembers class * {
    @com.google.firebase.database.IgnoreExtraProperties *;
  }
  -dontwarn com.google.firebase.**
  ```
- **Analysis**:
  - With Firebase Realtime Database and Cloud Firestore removed, referencing `@com.google.firebase.database.IgnoreExtraProperties` generates warnings during release R8 compilation because the annotation class does not exist in the classpath.
  - `-keepattributes Signature` is only needed when generic type reflection is required by external deserializers (e.g. Firebase reflection mappers). Room and Kotlinx Serialization use compile-time code generation / KSP.
- **Diff / Elimination Plan**:
  ```diff
  -# Firebase
  --keepattributes Signature
  --keepclassmembers class * {
  -  @com.google.firebase.database.IgnoreExtraProperties *;
  -}
  ```
- **Verification**: Verified that `app/proguard-rules.pro` does not contain any Firebase rules.

#### 2. Verification of Retained Rules for Offline First Stack
1. **Room Database**:
   ```proguard
   -keep class * extends androidx.room.RoomDatabase
   -dontwarn androidx.room.paging.**
   ```
   - Retains Room database implementations (`AppDatabase_Impl`) generated by KSP.
2. **Dagger Hilt**:
   ```proguard
   -keep,allowobfuscation,allowshrinking @dagger.hilt.android.AndroidEntryPoint class *
   -keep,allowobfuscation,allowshrinking @dagger.hilt.android.HiltAndroidApp class *
   ```
   - Retains `@AndroidEntryPoint` and `@HiltAndroidApp` classes during R8 shrinking while allowing identifier obfuscation.
3. **Kotlinx Serialization / Annotations**:
   ```proguard
   -keepattributes *Annotation*, InnerClasses
   -dontnote kotlinx.serialization.AnnotationsKt
   -keep,allowcreations,allowoptimization class * {
       @kotlinx.serialization.Serializable *;
   }
   ```
   - Protects domain models and JSON serializers.
4. **Coroutines, ML Kit, Biometric & AndroidX Security Crypto**:
   - `kotlinx-coroutines-android`, `play-services-mlkit-document-scanner`, `mlkit:text-recognition`, `androidx.biometric:biometric`, and `androidx.security:security-crypto` ship with embedded `consumer-rules.pro` files.
   - R8 automatically includes consumer rules from AAR dependencies during release builds.

---

## 4. Synthesis and Milestone 2 Verification Checklist

| Configuration Target | Verification Item | Expected State | Actual Verified State | Status |
|---|---|---|---|---|
| `AndroidManifest.xml` | `FirebaseInitProvider` | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | WorkManager Initializer | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | `INTERNET` permission | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | `ACCESS_NETWORK_STATE` | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | `RECEIVE_BOOT_COMPLETED` | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | `FOREGROUND_SERVICE` | Absent | Absent | **PASS** |
| `AndroidManifest.xml` | `CAMERA` permission | Present | Present (Line 5) | **PASS** |
| `AndroidManifest.xml` | Storage permissions | Bounded by maxSdk | Present (Lines 7-9, maxSdk=32/28) | **PASS** |
| `AndroidManifest.xml` | `USE_BIOMETRIC` permission | Present | Present (Line 11) | **PASS** |
| `AndroidManifest.xml` | `POST_NOTIFICATIONS` permission | Present | Present (Line 12) | **PASS** |
| `AndroidManifest.xml` | `FileProvider` definition | Authorities & file_paths | Present (Lines 39-47) | **PASS** |
| `app/proguard-rules.pro` | Firebase rules | Absent | Absent | **PASS** |
| `app/proguard-rules.pro` | Room rules | Present | Present (Lines 1-3) | **PASS** |
| `app/proguard-rules.pro` | Hilt rules | Present | Present (Lines 5-7) | **PASS** |
| `app/proguard-rules.pro` | Kotlinx rules | Present | Present (Lines 9-14) | **PASS** |

---

## 5. Forward-Looking Notes for Milestone 3

1. **FileProvider Authority Alignment**:
   - In `app/src/main/java/com/docscanner/app/util/Extensions.kt:52`, change `"$packageName.provider"` to `"$packageName.fileprovider"` to ensure compatibility with `AndroidManifest.xml` and `PdfGeneratorService.kt:65`.
2. **Unit Testing Harness**:
   - Unit tests in Milestone 3 can utilize the clean test dependencies (`junit`, `mockk`, `turbine`, `room-testing`) to test local database DAOs, image filter matrices, PDF generation math, and encryption algorithms in total isolation.
