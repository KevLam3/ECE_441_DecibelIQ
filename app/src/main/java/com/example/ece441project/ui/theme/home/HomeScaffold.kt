package com.example.ece441project.ui.theme.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.BleViewModel
import com.example.ece441project.viewmodel.ThemeViewModel
import com.example.ece441project.ui.theme.home.subsection.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(
    bleViewModel: BleViewModel,
    themeViewModel: ThemeViewModel
) {
    val homeNavController = rememberNavController()

    // Collect BLE values once
    val spl = bleViewModel.spl.collectAsState().value
    val laeq = bleViewModel.laeq.collectAsState().value
    val dose = bleViewModel.dose.collectAsState().value
    val led = bleViewModel.led.collectAsState().value
    val blink = bleViewModel.blink.collectAsState().value
    val time24 = bleViewModel.time24.collectAsState().value
    val safe = bleViewModel.safe.collectAsState().value
    val batteryPercent = bleViewModel.batteryPercent.collectAsState().value

    @Composable
    fun batteryIconFor(percent: Int) = when {
        percent <= 5 -> Icons.Default.Battery0Bar
        percent <= 20 -> Icons.Default.Battery1Bar
        percent <= 35 -> Icons.Default.Battery2Bar
        percent <= 50 -> Icons.Default.Battery3Bar
        percent <= 65 -> Icons.Default.Battery4Bar
        percent <= 80 -> Icons.Default.Battery5Bar
        percent <= 95 -> Icons.Default.Battery6Bar
        else -> Icons.Default.BatteryFull
    }

    @Composable
    fun batteryColorFor(percent: Int): Color = when {
        percent <= 20 -> Color(0xFFD32F2F)
        percent <= 50 -> Color(0xFFFBC02D)
        else -> Color(0xFF388E3C)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Decibel IQ") },
                actions = {
                    Row(modifier = Modifier.padding(end = 12.dp)) {
                        Icon(
                            imageVector = batteryIconFor(batteryPercent),
                            contentDescription = "Battery",
                            tint = batteryColorFor(batteryPercent)
                        )
                        Text(
                            text = "$batteryPercent%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = batteryColorFor(batteryPercent)
                        )
                    }
                }
            )
        },

        bottomBar = {
            NavigationBar {

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

            // CURRENT LEVELS (all params passed)
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

            // SHIFT TIME
            composable("shift_time") {
                ShiftTimeScreen(
                    bleViewModel = bleViewModel,
                    currentDose = dose
                )
            }

            // PREVIOUS LOG
            composable("previous_log") {
                PreviousLogScreen(navController = homeNavController)
            }

            // SHIFT DETAIL
            composable("shift_detail/{logId}") { backStackEntry ->
                val logId = backStackEntry.arguments?.getString("logId") ?: return@composable
                ShiftDetailScreen(logId = logId)
            }

            // INFO MAIN SCREEN
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

            // INFO SUBSECTIONS — ALL TAKE ZERO PARAMETERS
            composable("info_why_noise") {
                InfoWhyNoiseScreen()
            }

            composable("info_sound_definitions") {
                InfoSoundDefinitionsScreen()
            }

            composable("info_noise_levels") {
                InfoNoiseLevelsScreen()
            }

            composable("info_indicator_info") {
                InfoIndicatorInfoScreen()
            }

            // SETTINGS
            composable("settings") {
                SettingsScreen(
                    navController = homeNavController,
                    bleViewModel = bleViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}