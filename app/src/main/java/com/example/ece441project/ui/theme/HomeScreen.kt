package com.example.ece441project.ui.theme

// ---------------------------------------------------------
// Jetpack Compose
// ---------------------------------------------------------
import android.Manifest
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ---------------------------------------------------------
// Graphics (Canvas for LAeq graph)
// ---------------------------------------------------------
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

// ---------------------------------------------------------
// BLE ViewModel
// ---------------------------------------------------------
import com.example.ece441project.BleViewModel

// ---------------------------------------------------------
// Android Runtime Permissions
// ---------------------------------------------------------
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// ---------------------------------------------------------
// LAeq Graph Composable
// ---------------------------------------------------------
@Composable
fun LAeqGraph(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {

        if (data.size < 2) return@Canvas

        val minVal = data.minOrNull() ?: 0f
        val maxVal = data.maxOrNull() ?: minVal + 1f
        val range = (maxVal - minVal).takeIf { it > 0f } ?: 1f

        val xStep = size.width / (data.size - 1)

        for (i in 0 until data.size - 1) {
            val x1 = i * xStep
            val x2 = (i + 1) * xStep

            val y1 = size.height - ((data[i] - minVal) / range) * size.height
            val y2 = size.height - ((data[i + 1] - minVal) / range) * size.height

            drawLine(
                color = Color(0xFF00AEEF),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 4f
            )
        }
    }
}

// ---------------------------------------------------------
// HomeScreen UI
// ---------------------------------------------------------
@Composable
fun HomeScreen() {

    // -----------------------------
    // Context + ViewModel
    // -----------------------------
    val context = LocalContext.current
    val vm: BleViewModel = viewModel()
    val maxPoints = 300

    // -----------------------------
    // Runtime Permissions
    // -----------------------------
    val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var permissionsGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        permissionsGranted = results.values.all { it }
        if (permissionsGranted) vm.startScan(context)
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }

    // -----------------------------
    // LAeq History Buffer
    // -----------------------------
    val laeqHistory = remember { mutableStateListOf<Float>() }

    LaunchedEffect(vm.laeq.value) {
        val v = vm.laeq.value.toFloatOrNull()
        if (v != null && v.isFinite()) {
            laeqHistory.add(v)
            if (laeqHistory.size > maxPoints) laeqHistory.removeAt(0)
        }
    }

    // -----------------------------
    // UI Layout
    // -----------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        // -----------------------------
        // LAeq Graph
        // -----------------------------
        Text("LAeq Trend", fontSize = 26.sp)
        Spacer(modifier = Modifier.height(10.dp))

        LAeqGraph(
            data = laeqHistory,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // -----------------------------
        // Telemetry Values
        // -----------------------------
        Text("Sound Exposure Summary", fontSize = 26.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Text("SPL: ${vm.spl.value} dBA", fontSize = 20.sp)
        Text("LAeq: ${vm.laeq.value} dBA", fontSize = 20.sp)
        Text("Dose: ${vm.dose.value} %", fontSize = 20.sp)
        Text("LED: ${vm.led.value}", fontSize = 20.sp)
        Text("Blink: ${vm.blink.value}", fontSize = 20.sp)
        Text("Time Left (24h): ${vm.time24.value} h", fontSize = 20.sp)
        Text("Safe Time Left: ${vm.safe.value} h", fontSize = 20.sp)
    }
}
