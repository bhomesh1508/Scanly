package com.docscanner.app.domain.repository

import android.content.Context
import com.docscanner.app.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface defining authentication operations.
 */
interface AuthRepository {

    /**
     * Exposes the currently authenticated user profile.
     * Null if no user is authenticated.
     */
    val currentUser: StateFlow<UserProfile?>

    /**
     * Exposes a boolean indicating whether a user is currently authenticated.
     */
    val isAuthenticated: StateFlow<Boolean>

    /**
     * Initiates Google Sign-In using the provided activity context.
     */
    suspend fun signInWithGoogle(activityContext: Context): Result<UserProfile>

    /**
     * Initiates sign-in using email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>

    /**
     * Registers a new account using email and password.
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile>

    /**
     * Signs out the current user and clears local auth state.
     */
    suspend fun signOut()

    /**
     * Deletes the currently authenticated user's account and all associated data.
     */
    suspend fun deleteAccount(): Result<Unit>
}
