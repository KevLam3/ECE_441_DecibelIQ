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

data class ShiftLog(
    val id: String = "",
    val date: String = "",
    val start: String = "",
    val end: String = "",
    val duration: String = "",
    val maxDose: String = ""
)

@Composable
fun PreviousLogScreen(
    navController: NavController
) {
    var logs by remember { mutableStateOf(listOf<ShiftLog>()) }

    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("shift_logs")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ShiftLog>()
                for (child in snapshot.children) {
                    val log = child.getValue(ShiftLog::class.java)
                    if (log != null) {
                        list.add(log.copy(id = child.key ?: ""))
                    }
                }
                logs = list.sortedByDescending { it.id }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Previous Logs", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(logs) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate("shift_detail/${log.id}")
                        },
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Date: ${log.date}", style = MaterialTheme.typography.bodyLarge)
                        Text("Start: ${log.start}", style = MaterialTheme.typography.bodyLarge)
                        Text("End: ${log.end}", style = MaterialTheme.typography.bodyLarge)
                        Text("Duration: ${log.duration}", style = MaterialTheme.typography.bodyLarge)
                        Text("Max Dose: ${log.maxDose}%", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}