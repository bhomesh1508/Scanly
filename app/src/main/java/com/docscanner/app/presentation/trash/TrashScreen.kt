package com.docscanner.app.presentation.trash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.docscanner.app.domain.model.Document

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val trashedDocs by viewModel.trashedDocuments.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trash") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Empty Trash") },
                            onClick = {
                                viewModel.emptyTrash()
                                showMenu = false
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (trashedDocs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Trash is empty")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(trashedDocs, key = { it.id }) { doc ->
                    ListItem(
                        headlineContent = { Text(doc.title) },
                        supportingContent = { Text("Will be permanently deleted soon") },
                        trailingContent = {
                            Row {
                                TextButton(onClick = { viewModel.restoreDocument(doc.id) }) {
                                    Text("Restore")
                                }
                                TextButton(onClick = { viewModel.permanentlyDelete(doc.id) }) {
                                    Text("Delete")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
