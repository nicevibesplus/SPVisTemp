package com.example.remote_control


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.remote_control.screens.*
import com.example.remote_control.network.NetworkService

sealed class Screen {
    object Main : Screen()
    object Second : Screen()
    object Third : Screen()
    object Fourth : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    val networkService = remember { NetworkService() }

    // Shared state for selected location and quality
    var selectedLocation by remember { mutableStateOf("Aasee") }
    var selectedQuality by remember { mutableStateOf("low") }

    when (currentScreen) {
        Screen.Main -> MainScreen(
            networkService = networkService,
            onNavigate = { currentScreen = Screen.Second }
        )
        Screen.Second -> SecondScreen(
            networkService = networkService,
            onNavigate = { currentScreen = Screen.Fourth }
        )
        Screen.Third -> ThirdScreen(
            networkService = networkService,
            onNavigate = { location, quality ->
                // Update shared state for location and quality
                selectedLocation = location
                selectedQuality = quality
                currentScreen = Screen.Fourth
            }
        )
        Screen.Fourth -> FourthScreen(
            networkService = networkService,
            selectedLocation = selectedLocation,
            selectedQuality = selectedQuality,
            onBackToThird = { currentScreen = Screen.Third }
        )
    }
}


