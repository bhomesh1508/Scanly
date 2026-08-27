package com.docscanner.app.presentation.editor.components

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.docscanner.app.domain.model.FilterType

@Composable
fun FilterSelector(
    filters: List<FilterType>,
    selectedFilter: FilterType,
    previewBitmaps: Map<FilterType, Bitmap?>,
    onFilterSelected: (FilterType) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onFilterSelected(filter) }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            width = if (selectedFilter == filter) 2.dp else 0.dp,
                            color = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                ) {
                    // Thumbnail preview would render here
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = filter.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
