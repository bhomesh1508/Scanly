package com.docscanner.app.data.repository

import android.content.Context
import com.docscanner.app.domain.model.UserProfile
import com.docscanner.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    override suspend fun signInWithGoogle(activityContext: Context): Result<UserProfile> {
        val user = UserProfile("local_user", "offline@docscanner.app", "Offline User", null, 0L, 5L * 1024 * 1024 * 1024, System.currentTimeMillis())
        _currentUser.value = user
        _isAuthenticated.value = true
        return Result.success(user)
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> {
        val user = UserProfile("local_user", email, "Offline User", null, 0L, 5L * 1024 * 1024 * 1024, System.currentTimeMillis())
        _currentUser.value = user
        _isAuthenticated.value = true
        return Result.success(user)
    }

    override suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<UserProfile> {
        val user = UserProfile("local_user", email, displayName, null, 0L, 5L * 1024 * 1024 * 1024, System.currentTimeMillis())
        _currentUser.value = user
        _isAuthenticated.value = true
        return Result.success(user)
    }

    override suspend fun signOut() {
        _currentUser.value = null
        _isAuthenticated.value = false
    }

    override suspend fun deleteAccount(): Result<Unit> {
        _currentUser.value = null
        _isAuthenticated.value = false
        return Result.success(Unit)
    }
}
