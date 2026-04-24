package com.example.ece441project.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BatteryIndicator(
    percent: Int,
    usbPower: Boolean
) {
    Row(
        modifier = Modifier.padding(end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (usbPower) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
            contentDescription = "Battery",
            tint = MaterialTheme.colorScheme.primary
        )
        Text("$percent%", style = MaterialTheme.typography.bodyMedium)
    }
}
