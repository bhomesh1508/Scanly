<h1 align="center">
  <br>
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/image/camera_alt/materialicons/48dp/2x/baseline_camera_alt_black_48dp.png" alt="Scanly" width="120">
  <br>
  Scanly - Advanced Document Scanner
  <br>
</h1>

<h4 align="center">A high-performance, offline-first Android Document Scanner with optional Cloud Backup &amp; Sync built with Jetpack Compose &amp; ML Kit.</h4>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#cloud-storage--sync">Cloud Storage &amp; Sync</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#installation">Installation</a> •
  <a href="#technologies-used">Technologies Used</a> •
  <a href="#license">License</a>
</p>

---

## 🚀 Key Features

*   **Intelligent Scanning**: Utilizes Google's ML Kit for high-accuracy document edge detection, perspective correction, and automatic capture.
*   **100% Offline-First Core**: Scanning, image enhancement, and PDF generation work seamlessly on-device without an internet connection or account.
*   **Optional Secure Cloud Backup**: Securely back up PDFs, JPGs, PNGs, thumbnails, and metadata with authenticated cloud storage.
*   **Storage Dashboard**: Monitor cloud quota usage with detailed breakdowns for Documents, Images, and PDFs.
*   **Flexible Save & Upload Options**: Choose between *Save Locally*, *Upload to Cloud*, or *Save & Upload* for every document.
*   **Multi-Page Documents**: Effortlessly scan, reorder, duplicate, merge, and split multi-page documents.
*   **Advanced Image Processing**: Apply professional filters (Grayscale, Black & White, Magic Color) and fine-tune brightness and contrast.
*   **Biometric Security**: Protect your sensitive documents with integrated App Lock (Fingerprint/Face Unlock).
*   **Modern UI/UX**: Meticulously crafted with Material Design 3 and Jetpack Compose for fluid animations and edge-to-edge aesthetics.

---

## ☁️ Cloud Storage & Sync

Scanly features an optional, privacy-respecting Cloud Storage subsystem:

### 1. Cloud Backup & Format Preservation
*   Back up full-fidelity PDFs, processed images (JPG/PNG), thumbnails, page ordering, and OCR metadata.
*   Real-time upload progress indicators and transparent sync statuses.

### 2. Synchronization Engine
*   **Clear Sync States**: Every document clearly indicates its state: `Synced`, `Uploading`, `Downloading`, `Offline`, `Sync Failed`, or `Local Only`.
*   **WorkManager Integration**: Background worker queues offline documents and automatically triggers synchronization when network connectivity returns.
*   **Wi-Fi Only Guard**: Option to restrict uploads strictly to Wi-Fi to preserve mobile cellular data.

### 3. Account & Storage Limits
*   **No Forced Sign-In**: Scanly is completely functional offline without creating an account.
*   **Authenticated Cloud Profile**: Sign up or log in to associate your cloud documents securely with your profile.
*   **Storage Quota**: Standard 10 GB free tier with live quota tracking in the Storage Dashboard.

### 4. Privacy & Consent
*   Documents are **never** uploaded automatically without user consent.
*   Encrypted local cache with complete user control over cache clearing and remote file deletion.

---

## 🏗 Architecture

Scanly is built according to **Clean Architecture** principles and the **MVVM** pattern:

```text
app/
├── data/
│   ├── local/          # Room DB, DAOs (Document, Page, Folder, SyncQueue, CloudDocument)
│   ├── mapper/         # Domain-to-Entity and Entity-to-Domain mappers
│   ├── repository/     # Concrete repositories (DocumentRepository, SettingsRepository)
│   └── service/        # CloudStorageService and AuthService implementations
├── domain/
│   ├── model/          # Pure Kotlin models (Document, Page, CloudDocument, StorageQuota, UserSettings)
│   ├── repository/     # Repository interfaces
│   └── service/        # Service interfaces (CloudStorageService, AuthService)
├── presentation/
│   ├── auth/           # Authentication & Profile screens
│   ├── cloud/          # Cloud Document catalog & Storage Dashboard screens
│   ├── common/         # Shared Compose components (Dialogs, Cards, EmptyStates)
│   ├── editor/         # Image adjustment & filter editor
│   ├── home/           # Local documents library
│   ├── navigation/     # Jetpack Navigation and M3 BottomNavBar
│   ├── scanner/        # Camera & ML Kit document capture
│   ├── settings/       # App preferences & Cloud configuration
│   └── viewer/         # PDF & page reader with OCR text extraction
└── service/
    ├── filter/         # Bitmap processing & ColorMatrix enhancements
    ├── pdf/            # Android PdfDocument generator
    └── sync/           # WorkManager CloudSyncWorker & CloudSyncManager
```

---

## 🛠 Technologies Used

*   **Kotlin**: 100% modern Kotlin codebase with Coroutines & StateFlow.
*   **Jetpack Compose & Material 3**: Declarative UI with dynamic theming.
*   **Google ML Kit (Document Scanner & Text Recognition)**: On-device machine learning models.
*   **AndroidX WorkManager**: Persistent, constraint-aware background synchronization.
*   **Dagger Hilt**: Dependency injection.
*   **Room Database**: Local SQLite persistence with multi-table indexing and cascade handling.
*   **DataStore Preferences**: Fast, asynchronous key-value persistence.
*   **Coil 3**: Asynchronous image loading for Compose.

---

## 📥 Installation

### Build from Source

1.  Clone the repository:
    ```bash
    git clone https://github.com/bhomesh1508/Scanly.git
    ```
2.  Open the project in **Android Studio (Ladybug / Koala or newer)**.
3.  Ensure JDK 17+ or JDK 21 is selected in Gradle Settings.
4.  Build and run on a physical Android device (API 24+):
    ```bash
    ./gradlew assembleDebug
    ```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

<p align="center">Made with ❤️ for Android.</p>
