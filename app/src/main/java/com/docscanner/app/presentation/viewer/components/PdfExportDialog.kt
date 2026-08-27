package com.docscanner.app.presentation.viewer.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.docscanner.app.domain.model.MarginPreset
import com.docscanner.app.domain.model.PageSize
import com.docscanner.app.domain.model.PdfExportOptions
import com.docscanner.app.domain.model.QualityLevel

@Composable
fun PdfExportDialog(
    onExport: (PdfExportOptions) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPageSize by remember { mutableStateOf(PageSize.A4) }
    var selectedMargin by remember { mutableStateOf(MarginPreset.NORMAL) }
    var selectedQuality by remember { mutableStateOf(QualityLevel.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export PDF") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Options would go here (Dropdowns, Radios)")
                // Simulated UI for brevity
            }
        },
        confirmButton = {
            Button(onClick = {
                onExport(
                    PdfExportOptions(
                        pageSize = selectedPageSize,
                        margin = selectedMargin,
                        quality = selectedQuality,
                        documentTitle = "",
                        author = ""
                    )
                )
            }) {
                Text("Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
