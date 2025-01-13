package com.example.remote_control.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.remote_control.data.DataProvider
import com.example.remote_control.network.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SecondScreen(networkService: NetworkService, onNavigate: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            coroutineScope.launch {
                // Toggle off and on overlays
                networkService.emitToggleOverlay(1792, display = false, type = "picture")
                networkService.emitToggleOverlay(1784, display = true, type = "website")
                networkService.emitToggleOverlay(1775, display = true, type = "website")
                networkService.emitToggleOverlay(1776, display = true, type = "website")

                delay(3000)

                val yearDetail = DataProvider.locationData["Aasee"]?.get("high")?.temperatures?.get(2020)
                if (yearDetail != null) {
                    // Toggle on website overlays
                    yearDetail.overlays.websites.forEach { overlayId ->
                        networkService.emitToggleOverlay(overlayId, display = true, type = "website")
                    }
                    // Toggle on picture overlays
                    yearDetail.overlays.pictures.forEach { overlayId ->
                        networkService.emitToggleOverlay(overlayId, display = true, type = "picture")
                    }

                    // Set initial temperature
                    networkService.postTemperature(23.4)
                    networkService.postCalendar(2000, 5)
                    networkService.postInfoText(yearDetail.text)
                }

                // Navigate after operations
                onNavigate()
            }
        }) {
            Text("Toggle Overlay and Go to Fourth Page")
        }
    }
}
