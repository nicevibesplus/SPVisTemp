package com.example.remote_control.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import android.content.Context
import android.widget.Toast
import com.example.remote_control.network.NetworkService
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun SwitchWithLabel(
    label: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = if (enabled) onCheckedChange else null, // Disable toggle if not enabled
            enabled = enabled // Pass enabled state to the Switch
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            label,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}


fun toggleAllOverlays(
    context: Context,
    networkService: NetworkService,
    videoId: Int,
    excludedOverlayIds: Set<Int> = emptySet() // New parameter for exclusions
) {
    networkService.fetchOverlays(videoId) { overlays, error ->
        if (overlays != null) {
            overlays.forEach { (overlayId, type) ->
                if (!excludedOverlayIds.contains(overlayId)) { // Skip excluded IDs
                    networkService.emitToggleOverlay(overlayId, display = false, type = type)
                }
            }
        } else {
        }
    }
}


