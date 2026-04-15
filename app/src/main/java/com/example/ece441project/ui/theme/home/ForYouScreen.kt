package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ForYouScreen(
    navController: NavController,
    safe: Float,
    spl: Float,
    laeq: Float,
    led: String,
    blink: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("For You", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        SectionItem("Safe hours left") { navController.navigate("safe_hours") }
        SectionItem("Battery life") { navController.navigate("battery_life") }
        SectionItem("Power on/off device") { navController.navigate("power_device") }

        Spacer(Modifier.height(24.dp))
    }
}
