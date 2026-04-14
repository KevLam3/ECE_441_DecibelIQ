package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScaffold() {
    val homeNavController = rememberNavController()

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
                        selected = homeNavController.currentDestination?.route == route,
                        onClick = {
                            homeNavController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(route.replace("_", " ").uppercase()) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = homeNavController,
                startDestination = "daily_log"
            ) {
                composable("daily_log") { DailyLogScreen() }
                composable("for_you") { ForYouScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}