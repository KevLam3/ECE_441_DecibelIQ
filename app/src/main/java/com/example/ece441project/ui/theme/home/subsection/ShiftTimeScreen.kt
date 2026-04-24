package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ece441project.BleViewModel
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ShiftTimeScreen(
    bleViewModel: BleViewModel,
    currentDose: Float
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val idFormat = remember { SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()) }

    // Live SPL from BLE
    val currentSpl = bleViewModel.spl.collectAsState().value

    var shiftStart by remember { mutableStateOf<String?>(null) }
    var shiftEnd by remember { mutableStateOf<String?>(null) }
    var maxSpl by remember { mutableStateOf(0f) }
    var shiftId by remember { mutableStateOf<String?>(null) }

    // Restore active shift if present
    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("active_shift")
        ref.get().addOnSuccessListener { snap ->
            val start = snap.child("start").getValue(String::class.java)
            val id = snap.child("id").getValue(String::class.java)
            if (start != null && id != null) {
                shiftStart = start
                shiftId = id
                bleViewModel.startShiftLogging(id)
            }
        }
    }

    // Track max SPL while shift is active
    LaunchedEffect(currentSpl, shiftStart) {
        if (shiftStart != null && currentSpl > maxSpl) {
            maxSpl = currentSpl
        }
    }

    fun startShift() {
        val now = Date()
        val startStr = timeFormat.format(now)
        val dateStr = dateFormat.format(now)
        val id = idFormat.format(now)

        shiftStart = startStr
        shiftEnd = null
        maxSpl = 0f
        shiftId = id

        val data = mapOf(
            "date" to dateStr,
            "start" to startStr
        )

        val db = FirebaseDatabase.getInstance()
        db.getReference("shift_logs").child(id).setValue(data)
        db.getReference("active_shift").child("start").setValue(startStr)
        db.getReference("active_shift").child("id").setValue(id)

        bleViewModel.startShiftLogging(id)
    }

    fun endShift() {
        val id = shiftId ?: return
        val startStr = shiftStart ?: return

        val now = Date()
        val endStr = timeFormat.format(now)

        shiftEnd = endStr

        val startDate = timeFormat.parse(startStr)
        val endDate = timeFormat.parse(endStr)
        val diff = endDate!!.time - startDate!!.time
        val hours = diff / (1000 * 60 * 60)
        val minutes = (diff / (1000 * 60)) % 60
        val duration = "${hours}h ${minutes}m"

        val db = FirebaseDatabase.getInstance()
        val logRef = db.getReference("shift_logs").child(id)

        val updates = mapOf(
            "end" to endStr,
            "duration" to duration,
            "maxSpl" to maxSpl.toString()
        )
        logRef.updateChildren(updates)

        db.getReference("active_shift").removeValue()
        bleViewModel.stopShiftLogging()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Shift Time", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { startShift() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Shift")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { endShift() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("End Shift & Save")
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {

                Text("Shift Started:", style = MaterialTheme.typography.titleMedium)
                Text(shiftStart ?: "—")

                Spacer(Modifier.height(12.dp))

                Text("Shift Ended:", style = MaterialTheme.typography.titleMedium)
                Text(shiftEnd ?: "—")

                Spacer(Modifier.height(12.dp))

                Text("Current Dose:", style = MaterialTheme.typography.titleMedium)
                Text("${"%.2f".format(currentDose)} %")

                Spacer(Modifier.height(12.dp))

                Text("Max SPL:", style = MaterialTheme.typography.titleMedium)
                Text("${"%.2f".format(maxSpl)} dBA")
            }
        }
    }
}