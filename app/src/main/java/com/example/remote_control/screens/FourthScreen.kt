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



import android.util.Log

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
    val measures = DataProvider.locationSpecificMeasures[selectedLocation] ?: emptyList()

    // Track excluded overlay IDs
    val excludedOverlayIds = remember { mutableStateListOf(1784, 1775, 1776) }

    // Track active info overlays
    val activeInfoOverlays = remember { mutableStateListOf<Int>() }

    // Track activation states for measures
    val measureActivationStates = remember {
        mutableStateMapOf<Int, Boolean>().apply {
            measures.forEach { measure ->
                this[measure.id] = false // Initialize all measures as not activated
            }
        }
    }

    // Current year
    val currentYear = years.getOrElse(currentYearIndex) { 0 }

    // Function to handle temperature-based overlays
    fun manageWarningOverlays() {
        CoroutineScope(Dispatchers.IO).launch {
            // Define the relevant overlays
            val relevantOverlays = setOf(1774, 1777, 1785)

            // Determine which overlays should be active based on the temperature
            val overlaysToToggle = mutableSetOf<Int>()
            if (currentTemperature > 28) {
                overlaysToToggle.addAll(relevantOverlays)
            } else if (currentTemperature > 27) {
                overlaysToToggle.addAll(listOf(1774, 1777))
            } else if (currentTemperature > 26) {
                overlaysToToggle.add(1774)
            }

            // Isolate changes to relevant overlays
            val currentRelevantExclusions = excludedOverlayIds.filter { it in relevantOverlays }.toSet()
            val overlaysToTurnOn = overlaysToToggle - currentRelevantExclusions
            val overlaysToTurnOff = currentRelevantExclusions - overlaysToToggle

            // Toggle relevant overlays on
            overlaysToTurnOn.forEach { overlayId ->
                networkService.emitToggleOverlay(overlayId, display = true, type = "overlay")
                excludedOverlayIds.add(overlayId) // Add to exclusions
            }

            // Toggle relevant overlays off
            overlaysToTurnOff.forEach { overlayId ->
                networkService.emitToggleOverlay(overlayId, display = false, type = "overlay")
                excludedOverlayIds.remove(overlayId) // Remove from exclusions
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Temperature Display and Measure Buttons
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Current Temperature
            Text(
                "Current Temperature: ${"%.1f".format(currentTemperature)} °C",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Create a button for each measure
            measures.forEach { measure ->
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            networkService.emitToggleOverlay(measure.id, display = true, type = "measure")
                            networkService.emitToggleOverlay(measure.info, display = true, type = "info") // Toggle on info overlay
                            activeInfoOverlays.add(measure.info) // Track the active info overlay

                            withContext(Dispatchers.Main) {
                                // Update the current temperature (reduce by tempChange)
                                currentTemperature -= measure.tempChange
                                networkService.postTemperature(currentTemperature) // Post updated temperature

                                measureActivationStates[measure.id] = true // Mark this measure as activated
                                excludedOverlayIds.add(measure.id) // Add measure ID to excluded overlays
                                manageWarningOverlays() // Update temperature-based overlays
                                Toast.makeText(context, "Measure ${measure.name} toggled ON", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !measureActivationStates[measure.id]!! && currentYear - 20 >= measure.startYear // Respect startYear
                ) {
                    Text(measure.name)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Bottom Section: Next and Back Buttons
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (buttonEnabled) {
                // "Next" Button
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // Step 1: Toggle off all overlays except excluded IDs
                            val videoId = if (selectedLocation == "Aasee") 1787 else 1766

                            // Toggle off active info overlays
                            activeInfoOverlays.forEach { infoId ->
                                networkService.emitToggleOverlay(infoId, display = false, type = "info")
                            }
                            activeInfoOverlays.clear() // Clear the list after toggling off

                            toggleAllOverlays(
                                context,
                                networkService,
                                videoId,
                                excludedOverlayIds = excludedOverlayIds.toSet()
                            )
                            delay(1000)

                            // Step 2: Fetch and toggle on overlays for the current year
                            val yearDetail = DataProvider.locationData[selectedLocation]?.get(selectedQuality)?.temperatures?.get(currentYear)

                            if (yearDetail != null) {
                                // Update the current temperature
                                currentTemperature += yearDetail.temp // Add the tempChange
                                networkService.postTemperature(currentTemperature) // Post updated temperature

                                // Manage temperature-based overlays
                                manageWarningOverlays()

                                // Post calendar API
                                val frequency = calculateFrequency(currentTemperature)
                                networkService.postCalendar(currentYear - 20, frequency)

                                // Post infotext API
                                networkService.postInfoText(yearDetail.text)

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
            } else {
                // "Back to Third Page" Button
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
}


fun calculateFrequency(temperature: Double): Int {
    return ((temperature * 10 / 6) - 34).toInt()
}