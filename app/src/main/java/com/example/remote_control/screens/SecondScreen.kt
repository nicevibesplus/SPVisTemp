package com.example.remote_control.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.remote_control.network.NetworkService

@Composable
fun SecondScreen(networkService: NetworkService, onNavigate: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            networkService.emitToggleOverlay(1792, display = false, type = "picture")
            networkService.emitToggleOverlay(1791, display = true, type = "picture")
            onNavigate()
        }) {
            Text("Toggle Overlay and Go to Third Page")
        }
    }
}
