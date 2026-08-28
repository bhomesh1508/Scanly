package com.docscanner.app.presentation.viewer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.docscanner.app.presentation.viewer.components.OcrResultSheet
import com.docscanner.app.presentation.viewer.components.PdfExportDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    viewModel: ViewerViewModel,
    onNavigateToEditor: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val document by viewModel.document.collectAsState()
    val pages by viewModel.pages.collectAsState()
    val ocrText by viewModel.ocrText.collectAsState()
    val ocrLoading by viewModel.ocrLoading.collectAsState()
    
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showOcrSheet by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = { 
                            showMenu = false
                            document?.id?.let { onNavigateToEditor(it) } 
                        })
                        DropdownMenuItem(text = { Text("Share as PDF") }, onClick = { showMenu = false; showExportDialog = true })
                        DropdownMenuItem(text = { Text("Extract Text (OCR)") }, onClick = {
                            showMenu = false
                            showOcrSheet = true
                            viewModel.runOcr(context)
                        })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = {
                            showMenu = false
                            viewModel.deleteDocument()
                            onNavigateBack()
                        })
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { pageIndex ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    pages.getOrNull(pageIndex)?.let { page ->
                        coil3.compose.AsyncImage(
                            model = page.processedImagePath,
                            contentDescription = "Document Page",
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }
                }
            }
            Text(
                text = "Page ${pagerState.currentPage + 1} of ${pages.size}",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp)
            )
        }
    }

    if (showExportDialog) {
        PdfExportDialog(
            onExport = { options ->
                showExportDialog = false
                viewModel.exportPdf(options)
            },
            onDismiss = { showExportDialog = false }
        )
    }

    if (showOcrSheet) {
        OcrResultSheet(
            text = ocrText,
            isLoading = ocrLoading,
            onCopy = { viewModel.copyOcrText(context) },
            onDismiss = { showOcrSheet = false }
        )
    }
}
