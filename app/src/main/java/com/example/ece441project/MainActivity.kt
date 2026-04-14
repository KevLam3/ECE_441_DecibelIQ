package com.example.ece441project

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.navigation.AppNavHost
import com.example.ece441project.ui.theme.ECE441ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request BLE permissions
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
            ECE441ProjectTheme {

                // ONE shared BleViewModel for the entire app
                val bleViewModel: BleViewModel = viewModel()

                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    bleViewModel = bleViewModel
                )
            }
        }
    }
}