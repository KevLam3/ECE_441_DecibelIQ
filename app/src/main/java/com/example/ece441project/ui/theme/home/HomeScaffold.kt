package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun HomeScaffold(navController: NavHostController) {
    val items = listOf(
        "daily_log",
        "for_you",
        "settings"
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { route ->
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == route,
                        onClick = { navController.navigate(route) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(route.replace("_", " ").uppercase()) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) { }
    }
}

