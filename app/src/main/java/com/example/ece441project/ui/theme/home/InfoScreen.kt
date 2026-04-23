package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun InfoScreen(
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
        Text("Info", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        SectionItem("Why Noise Monitoring Matters") {
            navController.navigate("info_why_noise")
        }

        SectionItem("Sound Definitions / Information") {
            navController.navigate("info_sound_definitions")
        }

        SectionItem("Examples of Noise Levels") {
            navController.navigate("info_noise_levels")
        }

        SectionItem("Indicator Information") {
            navController.navigate("info_indicator_info")
        }
    }
}