package com.docscanner.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.docscanner.app.presentation.navigation.AppNavigation
import com.docscanner.app.presentation.settings.SettingsViewModel
import com.docscanner.app.presentation.theme.DocScannerTheme
import com.docscanner.app.presentation.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.settings.collectAsState()
            val themeMode = when (settings.theme) {
                com.docscanner.app.domain.model.UserSettings.ThemeMode.SYSTEM -> ThemeMode.SYSTEM
                com.docscanner.app.domain.model.UserSettings.ThemeMode.LIGHT -> ThemeMode.LIGHT
                com.docscanner.app.domain.model.UserSettings.ThemeMode.DARK -> ThemeMode.DARK
            }
            DocScannerTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
