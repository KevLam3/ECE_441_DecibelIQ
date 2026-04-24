package com.example.ece441project.ui.theme.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ece441project.BleViewModel
import com.example.ece441project.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("MissingPermission")
@Composable
fun SettingsScreen(
    navController: NavController,
    bleViewModel: BleViewModel,
    themeViewModel: ThemeViewModel,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val fontSize by themeViewModel.fontSize.collectAsState()
    val buttonColorInt by themeViewModel.buttonColor.collectAsState()

    val buttonColor = Color(buttonColorInt)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // -----------------------------
        // APPEARANCE SECTION
        // -----------------------------
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        // DARK MODE TOGGLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { themeViewModel.toggleDarkMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // FONT SIZE SLIDER
        Text("Font Size", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = fontSize.toFloat(),
            onValueChange = { themeViewModel.updateFontSize(it.toInt()) },
            valueRange = 0f..2f,
            steps = 1
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BUTTON COLOR PICKER
        Text("Button Color", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        val colors = listOf(
            0xFF6750A4.toInt(), // purple
            0xFF386A20.toInt(), // green
            0xFFB3261E.toInt(), // red
            0xFF005AC1.toInt()  // blue
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            colors.forEach { c ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(Color(c), shape = CircleShape)
                        .clickable { themeViewModel.updateButtonColor(c) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // -----------------------------
        // DEVICE SECTION
        // -----------------------------
        Text(
            text = "Device",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { bleViewModel.startScan() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to ESP32")
        }

        Spacer(modifier = Modifier.weight(1f))

        // -----------------------------
        // ACCOUNT SECTION
        // -----------------------------
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            )
        ) {
            Text("Sign Out")
        }
    }
}