package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoWhyNoiseScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Why Noise Monitoring Matters", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text(
            "According to the World Health Organization (WHO), 22 million workers are exposed to hazardous noise each year...\n\n" +
                    "Hearing damage happens gradually, often going unnoticed until it's permanent...\n\n" +
                    "By tracking your exposure over time, you can take simple steps to reduce your risk.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
