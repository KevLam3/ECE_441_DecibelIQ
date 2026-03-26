package com.example.ece441project.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    val people = listOf("Kevin", "Adam", "Bhavik", "Darion")

    Column {
        LazyColumn {
            items(people) { name ->
                ListItem(name = name)
            }
        }
    }
}

@Composable
fun ListItem(name: String) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = name,
            modifier = Modifier.padding(24.dp)
        )
    }
}

