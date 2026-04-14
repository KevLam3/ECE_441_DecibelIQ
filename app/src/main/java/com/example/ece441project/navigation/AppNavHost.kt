package com.example.ece441project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.ece441project.ui.theme.auth.AuthScreen
import com.example.ece441project.ui.theme.auth.RegisterScreen

import com.example.ece441project.ui.theme.home.HomeScaffold
import com.example.ece441project.ui.theme.home.DailyLogScreen
import com.example.ece441project.ui.theme.home.ForYouScreen
import com.example.ece441project.ui.theme.home.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = modifier
    ) {
        composable("auth") {
            AuthScreen(
                onSignInSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScaffold(navController)
        }

        composable("daily_log") { DailyLogScreen() }
        composable("for_you") { ForYouScreen() }
        composable("settings") { SettingsScreen() }
    }
}