package com.example.remote_control.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remote_control.network.NetworkService
import com.example.remote_control.components.toggleAllOverlays
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainScreen(
    networkService: NetworkService,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current

    // Use a MutableList wrapped in mutableStateOf to make it reactive
    val buttonStates = remember { mutableStateOf(listOf(true, false, false, false, false)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                networkService.login("admin", "pass") { success, _ ->
                    if (success) {
                        networkService.connectSocket()
                        Log.d("MainScreen", "Login successful, enabling second button")
                        // Enable the second button
                        buttonStates.value = buttonStates.value.toMutableList().apply { set(1, true) }
                    } else {
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            enabled = buttonStates.value[0]
        ) {
            Text("Login and Connect")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                networkService.setScenario(1767, "Studienprojekt 24/25")
                networkService.setLocation(1768, "outdoor", "Aasee")
                Toast.makeText(context, "Scenario and Location Set", Toast.LENGTH_SHORT).show()
                Log.d("MainScreen", "Scenario and Location set, enabling third button")
                // Enable the third button
                buttonStates.value = buttonStates.value.toMutableList().apply { set(2, true) }
            },
            enabled = buttonStates.value[1]
        ) {
            Text("Setup Scenario and Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val coroutineScope = rememberCoroutineScope()

        // New Button
        Button(
            onClick = {
                coroutineScope.launch {
                    // Emit toggle overlays
                    networkService.emitToggleOverlay(1784, display = true, type = "website")
                    networkService.emitToggleOverlay(1775, display = true, type = "website")
                    networkService.emitToggleOverlay(1776, display = true, type = "website")
                    networkService.emitToggleOverlay(1806, display = true, type = "website")


                    delay(1000) // Delay for 1 second

                    // Reset location to Aasee
                    networkService.setLocation(1768, "outdoor", "Aasee")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Location Reset to Aasee", Toast.LENGTH_SHORT).show()
                        Log.d("MainScreen", "Location reset, enabling toggle overlays button")

                        // Enable the fourth button
                        buttonStates.value = buttonStates.value.toMutableList().apply { set(3, true) }
                    }
                }
            },
            enabled = buttonStates.value[2]
        ) {
            Text("Reset Location to Aasee")
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                toggleAllOverlays(context, networkService, videoId = 1787)
                Log.d("MainScreen", "Overlays toggled, enabling fifth button")
                // Enable the fifth button
                buttonStates.value = buttonStates.value.toMutableList().apply { set(4, true) }
            },
            enabled = buttonStates.value[3]
        ) {
            Text("Toggle All Overlays")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                networkService.emitToggleOverlay(1792, display = true, type = "picture")
                onNavigate()
                Log.d("MainScreen", "Navigating to Info Screen")
            },
            enabled = buttonStates.value[4]
        ) {
            Text("Go to Info Screen")
        }
    }
}
