package com.example.remote_control.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remote_control.network.NetworkService
import com.example.remote_control.components.toggleAllOverlays

@Composable
fun MainScreen(
    networkService: NetworkService,
    onNavigate: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            networkService.login("admin", "pass") { success, _ ->
                if (success) {
                    networkService.connectSocket()
                } else {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Login and Connect")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            networkService.setScenario(1767, "Studienprojekt 24/25")
            networkService.setLocation(1768, "outdoor", "Aasee")
            Toast.makeText(context, "Scenario and Location Set", Toast.LENGTH_SHORT).show()
        }) {
            Text("Setup Scenario and Location")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            toggleAllOverlays(context, networkService, videoId = 1787)
        }) {
            Text("Toggle All Overlays")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            networkService.emitToggleOverlay(1792, display = true, type = "picture")
            onNavigate()
        }) {
            Text("Go to info screen")
        }
    }
}