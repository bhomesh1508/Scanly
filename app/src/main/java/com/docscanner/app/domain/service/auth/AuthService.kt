package com.docscanner.app.domain.service.auth

import com.docscanner.app.domain.model.UserAccount
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication service interface managing user accounts, sessions, and credentials.
 */
interface AuthService {

    /**
     * Currently authenticated user account, or null if unauthenticated.
     */
    val currentUser: StateFlow<UserAccount?>

    /**
     * True if the user is currently signed in.
     */
    val isSignedIn: Boolean
        get() = currentUser.value != null

    /**
     * Signs in with email and password.
     */
    suspend fun signIn(email: String, password: String): Result<UserAccount>

    /**
     * Registers a new user account with email, password, and display name.
     */
    suspend fun signUp(email: String, password: String, displayName: String): Result<UserAccount>

    /**
     * Signs out the currently authenticated user.
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Sends password reset email.
     */
    suspend fun resetPassword(email: String): Result<Unit>
}
