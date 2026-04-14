package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.BleViewModel
import com.example.ece441project.ui.theme.home.subsection.*

@Composable
fun HomeScaffold(
    bleViewModel: BleViewModel
) {
    val homeNavController = rememberNavController()

    // Collect BLE values
    val spl = bleViewModel.spl.collectAsState().value
    val laeq = bleViewModel.laeq.collectAsState().value
    val dose = bleViewModel.dose.collectAsState().value
    val led = bleViewModel.led.collectAsState().value
    val blink = bleViewModel.blink.collectAsState().value
    val time24 = bleViewModel.time24.collectAsState().value
    val safe = bleViewModel.safe.collectAsState().value

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "daily_log",
                    onClick = {
                        homeNavController.navigate("daily_log") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Daily Log") }
                )

                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "for_you",
                    onClick = {
                        homeNavController.navigate("for_you") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text("For You") }
                )

                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "settings",
                    onClick = {
                        homeNavController.navigate("settings") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = "daily_log",
            modifier = Modifier.padding(innerPadding)
        ) {

            // DAILY LOG
            composable("daily_log") {
                DailyLogScreen(
                    navController = homeNavController,
                    spl = spl,
                    laeq = laeq,
                    dose = dose,
                    led = led,
                    blink = blink,
                    time24 = time24,
                    safe = safe
                )
            }

            // FOR YOU
            composable("for_you") {
                ForYouScreen(
                    navController = homeNavController,
                    safe = safe,
                    spl = spl,
                    laeq = laeq,
                    led = led,
                    blink = blink
                )
            }

            // SETTINGS
            composable("settings") {
                SettingsScreen(
                    navController = homeNavController,
                    bleViewModel = bleViewModel
                )
            }

            // SETTINGS SUBSECTIONS
            composable("customization") { CustomizationScreen() }
            composable("account_management") { AccountManagementScreen() }
            composable("color_indication") { ColorIndicationScreen() }
            composable("restart_device") { RestartDeviceScreen(bleViewModel = bleViewModel) }

            // DAILY LOG SUBROUTES
            composable("shift_started") { ScreenTemplate("Shift Started") }
            composable("shift_ended") { ScreenTemplate("Shift Ended") }
            composable("current_levels") { ScreenTemplate("Current Sound Levels") }

            // FOR YOU SUBROUTES
            composable("safe_hours") { ScreenTemplate("Safe Hours Left") }
            composable("current_sound") { ScreenTemplate("Current Sound") }
            composable("battery_life") { ScreenTemplate("Battery Life") }
            composable("power_device") { ScreenTemplate("Power Device") }
        }
    }
}