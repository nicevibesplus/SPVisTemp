package com.example.remote_control.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.example.remote_control.data.DataProvider
import com.example.remote_control.network.NetworkService
import com.example.remote_control.components.SwitchWithLabel
import com.example.remote_control.components.toggleAllOverlays
import kotlinx.coroutines.*

@Composable
fun ThirdScreen(
    networkService: NetworkService,
    onNavigate: (String, String) -> Unit
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf("Aasee") }
    var selectedQuality by remember { mutableStateOf("low") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Location:")
        Row {
            SwitchWithLabel("Aasee", selectedLocation == "Aasee") {
                if (it) selectedLocation = "Aasee"
            }
            Spacer(modifier = Modifier.width(16.dp))
            SwitchWithLabel("Prinzipalmarkt", selectedLocation == "Prinzipalmarkt") {
                if (it) selectedLocation = "Prinzipalmarkt"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Select Quality:")
        Row {
            SwitchWithLabel("Low", selectedQuality == "low") {
                if (it) selectedQuality = "low"
            }
            Spacer(modifier = Modifier.width(16.dp))
            SwitchWithLabel("High", selectedQuality == "high") {
                if (it) selectedQuality = "high"
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Determine the video ID based on the selected location
                        val videoId = if (selectedLocation == "Aasee") 1787 else 1766

                        // Step 1: Select the location via API call
                        val locationId = if (selectedLocation == "Aasee") 1768 else 1790
                        networkService.emitToggleOverlay(1784, display = true, type = "website")
                        networkService.setLocation(locationId, "outdoor", selectedLocation)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Location set: $selectedLocation", Toast.LENGTH_SHORT).show()
                        }
                        delay(3000)

                        // Step 2: Toggle off all overlays
                        toggleAllOverlays(context, networkService, videoId)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "All overlays toggled off", Toast.LENGTH_SHORT).show()
                        }
                        delay(1000)

                        // Step 3: Fetch data for 2020 and toggle on overlays
                        val yearDetail = DataProvider.locationData[selectedLocation]?.get(selectedQuality)?.temperatures?.get(2020)

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

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Overlays toggled on for $selectedLocation ($selectedQuality, 2020)", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "No data available for $selectedLocation ($selectedQuality) in 2020", Toast.LENGTH_SHORT).show()
                            }
                        }


                        // Navigate to the next screen
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            onNavigate(selectedLocation, selectedQuality)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Start")
            }
        }
    }
}
