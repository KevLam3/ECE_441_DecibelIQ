package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoNoiseLevelsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Examples of Noise Levels", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        NoiseExample("Rocket Launch", "180 dB", "0 seconds")
        NoiseExample("Gunshot / Explosion", "140 dB", "Instant hearing loss risk")
        NoiseExample("Fireworks / Siren", "120 dB", "15 sec – 15 min")
        NoiseExample("Power Tools", "100 dB", "1–2 hours")
        NoiseExample("Car Traffic", "80 dB", "8 hours")
        NoiseExample("Conversation", "60 dB", "Safe")
        NoiseExample("Whisper", "20 dB", "Safe")
    }
}

@Composable
fun NoiseExample(source: String, db: String, exposure: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(source, style = MaterialTheme.typography.titleSmall)
            Text("Level: $db")
            Text("Max exposure: $exposure")
        }
    }
}
