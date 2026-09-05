package com.docscanner.app.domain.model

/**
 * Represents an authenticated user profile for cloud services.
 *
 * @property uid Unique user ID provided by auth provider.
 * @property email User's email address.
 * @property displayName User's display or chosen profile name.
 * @property photoUrl Optional avatar URL.
 * @property isAnonymous True if using an ephemeral guest account.
 * @property createdAt Timestamp when account was registered.
 */
data class UserAccount(
    val uid: String,
    val email: String,
    val displayName: String = "",
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
