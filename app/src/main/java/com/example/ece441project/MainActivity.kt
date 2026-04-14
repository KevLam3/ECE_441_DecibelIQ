package com.example.ece441project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.ece441project.navigation.AppNavHost
import com.example.ece441project.ui.theme.ECE441ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ECE441ProjectTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}