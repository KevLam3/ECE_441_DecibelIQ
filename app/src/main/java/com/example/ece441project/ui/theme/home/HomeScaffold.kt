package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.BleViewModel
import com.example.ece441project.viewmodel.ThemeViewModel
import com.example.ece441project.ui.theme.home.subsection.*

@Composable
fun HomeScaffold(
    bleViewModel: BleViewModel,
    themeViewModel: ThemeViewModel
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
                // INFO TAB (replaces "For You" and appears first)
                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "info",
                    onClick = {
                        homeNavController.navigate("info") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("Info") }
                )

                // DAILY LOG TAB (second)
                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "daily_log",
                    onClick = {
                        homeNavController.navigate("daily_log") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Daily Log") }
                )

                // SETTINGS TAB (third)
                NavigationBarItem(
                    selected = homeNavController.currentDestination?.route == "settings",
                    onClick = {
                        homeNavController.navigate("settings") {
                            popUpTo(homeNavController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
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

            // CURRENT LEVELS
            composable("current_levels") {
                CurrentLevelsScreen(
                    spl = spl,
                    laeq = laeq,
                    dose = dose,
                    led = led,
                    blink = blink,
                    time24 = time24,
                    safe = safe
                )
            }

            // INFO (replaces FOR YOU)
            composable("info") {
                InfoScreen(
                    navController = homeNavController,
                    safe = safe,
                    spl = spl,
                    laeq = laeq,
                    led = led,
                    blink = blink
                )
            }

            // SETTINGS (flattened; Customization merged here)
            composable("settings") {
                SettingsScreen(
                    navController = homeNavController,
                    bleViewModel = bleViewModel,
                    themeViewModel = themeViewModel
                )
            }

            // DAILY LOG SUBROUTES
            composable("shift_started") { ScreenTemplate("Shift Started") }
            composable("shift_ended") { ScreenTemplate("Shift Ended") }

            // INFO SUBROUTES (formerly FOR YOU subroutes)
            composable("safe_hours") { ScreenTemplate("Safe Hours Left") }
            composable("battery_life") { ScreenTemplate("Battery Life") }
            composable("power_device") { ScreenTemplate("Power Device") }
        }
    }
}
