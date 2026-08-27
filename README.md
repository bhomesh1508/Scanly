<h1 align="center">
  <br>
  <img src="https://raw.githubusercontent.com/google/material-design-icons/master/png/image/camera_alt/materialicons/48dp/2x/baseline_camera_alt_black_48dp.png" alt="Scanly" width="120">
  <br>
  Scanly - Advanced Document Scanner
  <br>
</h1>

<h4 align="center">A high-performance, offline-first Android Document Scanner built with Jetpack Compose & ML Kit.</h4>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#installation">Installation</a> •
  <a href="#technologies-used">Technologies Used</a> •
  <a href="#license">License</a>
</p>

![Screenshot](https://via.placeholder.com/1200x600.png?text=Scanly+-+Professional+Document+Scanning)

---

## 🚀 Key Features

*   **Intelligent Scanning**: Utilizes Google's ML Kit for high-accuracy document edge detection, perspective correction, and automatic capture.
*   **Offline-First & Private**: 100% of the scanning and processing happens locally on your device. No cloud storage is required, ensuring absolute privacy.
*   **Multi-Page Documents**: Effortlessly scan, reorder, merge, and split multi-page PDF documents.
*   **Advanced Image Processing**: Apply professional filters (Grayscale, Black & White, Magic Color) to enhance document legibility.
*   **Biometric Security**: Protect your sensitive documents with an integrated App Lock (Fingerprint/Face Unlock).
*   **PDF & JPEG Export**: Export your scans instantly into high-quality PDFs or JPEG images.
*   **Modern UI/UX**: Designed meticulously with Material Design 3 and Jetpack Compose for a buttery-smooth, edge-to-edge user experience.

## 🏗 Architecture

Scanly follows modern Android development best practices, adhering to the **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** design pattern.

*   **Presentation Layer**: Built entirely with Jetpack Compose. State management is handled efficiently using Kotlin `StateFlow`.
*   **Domain Layer**: Contains the core business logic, UseCases, and pure Kotlin models.
*   **Data Layer**: Manages local data persistence using Room Database. (Firebase Sync module is decoupled and currently mocked for strict offline compliance).

## 🛠 Technologies Used

*   **Kotlin**: 100% Kotlin codebase.
*   **Jetpack Compose**: For a declarative and reactive UI.
*   **Google ML Kit (Document Scanner)**: For state-of-the-art machine learning edge detection.
*   **Coroutines & Flow**: For asynchronous programming and reactive streams.
*   **Dagger Hilt**: For robust Dependency Injection.
*   **Room Database**: For local SQLite persistence.
*   **Jetpack Navigation**: Type-safe navigation in Compose.
*   **Material Design 3**: For dynamic theming and modern components.

## 📥 Installation

You can install the app directly by downloading the compiled APK, or build it yourself from source.

### Build from Source

1.  Clone the repository:
    ```bash
    git clone https://github.com/bhomesh1508/Scanly.git
    ```
2.  Open the project in **Android Studio (Koala or newer)**.
3.  Let Gradle sync all dependencies.
4.  Build and run the app on a physical Android device (API 24+). *Note: The ML Kit scanner requires a physical device with a camera to function properly.*

## 🔒 Privacy & Security

Scanly is designed with privacy at its core. By default, the application does not transmit any document data over the internet. All ML models run strictly on-device, and documents are stored securely in the app's local sandbox environment.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
<p align="center">Made with ❤️ for Android.</p>
