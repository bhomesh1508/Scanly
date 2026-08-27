package com.docscanner.app.presentation.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.docscanner.app.domain.model.Folder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel = hiltViewModel(),
    onNavigateToFolder: (String) -> Unit
) {
    val folders by viewModel.folders.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folders") },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Folder")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (folders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No folders yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(folders, key = { it.id }) { folder ->
                    FolderItem(
                        folder = folder,
                        onClick = { onNavigateToFolder(folder.id) },
                        onDelete = { viewModel.deleteFolder(folder.id) },
                        onRename = { newName -> viewModel.renameFolder(folder.id, newName) }
                    )
                }
            }
        }

        if (showCreateDialog) {
            var folderName by remember { mutableStateOf("") }
            var folderColor by remember { mutableStateOf(Color.Blue.value.toLong()) }
            
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create Folder") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = folderName,
                            onValueChange = { folderName = it },
                            label = { Text("Folder Name") }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (folderName.isNotBlank()) {
                                viewModel.createFolder(folderName, folderColor)
                                showCreateDialog = false
                            }
                        }
                    ) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun FolderItem(
    folder: Folder,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    ListItem(
        headlineContent = { Text(folder.name) },
        supportingContent = { Text("${folder.documentCount} documents") },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(folder.color), CircleShape)
            )
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        },
        modifier = Modifier.clickable(
            onClick = onClick,
        )
    )
}
