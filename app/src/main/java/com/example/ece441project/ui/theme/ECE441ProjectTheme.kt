package com.example.ece441project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.example.ece441project.viewmodel.ThemeViewModel

@Composable
fun ECE441ProjectTheme(
    darkTheme: Boolean,
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val fontSize = themeViewModel.fontSize.collectAsState().value

    // Convert 0/1/2 → scale factor
    val scale = when (fontSize) {
        0 -> 0.85f   // Small
        1 -> 1.0f    // Medium (default)
        else -> 1.15f // Large
    }

    val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

    val scaledTypography = scaledTypography(
        base = Typography,
        scale = scale
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )

    val buttonColor = themeViewModel.buttonColor.collectAsState().value

    val customColorScheme = colorScheme.copy(
        primary = Color(buttonColor)
    )
}