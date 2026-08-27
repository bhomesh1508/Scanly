package com.docscanner.app.data.repository

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.docscanner.app.domain.model.FilterType
import com.docscanner.app.domain.model.UserSettings.ThemeMode
import com.docscanner.app.domain.model.UserSettings
import com.docscanner.app.domain.model.PageSize
import com.docscanner.app.domain.model.QualityLevel
import com.docscanner.app.domain.model.MarginPreset
import com.docscanner.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Application
) : SettingsRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_FILTER = stringPreferencesKey("default_filter")
        val DEFAULT_PAGE_SIZE = stringPreferencesKey("default_page_size")
        val DEFAULT_PDF_QUALITY = stringPreferencesKey("default_pdf_quality")
        val DEFAULT_MARGIN = stringPreferencesKey("default_margin")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val AUTO_SYNC_ENABLED = booleanPreferencesKey("auto_sync_enabled")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val ENCRYPT_NEW_DOCUMENTS = booleanPreferencesKey("encrypt_new_documents")
    }

    override val settings: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            theme = ThemeMode.valueOf(preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
            defaultFilter = FilterType.valueOf(preferences[PreferencesKeys.DEFAULT_FILTER] ?: FilterType.ORIGINAL.name),
            defaultPageSize = PageSize.valueOf(preferences[PreferencesKeys.DEFAULT_PAGE_SIZE] ?: "A4"),
            defaultPdfQuality = QualityLevel.valueOf(preferences[PreferencesKeys.DEFAULT_PDF_QUALITY] ?: "HIGH"),
            defaultMargin = MarginPreset.valueOf(preferences[PreferencesKeys.DEFAULT_MARGIN] ?: "NORMAL"),
            appLockEnabled = preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false,
            autoSyncEnabled = preferences[PreferencesKeys.AUTO_SYNC_ENABLED] ?: true,
            hasSeenOnboarding = preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] ?: false,
            encryptNewDocuments = preferences[PreferencesKeys.ENCRYPT_NEW_DOCUMENTS] ?: false
        )
    }

    override suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = settings.theme.name
            preferences[PreferencesKeys.DEFAULT_FILTER] = settings.defaultFilter.name
            preferences[PreferencesKeys.DEFAULT_PAGE_SIZE] = settings.defaultPageSize.name
            preferences[PreferencesKeys.DEFAULT_PDF_QUALITY] = settings.defaultPdfQuality.name
            preferences[PreferencesKeys.DEFAULT_MARGIN] = settings.defaultMargin.name
            preferences[PreferencesKeys.APP_LOCK_ENABLED] = settings.appLockEnabled
            preferences[PreferencesKeys.AUTO_SYNC_ENABLED] = settings.autoSyncEnabled
            preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] = settings.hasSeenOnboarding
            preferences[PreferencesKeys.ENCRYPT_NEW_DOCUMENTS] = settings.encryptNewDocuments
        }
    }

    override fun getTheme(): Flow<ThemeMode> {
        return context.dataStore.data.map { preferences ->
            ThemeMode.valueOf(preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
        }
    }

    override fun isAppLockEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.APP_LOCK_ENABLED] ?: false
        }
    }
}
