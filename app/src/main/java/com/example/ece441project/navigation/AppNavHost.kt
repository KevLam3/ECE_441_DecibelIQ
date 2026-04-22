package com.example.ece441project.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ece441project.BleViewModel
import com.example.ece441project.ui.theme.auth.AuthScreen
import com.example.ece441project.ui.theme.auth.RegisterScreen
import com.example.ece441project.ui.theme.home.HomeScaffold
import com.example.ece441project.ui.theme.home.SettingsScreen
import com.example.ece441project.viewmodel.ThemeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    bleViewModel: BleViewModel,
    themeViewModel: ThemeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        // AUTH SCREEN
        composable("auth") {
            Surface(color = MaterialTheme.colorScheme.background) {
                AuthScreen(
                    onSignInSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
        }

        // REGISTER SCREEN
        composable("register") {
            Surface(color = MaterialTheme.colorScheme.background) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // HOME (3‑TAB SCAFFOLD)
        composable("home") {
            HomeScaffold(
                bleViewModel = bleViewModel,
                themeViewModel = themeViewModel
            )
        }

        // SETTINGS SCREEN
        composable("settings") {
            SettingsScreen(
                navController = navController,
                bleViewModel = bleViewModel
            )
        }
    }
}