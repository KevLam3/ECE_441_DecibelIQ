package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import kotlin.math.abs

@Composable
fun ShiftDetailScreen(
    logId: String
) {
    var doseHistory by remember { mutableStateOf(listOf<Pair<Long, Float>>()) }
    var date by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var maxSpl by remember { mutableStateOf(0f) }
    var currentDose by remember { mutableStateOf(0f) }

    LaunchedEffect(logId) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("shift_logs")
            .child(logId)

        ref.get().addOnSuccessListener { snap ->
            date = snap.child("date").value?.toString() ?: ""
            start = snap.child("start").value?.toString() ?: ""
            end = snap.child("end").value?.toString() ?: ""
            duration = snap.child("duration").value?.toString() ?: ""

            // Load max SPL
            maxSpl = snap.child("maxSpl").value?.toString()?.toFloatOrNull() ?: 0f

            // Load dose history
            val list = mutableListOf<Pair<Long, Float>>()
            val hist = snap.child("doseHistory")
            for (child in hist.children) {
                val ts = child.key?.toLongOrNull() ?: continue
                val v = child.getValue(Float::class.java) ?: continue
                list.add(ts to v)
            }

            doseHistory = list.sortedBy { it.first }

            // Current dose = last entry
            currentDose = doseHistory.lastOrNull()?.second ?: 0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Shift Details", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        Text("Date: $date")
        Text("Start: $start")
        Text("End: $end")
        Text("Duration: $duration")

        Spacer(Modifier.height(12.dp))

        Text("Current Dose: ${"%.2f".format(currentDose)} %")
        Text("Max SPL: ${"%.2f".format(maxSpl)} dBA")

        Spacer(Modifier.height(24.dp))

        Text("Dose Over Time", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        if (doseHistory.isNotEmpty()) {
            DoseGraphWithTime(doseHistory)
        } else {
            Text("No dose data recorded")
        }
    }
}

@Composable
fun DoseGraphWithTime(points: List<Pair<Long, Float>>) {

    if (points.size < 2) {
        Text("Not enough data")
        return
    }

    val minTs = points.first().first
    val maxTs = points.last().first
    val totalMs = (maxTs - minTs).coerceAtLeast(1)
    val totalSec = totalMs / 1000f

    val maxVal = points.maxOf { it.second }.coerceAtLeast(1f)

    // Touch state
    var touchX by remember { mutableStateOf<Float?>(null) }
    var selectedPoint by remember { mutableStateOf<Pair<Long, Float>?>(null) }

    Column {

        // GRAPH
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        touchX = offset.x

                        val width = size.width
                        val ratio = (touchX!! / width).coerceIn(0f, 1f)
                        val ts = (minTs + ratio * totalMs).toLong()

                        selectedPoint = points.minByOrNull { abs(it.first - ts) }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            var prevX = 0f
            var prevY = height - (points.first().second / maxVal) * height

            for (i in 1 until points.size) {
                val (ts, value) = points[i]

                val x = ((ts - minTs).toFloat() / totalMs) * width
                val y = height - (value / maxVal) * height

                drawLine(
                    color = Color(0xFF4CAF50),
                    start = Offset(prevX, prevY),
                    end = Offset(x, y),
                    strokeWidth = 4f
                )

                prevX = x
                prevY = y
            }

            // Touch cursor
            touchX?.let { x ->
                drawLine(
                    color = Color.Red,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 2f
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // TIME MARKERS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0:00", style = MaterialTheme.typography.bodySmall)

            val midSec = (totalSec / 2).toInt()
            Text("${midSec / 60}:${(midSec % 60).toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodySmall)

            val endSec = totalSec.toInt()
            Text("${endSec / 60}:${(endSec % 60).toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(12.dp))

        // TOUCH INFO BOX
        selectedPoint?.let { (ts, value) ->
            val sec = ((ts - minTs) / 1000).toInt()
            val mm = sec / 60
            val ss = sec % 60

            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Time: $mm:${ss.toString().padStart(2, '0')}")
                    Text("Dose: ${"%.2f".format(value)} %")
                }
            }
        }
    }
}