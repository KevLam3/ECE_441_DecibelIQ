package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ece441project.BleViewModel

@Composable
fun DailyLogScreen(
    viewModel: BleViewModel = viewModel()
) {
    val spl = viewModel.spl.value
    val laeq = viewModel.laeq.value
    val dose = viewModel.dose.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Daily Log", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

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
                headlineContent = { Text("Dose") },
                supportingContent = { Text("$dose %") },
                leadingContent = { Icon(Icons.Default.Info, null) }
            )
        }
    }
}
