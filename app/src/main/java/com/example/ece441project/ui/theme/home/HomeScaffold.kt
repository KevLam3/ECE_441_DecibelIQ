package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.BleViewModel

@Composable
fun HomeScaffold() {

    val bleViewModel: BleViewModel = viewModel()

    val spl = bleViewModel.spl.collectAsState()
    val laeq = bleViewModel.laeq.collectAsState()
    val dose = bleViewModel.dose.collectAsState()
    val led = bleViewModel.led.collectAsState()
    val blink = bleViewModel.blink.collectAsState()
    val time24 = bleViewModel.time24.collectAsState()
    val safe = bleViewModel.safe.collectAsState()

    val homeNavController = rememberNavController()

    val items = listOf("daily_log", "for_you", "settings")

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
                composable("daily_log") {
                    DailyLogScreen(
                        spl = spl.value,
                        laeq = laeq.value,
                        dose = dose.value,
                        led = led.value,
                        blink = blink.value,
                        time24 = time24.value,
                        safe = safe.value
                    )
                }
                composable("for_you") { ForYouScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}