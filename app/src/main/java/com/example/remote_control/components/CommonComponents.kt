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

@Composable
fun SwitchWithLabel(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row {
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        Spacer(modifier = androidx.compose.ui.Modifier.width(8.dp))
        Text(label)
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


