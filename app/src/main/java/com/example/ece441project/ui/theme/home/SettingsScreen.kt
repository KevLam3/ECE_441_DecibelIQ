package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ece441project.BleViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    navController: NavController,
    bleViewModel: BleViewModel,
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        SectionItem("Customization") { navController.navigate("customization") }
        SectionItem("Account management") { navController.navigate("account_management") }
        SectionItem("Color indication") { navController.navigate("color_indication") }
        SectionItem("Restart device") { navController.navigate("restart_device") }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate("auth") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign out")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                bleViewModel.startScan()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Connect to ESP32")
        }
    }
}