package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ece441project.ui.theme.home.DataCard
import kotlinx.coroutines.launch

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
    // SPL bounds
    val minDb = 40f
    val maxDb = 120f

    // Normalize SPL into 0f .1f range
    val progress = ((spl - minDb) / (maxDb - minDb))
        .coerceIn(0f, 1f)

    // Determine bar color
    val barColor = when {
        spl >= 100f -> Color.Red
        spl >= 85f -> Color(0xFFFFA500) // Orange
        else -> Color(0xFF4CAF50) // Green
    }

    // Smooth scroll state
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Auto‑animate scroll when content changes
    LaunchedEffect(spl, laeq, dose, safe) {
        scope.launch {
            scrollState.animateScrollTo(scrollState.value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // Title
        Text(
            "Current Sound Levels",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        // PERFECTLY CENTERED BIG SPL TEXT
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$spl dB",
                style = MaterialTheme.typography.displayLarge,
                color = barColor
            )
        }

        Spacer(Modifier.height(12.dp))

        // PROGRESS BAR (40–120 dB)
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            color = barColor,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        Spacer(Modifier.height(20.dp))

        // --- Data Cards ---
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

        Spacer(Modifier.height(40.dp)) // breathing room at bottom
    }
}