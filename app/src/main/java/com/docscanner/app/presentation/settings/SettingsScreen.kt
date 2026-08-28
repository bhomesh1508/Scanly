package com.docscanner.app.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.docscanner.app.domain.model.UserSettings.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToTrash: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Appearance
            Text("Appearance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            var showThemeMenu by remember { mutableStateOf(false) }
            ListItem(
                headlineContent = { Text("Theme") },
                supportingContent = { Text(settings.theme.name) },
                modifier = Modifier.clickable { showThemeMenu = true }
            )
            Divider()

            // Security
            Text("Security", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            ListItem(
                headlineContent = { Text("App Lock") },
                trailingContent = {
                    Switch(checked = settings.appLockEnabled, onCheckedChange = { viewModel.toggleAppLock(it) })
                }
            )
            ListItem(
                headlineContent = { Text("Encrypt New Documents") },
                trailingContent = {
                    Switch(checked = settings.encryptNewDocuments, onCheckedChange = { viewModel.toggleEncryption(it) })
                }
            )
            Divider()

            // Data
            Text("Data", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            ListItem(
                headlineContent = { Text("Trash") },
                modifier = Modifier.clickable { onNavigateToTrash() }
            )
            ListItem(
                headlineContent = { Text("Clear Cache") },
                modifier = Modifier.clickable { viewModel.clearCache(context) }
            )
            Divider()

            // About
            Text("About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            ListItem(headlineContent = { Text("App Version") }, supportingContent = { Text("1.0.0") })
        }
    }
}
