package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ece441project.BleViewModel

@Composable
fun ForYouScreen(
    viewModel: BleViewModel = viewModel()
) {
    val safe = viewModel.safe.value
    val spl = viewModel.spl.value
    val laeq = viewModel.laeq.value
    val led = viewModel.led.value
    val blink = viewModel.blink.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("For You", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Safe Hours Left") },
                supportingContent = { Text("$safe h") },
                leadingContent = { Icon(Icons.Default.AccessTime, null) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Current SPL") },
                supportingContent = { Text("$spl dB") },
                leadingContent = { Icon(Icons.Default.GraphicEq, null) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("LAeq") },
                supportingContent = { Text("$laeq dB") },
                leadingContent = { Icon(Icons.Default.Info, null) }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("LED Status") },
                supportingContent = { Text("Color: $led, Blink: $blink") },
                leadingContent = { Icon(Icons.Default.Lightbulb, null) }
            )
        }
    }
}