package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ece441project.viewmodel.ThemeViewModel

@Composable
fun CustomizationScreen(themeViewModel: ThemeViewModel) {

    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val fontSize by themeViewModel.fontSize.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Customization", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // -------------------------
        // DARK MODE TOGGLE
        // -------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Dark Mode") },
                supportingContent = { Text("Toggle between light and dark theme") },
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { themeViewModel.toggleDarkMode(it) }
                    )
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // -------------------------
        // FONT SIZE SLIDER
        // -------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Font Size") },
                supportingContent = {
                    Column {
                        Slider(
                            value = fontSize.toFloat(),
                            onValueChange = { themeViewModel.updateFontSize(it.toInt()) },
                            valueRange = 0f..2f,
                            steps = 1
                        )

                        val label = when (fontSize) {
                            0 -> "Small"
                            1 -> "Medium"
                            else -> "Large"
                        }

                        Text("Current: $label")
                    }
                }
            )
        }

        //Button Color
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Button Color") },
                supportingContent = {
                    Row {
                        val colors = listOf(
                            0xFF6750A4, // purple
                            0xFF386A20, // green
                            0xFFB3261E, // red
                            0xFF005AC1  // blue
                        )

                        colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp)
                                    .background(Color(c.toInt()), shape = CircleShape)
                                    .clickable { themeViewModel.updateButtonColor(c.toInt()) }
                            )
                        }
                    }
                }
            )
        }
    }
}