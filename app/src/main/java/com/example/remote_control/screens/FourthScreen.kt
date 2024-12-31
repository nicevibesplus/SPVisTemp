package com.example.remote_control.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remote_control.components.toggleAllOverlays
import com.example.remote_control.data.DataProvider
import com.example.remote_control.network.NetworkService
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.*



@Composable
fun FourthScreen(
    networkService: NetworkService,
    selectedLocation: String, // Passed from ThirdScreen
    selectedQuality: String,  // Passed from ThirdScreen
    onBackToThird: () -> Unit // Navigation callback to go to the third screen
) {
    val context = LocalContext.current
    var currentYearIndex by remember { mutableStateOf(0) }
    val years = listOf(2040, 2060, 2080, 2100)
    var buttonEnabled by remember { mutableStateOf(true) }

    // Current temperature (starting value: 23.4)
    var currentTemperature by remember { mutableStateOf(23.4) }

    // Measure-specific state
    val measure = DataProvider.locationSpecificMeasures[selectedLocation]?.firstOrNull()
    var isMeasureButtonEnabled by remember { mutableStateOf(false) }
    var isMeasureActivated by remember { mutableStateOf(false) } // New flag for permanent disable

    // Excluded Overlay IDs
    val excludedOverlayIds = remember { mutableStateListOf(1784) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display Current Temperature
        Text("Current Temperature: ${"%.1f".format(currentTemperature)} °C", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // "Next" Button
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Step 1: Toggle off all overlays except excluded IDs
                        val videoId = if (selectedLocation == "Aasee") 1787 else 1766
                        toggleAllOverlays(context, networkService, videoId, excludedOverlayIds = excludedOverlayIds.toSet())
                        delay(1000)

                        // Step 2: Fetch and toggle on overlays for the current year
                        val currentYear = years[currentYearIndex]
                        val yearDetail = DataProvider.locationData[selectedLocation]?.get(selectedQuality)?.temperatures?.get(currentYear)

                        if (yearDetail != null) {
                            // Update the current temperature
                            currentTemperature += yearDetail.temp // Add the tempChange
                            networkService.postTemperature(currentTemperature) // Post updated temperature

                            // Toggle on picture overlays
                            yearDetail.overlays.pictures.forEach { overlayId ->
                                networkService.emitToggleOverlay(overlayId, display = true, type = "picture")
                            }

                            // Toggle on website overlays
                            yearDetail.overlays.websites.forEach { overlayId ->
                                networkService.emitToggleOverlay(overlayId, display = true, type = "website")
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "No overlay data available for $selectedLocation ($selectedQuality) in $currentYear",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        // Step 3: Enable Measure Button if Condition is Met
                        if (measure != null && years[currentYearIndex] >= measure.startYear && !isMeasureActivated) {
                            isMeasureButtonEnabled = true
                        }

                        // Progress to the next year
                        if (currentYearIndex < years.size - 1) {
                            currentYearIndex++
                        } else {
                            buttonEnabled = false // Disable the button after the last year
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }, enabled = buttonEnabled) {
                Text("Next")
            }

            // Measure Button
            Button(
                onClick = {
                    measure?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            networkService.emitToggleOverlay(it.id, display = true, type = "measure")
                            withContext(Dispatchers.Main) {
                                // Update the current temperature (reduce by tempChange)
                                currentTemperature -= it.tempChange
                                networkService.postTemperature(currentTemperature) // Post updated temperature

                                isMeasureButtonEnabled = false // Disable the button after clicking
                                isMeasureActivated = true // Permanently disable after activation
                                excludedOverlayIds.add(it.id) // Add measure ID to excluded overlays
                                Toast.makeText(context, "Measure ${it.name} toggled ON", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = isMeasureButtonEnabled
            ) {
                Text(measure?.name ?: "No Measure")
            }
        }

        if (!buttonEnabled) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Determine the video ID based on the selected location
                        val videoId = if (selectedLocation == "Aasee") 1787 else 1766

                        // Step 1: Toggle off all overlays
                        toggleAllOverlays(context, networkService, videoId)
                        delay(1000)

                        // Step 2: Toggle on overlay 1791
                        networkService.emitToggleOverlay(1791, display = true, type = "picture")

                        // Navigate back to the third screen
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Navigating back to the third screen", Toast.LENGTH_SHORT).show()
                            onBackToThird()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text("Back to Third Page")
            }
        }
    }
}

