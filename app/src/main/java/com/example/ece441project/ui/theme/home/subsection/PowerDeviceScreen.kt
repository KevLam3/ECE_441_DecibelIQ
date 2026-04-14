package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.ece441project.BleViewModel

@Composable
fun PowerDeviceScreen(bleViewModel: BleViewModel) {
    ScreenTemplate("Power Device\n(Connect / Disconnect / Toggle)")
}
