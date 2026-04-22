package com.example.ece441project.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.example.ece441project.viewmodel.ThemeViewModel

// -------------------------
// FULL MATERIAL 3 COLOR SETS
// -------------------------

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFCFBCFF),
    onPrimary = Color.Black,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun ECE441ProjectTheme(
    darkTheme: Boolean,
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val fontSize = themeViewModel.fontSize.collectAsState().value
    val buttonColor = themeViewModel.buttonColor.collectAsState().value

    // Convert font size index to scale factor
    val scale = when (fontSize) {
        0 -> 0.85f
        1 -> 1.0f
        else -> 1.25f
    }

    // Pick correct base color scheme
    val base = if (darkTheme) DarkColors else LightColors

    // Override primary with user-selected button color
    val colorScheme = base.copy(
        primary = Color(buttonColor),
        onPrimary = if (darkTheme) Color.Black else Color.White
    )

    // Apply scaled typography
    val typography = scaledTypography(Typography, scale)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}