package com.example.ece441project.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun HomeScreen() {
    // State that will update when Firebase changes
    val rawValue = remember { mutableStateOf("Loading...") }

    // Attach Firebase listener ONCE
    LaunchedEffect(Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("mic/raw")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val value = snapshot.getValue(Long::class.java)
                    rawValue.value = value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                rawValue.value = "Error: ${error.message}"
            }
        })
    }

    // UI
    Column {
        Text(
            text = "Mic Raw Value:",
            fontSize = 22.sp
        )

        Text(
            text = rawValue.value,
            fontSize = 40.sp
        )
    }
}