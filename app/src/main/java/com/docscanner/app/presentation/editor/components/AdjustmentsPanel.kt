package com.docscanner.app.presentation.editor.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdjustmentsPanel(
    brightness: Float,
    contrast: Float,
    onBrightnessChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Brightness")
            Text("${(brightness * 100).toInt()}%")
        }
        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            valueRange = -1f..1f,
            steps = 200
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Contrast")
            Text("${(contrast * 100).toInt()}%")
        }
        Slider(
            value = contrast,
            onValueChange = onContrastChange,
            valueRange = -1f..1f,
            steps = 200
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                onBrightnessChange(0f)
                onContrastChange(0f)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }
}
