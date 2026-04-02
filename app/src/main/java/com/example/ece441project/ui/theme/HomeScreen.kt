@file:Suppress("DEPRECATION")

package com.example.ece441project.ui.theme

// -----------------------------
// Jetpack Compose
// -----------------------------
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// -----------------------------
// Android + Bluetooth
// -----------------------------
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel

// -----------------------------
// App Code
// -----------------------------
import com.example.ece441project.BleViewModel
import com.example.ece441project.Sample

// -----------------------------
// Firebase
// -----------------------------
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

// ---------------------------------------------------------
// Permission Helper
// ---------------------------------------------------------
private fun hasBlePermissions(context: android.content.Context): Boolean {
    val required = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    return required.all { perm ->
        ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
    }
}

// ---------------------------------------------------------
// Waveform Graph
// ---------------------------------------------------------
@Composable
fun WaveformGraph(
    data: List<Sample>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val minVal = data.minOf { it.value }.toFloat()
        val maxVal = data.maxOf { it.value }.toFloat()
        val valRange = (maxVal - minVal).takeIf { it > 0f } ?: 1f

        val xStep = size.width / (data.size - 1)

        for (i in 0 until data.size - 1) {
            val x1 = i * xStep
            val x2 = (i + 1) * xStep

            val y1 = size.height - ((data[i].value - minVal) / valRange) * size.height
            val y2 = size.height - ((data[i + 1].value - minVal) / valRange) * size.height

            drawLine(
                color = Color.Red,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 3f
            )
        }
    }
}

// ---------------------------------------------------------
// HomeScreen UI + Permission-Safe BLE Startup
// ---------------------------------------------------------
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val firebaseRef = Firebase.database.getReference("mic/raw")

    val vm: BleViewModel = viewModel()
    val maxPoints = 300

    // -----------------------------
    // Permission Launcher
    // -----------------------------
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grantedMap ->
        val allGranted = grantedMap.values.all { it }
        if (allGranted) {
            vm.startScan(context, firebaseRef, maxPoints)
        }
    }

    // -----------------------------
    // Request permissions on first load
    // -----------------------------
    LaunchedEffect(Unit) {
        if (!hasBlePermissions(context)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            vm.startScan(context, firebaseRef, maxPoints)
        }
    }

    // -----------------------------
    // UI
    // -----------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Mic Raw Value:", fontSize = 22.sp)
        Text(vm.rawValue.value, fontSize = 60.sp)

        Spacer(modifier = Modifier.height(20.dp))

        WaveformGraph(
            data = vm.waveform,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}