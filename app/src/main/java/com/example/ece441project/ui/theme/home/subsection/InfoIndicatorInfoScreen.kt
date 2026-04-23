package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoIndicatorInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Indicator Information", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        InfoCard("LED Colors (Based on Dose)",
            "Green - Safe\nYellow - Caution\nRed - High Risk"
        )

        InfoCard("LED Blinking (Based on Current Noise)",
            "Blinking: Sound > 85 dBA\nSolid: Sound ≤ 85 dBA"
        )

        InfoCard("Dose States",
            "Safe (<50%): Green, no vibration\n" +
                    "Caution (50–80%): Yellow, 1 pulse/30s\n" +
                    "High Risk (80%+): Red, 2 pulses/30s"
        )
    }
}
