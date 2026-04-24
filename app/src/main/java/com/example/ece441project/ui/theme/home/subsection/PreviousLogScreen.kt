package com.example.ece441project.ui.theme.home.subsection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*

@Composable
fun PreviousLogScreen(navController: NavController) {

    var logs by remember { mutableStateOf<List<ShiftLog>>(emptyList()) }

    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("shift_logs")
        ref.get().addOnSuccessListener { snap ->
            val list = mutableListOf<ShiftLog>()
            snap.children.forEach { child ->
                val id = child.key ?: return@forEach
                val date = child.child("date").getValue(String::class.java) ?: "—"
                val start = child.child("start").getValue(String::class.java) ?: "—"
                val end = child.child("end").getValue(String::class.java) ?: "—"
                val duration = child.child("duration").getValue(String::class.java) ?: "—"
                val maxSpl = child.child("maxSpl").getValue(String::class.java)?.toFloatOrNull() ?: 0f

                // Get last dose from doseHistory
                val doseHistory = child.child("doseHistory")
                val lastDose = doseHistory.children.lastOrNull()?.getValue(Float::class.java) ?: 0f

                list.add(
                    ShiftLog(
                        id = id,
                        date = date,
                        start = start,
                        end = end,
                        duration = duration,
                        maxSpl = maxSpl,
                        lastDose = lastDose
                    )
                )
            }
            logs = list.sortedByDescending { it.id }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(logs) { log ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { navController.navigate("shift_detail/${log.id}") },
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Date: ${log.date}")
                    Text("Start: ${log.start}")
                    Text("End: ${log.end}")
                    Text("Duration: ${log.duration}")
                    Text("Current Dose: ${"%.2f".format(log.lastDose)} %")
                    Text("Max SPL: ${"%.2f".format(log.maxSpl)} dBA")
                }
            }
        }
    }
}

data class ShiftLog(
    val id: String,
    val date: String,
    val start: String,
    val end: String,
    val duration: String,
    val maxSpl: Float,
    val lastDose: Float
)