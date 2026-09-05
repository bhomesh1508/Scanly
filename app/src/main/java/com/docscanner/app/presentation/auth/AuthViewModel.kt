package com.docscanner.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.UserAccount
import com.docscanner.app.domain.service.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    val currentUser: StateFlow<UserAccount?> = authService.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun signIn(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authService.signIn(email, pass)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Authentication failed"
            }
        }
    }

    fun signUp(email: String, pass: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = authService.signUp(email, pass, name)
            _isLoading.value = false
            if (result.isSuccess) {
                onSuccess()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }

    fun signOut(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            authService.signOut()
            onSuccess()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
