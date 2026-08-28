## 2026-08-28T08:19:00Z
Mission: Perform a deep, comprehensive security & privacy audit of the Scanly offline Android application.
Focus on:
1. AndroidManifest.xml: Check exported components (activities, services, receivers, providers), missing android:exported attributes, intent-filters, custom permissions, application flags (allowBackup, usesCleartextTraffic, networkSecurityConfig).
2. Local File Storage & Data Privacy: Internal vs external storage paths, scoped storage, cache management, temp file cleanup, world-readable/writable files, FileProvider configuration (file_paths.xml) and grantUriPermissions.
3. Offline Data Safety & Zero-Leakage: Ensure no unauthorized network permissions (INTERNET), no analytics/tracking beacons, no background telemetry, no clipboard leaks of sensitive doc data.
4. Logging & Information Exposure: Audit Log.d/Log.e/Log.v statements across the codebase to ensure no PII, document names/paths, raw image bytes, or sensitive metadata are logged to logcat in production builds.
5. IPC & Intent Safety: Validate incoming/outgoing intents, pending intents (immutable flags: FLAG_IMMUTABLE), deep links, and file sharing mechanisms.
