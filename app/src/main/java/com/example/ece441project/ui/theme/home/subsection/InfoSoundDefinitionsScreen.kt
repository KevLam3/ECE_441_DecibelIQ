package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoSoundDefinitionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Sound Definitions & Information", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        InfoCard("What is SPL?", "SPL tells you how loud a sound is at a moment, measured in dB.")
        InfoCard("What is LAeq?", "LAeq is the average sound level over time.")
        InfoCard("What is Noise Dose?", "Noise dose shows how much exposure you've had compared to safe limits.")
        InfoCard("What is dB?", "Every 10 dB increase means the sound is much louder, not just a little.")
    }
}

@Composable
fun InfoCard(title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
