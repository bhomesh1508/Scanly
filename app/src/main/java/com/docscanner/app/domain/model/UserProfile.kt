package com.docscanner.app.domain.model

/**
 * Represents the profile of an authenticated user.
 *
 * @property uid Unique user ID assigned by the authentication provider.
 * @property email User's email address.
 * @property displayName User's display name.
 * @property photoUrl URL to the user's profile picture.
 * @property cloudStorageUsedBytes Total bytes used by synced documents in cloud storage.
 * @property cloudStorageLimitBytes Total bytes available in cloud storage (default 2GB).
 * @property createdAt Timestamp when the user profile was created.
 */
data class UserProfile(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val cloudStorageUsedBytes: Long = 0,
    val cloudStorageLimitBytes: Long = 2L * 1024 * 1024 * 1024, // 2 GB
    val createdAt: Long
)
