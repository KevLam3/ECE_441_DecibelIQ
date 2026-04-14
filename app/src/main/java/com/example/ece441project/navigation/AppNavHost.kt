package com.example.ece441project.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ece441project.BleViewModel
import com.example.ece441project.ui.theme.auth.AuthScreen
import com.example.ece441project.ui.theme.auth.RegisterScreen
import com.example.ece441project.ui.theme.home.HomeScaffold
import com.example.ece441project.ui.theme.home.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    bleViewModel: BleViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {

        // -------------------------
        // AUTH SCREEN
        // -------------------------
        composable("auth") {
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

        // -------------------------
        // REGISTER SCREEN
        // -------------------------
        composable("register") {
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

        // -------------------------
        // HOME (3‑TAB SCAFFOLD)
        // -------------------------
        composable("home") {
            HomeScaffold(
                bleViewModel = bleViewModel
            )
        }

        // -------------------------
        // SETTINGS SCREEN
        // -------------------------
        composable("settings") {
            SettingsScreen(
                navController = navController,
                bleViewModel = bleViewModel
            )
        }
    }
}
