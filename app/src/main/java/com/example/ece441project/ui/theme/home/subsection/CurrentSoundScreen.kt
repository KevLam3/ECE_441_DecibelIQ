package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CurrentSoundScreen(
    spl: Float,
    laeq: Float
) {
    ScreenTemplate("Current Sound\nSPL: $spl dB\nLAeq: $laeq dB")
}
