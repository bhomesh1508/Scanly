package com.docscanner.app.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.docscanner.app.presentation.editor.components.AdjustmentsPanel
import com.docscanner.app.presentation.editor.components.FilterSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel,
    onNavigateToViewer: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val document by viewModel.document.collectAsState()
    val pages by viewModel.pages.collectAsState()
    val selectedPageIndex by viewModel.selectedPageIndex.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val contrast by viewModel.contrast.collectAsState()
    val previewBitmap by viewModel.previewBitmap.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document?.title ?: "Edit Document") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.saveChanges()
                        document?.id?.let { onNavigateToViewer(it) } 
                    }) {
                        Icon(Icons.Default.Check, "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                previewBitmap?.let { bitmap ->
                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Document Preview",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } ?: pages.getOrNull(selectedPageIndex)?.let { page ->
                    coil3.compose.AsyncImage(
                        model = page.processedImagePath,
                        contentDescription = "Document Page",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }

            FilterSelector(
                filters = com.docscanner.app.domain.model.FilterType.values().toList(),
                selectedFilter = currentFilter,
                previewBitmaps = emptyMap(),
                onFilterSelected = viewModel::applyFilter
            )

            AdjustmentsPanel(
                brightness = brightness,
                contrast = contrast,
                onBrightnessChange = viewModel::adjustBrightness,
                onContrastChange = viewModel::adjustContrast
            )
            
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                // Page thumbnails
            }
        }
    }
}
