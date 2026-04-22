package com.example.ece441project

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.navigation.AppNavHost
import com.example.ece441project.ui.theme.ECE441ProjectTheme
import com.example.ece441project.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request BLE permissions (Android 12+)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )

        setContent {

            // Shared ViewModels
            val bleViewModel: BleViewModel = viewModel()
            val themeViewModel: ThemeViewModel = viewModel()

            val navController = rememberNavController()

            // Observe dark mode state BEFORE showing any screen
            val isDarkMode = themeViewModel.isDarkMode.collectAsState()

            // Apply theme globally (Sign-In, Register, Home, everything)
            ECE441ProjectTheme(
                darkTheme = isDarkMode.value,
                themeViewModel = themeViewModel
            ) {
                AppNavHost(
                    navController = navController,
                    bleViewModel = bleViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}
