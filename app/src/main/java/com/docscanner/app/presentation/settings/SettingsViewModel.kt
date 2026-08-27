package com.docscanner.app.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.docscanner.app.domain.model.FilterType
import com.docscanner.app.domain.model.PageSize
import com.docscanner.app.domain.model.QualityLevel
import com.docscanner.app.domain.model.UserSettings.ThemeMode
import com.docscanner.app.domain.model.UserProfile
import com.docscanner.app.domain.model.UserSettings
import com.docscanner.app.domain.repository.AuthRepository
import com.docscanner.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsRepository.settings
        .stateIn(
            viewModelScope, 
            SharingStarted.WhileSubscribed(5000), 
            UserSettings(ThemeMode.SYSTEM, FilterType.ORIGINAL, PageSize.A4, QualityLevel.HIGH, com.docscanner.app.domain.model.MarginPreset.NORMAL, false, false, false, false)
        )

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateTheme(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(theme = mode)) }
    }

    fun updateDefaultFilter(filter: FilterType) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(defaultFilter = filter)) }
    }

    fun updateDefaultPageSize(size: PageSize) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(defaultPageSize = size)) }
    }

    fun updateDefaultQuality(quality: QualityLevel) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(defaultPdfQuality = quality)) }
    }

    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(appLockEnabled = enabled)) }
    }

    fun toggleAutoSync(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(autoSyncEnabled = enabled)) }
    }

    fun toggleEncryption(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings(settings.value.copy(encryptNewDocuments = enabled)) }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }

    fun clearCache(context: Context) {
        context.cacheDir.deleteRecursively()
    }
}
