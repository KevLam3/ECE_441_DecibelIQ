package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SettingsScreen(
    auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("Account") },
                supportingContent = { Text(auth.currentUser?.email ?: "Not signed in") },
                leadingContent = { Icon(Icons.Default.Person, null) },
                trailingContent = {
                    TextButton(onClick = { auth.signOut() }) {
                        Text("Sign out")
                    }
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            ListItem(
                headlineContent = { Text("LED Colors") },
                supportingContent = { Text("Explains what each LED color means") },
                leadingContent = { Icon(Icons.Default.Palette, null) }
            )
        }
    }
}
