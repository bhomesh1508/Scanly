package com.docscanner.app.presentation.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.UserProfile
import com.docscanner.app.domain.repository.AuthRepository
import com.docscanner.app.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _storageUsage = MutableStateFlow<Pair<Long, Long>>(Pair(0L, 0L))
    val storageUsage: StateFlow<Pair<Long, Long>> = _storageUsage.asStateFlow()

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        refreshUsage()
    }

    fun refreshUsage() {
        viewModelScope.launch {
            syncRepository.getStorageUsage().collect {
                _storageUsage.value = it
            }
        }
    }
}
