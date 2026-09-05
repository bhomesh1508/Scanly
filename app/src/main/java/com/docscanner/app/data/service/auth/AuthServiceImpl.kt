package com.docscanner.app.data.service.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.docscanner.app.domain.model.UserAccount
import com.docscanner.app.domain.service.auth.AuthService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AuthService {

    private val scope = CoroutineScope(Dispatchers.IO)

    private object AuthKeys {
        val USER_ID = stringPreferencesKey("auth_user_id")
        val USER_EMAIL = stringPreferencesKey("auth_user_email")
        val USER_NAME = stringPreferencesKey("auth_user_name")
        val USER_CREATED = stringPreferencesKey("auth_user_created")
    }

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    override val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    init {
        scope.launch {
            loadPersistedSession()
        }
    }

    private suspend fun loadPersistedSession() {
        val prefs = dataStore.data.firstOrNull() ?: return
        val uid = prefs[AuthKeys.USER_ID]
        val email = prefs[AuthKeys.USER_EMAIL]
        val name = prefs[AuthKeys.USER_NAME] ?: ""
        val created = prefs[AuthKeys.USER_CREATED]?.toLongOrNull() ?: System.currentTimeMillis()

        if (!uid.isNullOrBlank() && !email.isNullOrBlank()) {
            _currentUser.value = UserAccount(
                uid = uid,
                email = email,
                displayName = name,
                photoUrl = null,
                isAnonymous = false,
                createdAt = created
            )
        }
    }

    override suspend fun signIn(email: String, password: String): Result<UserAccount> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        // Generate or resolve deterministic user UID based on email for the current session
        val uid = UUID.nameUUIDFromBytes(trimmedEmail.lowercase().toByteArray()).toString()
        val displayName = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        val account = UserAccount(
            uid = uid,
            email = trimmedEmail,
            displayName = displayName,
            photoUrl = null,
            isAnonymous = false,
            createdAt = System.currentTimeMillis()
        )

        persistSession(account)
        _currentUser.value = account
        return Result.success(account)
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<UserAccount> {
        val trimmedEmail = email.trim()
        val trimmedName = displayName.trim()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        val uid = UUID.nameUUIDFromBytes(trimmedEmail.lowercase().toByteArray()).toString()
        val name = if (trimmedName.isNotBlank()) trimmedName else trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        val account = UserAccount(
            uid = uid,
            email = trimmedEmail,
            displayName = name,
            photoUrl = null,
            isAnonymous = false,
            createdAt = System.currentTimeMillis()
        )

        persistSession(account)
        _currentUser.value = account
        return Result.success(account)
    }

    override suspend fun signOut(): Result<Unit> {
        dataStore.edit { prefs ->
            prefs.remove(AuthKeys.USER_ID)
            prefs.remove(AuthKeys.USER_EMAIL)
            prefs.remove(AuthKeys.USER_NAME)
            prefs.remove(AuthKeys.USER_CREATED)
        }
        _currentUser.value = null
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        val trimmed = email.trim()
        if (trimmed.isBlank() || !trimmed.contains("@")) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address"))
        }
        return Result.success(Unit)
    }

    private suspend fun persistSession(account: UserAccount) {
        dataStore.edit { prefs ->
            prefs[AuthKeys.USER_ID] = account.uid
            prefs[AuthKeys.USER_EMAIL] = account.email
            prefs[AuthKeys.USER_NAME] = account.displayName
            prefs[AuthKeys.USER_CREATED] = account.createdAt.toString()
        }
    }
}
