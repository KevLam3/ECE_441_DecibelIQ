package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ece441project.ui.theme.home.DataCard

@Composable
fun CurrentLevelsScreen(
    spl: Float,
    laeq: Float,
    dose: Float,
    led: String,
    blink: Boolean,
    time24: Float,
    safe: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Current Sound Levels", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        DataCard("Current SPL", "$spl dB", Icons.Default.GraphicEq)
        Spacer(Modifier.height(12.dp))

        DataCard("LAeq", "$laeq dB", Icons.Default.Equalizer)
        Spacer(Modifier.height(12.dp))

        DataCard("Dose", "$dose %", Icons.Default.Percent)
        Spacer(Modifier.height(12.dp))

        DataCard("LED Color", led, Icons.Default.Lightbulb)
        Spacer(Modifier.height(12.dp))

        DataCard(
            "Blink",
            if (blink) "Blinking" else "Not blinking",
            if (blink) Icons.Default.Visibility else Icons.Default.VisibilityOff
        )
        Spacer(Modifier.height(12.dp))

        DataCard("24h Time", "$time24 h", Icons.Default.AccessTime)
        Spacer(Modifier.height(12.dp))

        DataCard("Safe Hours Left", "$safe h", Icons.Default.Timer)
    }
}