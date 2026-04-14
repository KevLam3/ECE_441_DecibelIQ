package com.example.ece441project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ece441project.ui.theme.auth.AuthScreen
import com.example.ece441project.ui.theme.auth.AuthViewModel
import com.example.ece441project.ui.theme.auth.RegisterScreen
import com.example.ece441project.ui.theme.home.HomeScaffold

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "auth",
        modifier = modifier
    ) {
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onSignInSuccess = { navController.navigate("home") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate("home") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScaffold()
        }
    }
}