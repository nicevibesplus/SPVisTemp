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

@SuppressLint("UnrememberedMutableState")


@Composable
fun MainScreen(
    networkService: NetworkService,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current

    // Use a MutableList wrapped in mutableStateOf to make it reactive
    val buttonStates = remember { mutableStateOf(listOf(true, false, false, false)) }

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

        Button(
            onClick = {
                toggleAllOverlays(context, networkService, videoId = 1787)
                Log.d("MainScreen", "Overlays toggled, enabling fourth button")
                // Enable the fourth button
                buttonStates.value = buttonStates.value.toMutableList().apply { set(3, true) }
            },
            enabled = buttonStates.value[2]
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
            enabled = buttonStates.value[3]
        ) {
            Text("Go to Info Screen")
        }
    }
}

