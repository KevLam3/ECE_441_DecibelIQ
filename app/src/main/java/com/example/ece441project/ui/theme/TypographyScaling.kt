package com.example.ece441project.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

fun scaledTypography(base: Typography, scale: Float): Typography {
    return Typography(
        displayLarge = base.displayLarge.copy(fontSize = base.displayLarge.fontSize * scale),
        displayMedium = base.displayMedium.copy(fontSize = base.displayMedium.fontSize * scale),
        displaySmall = base.displaySmall.copy(fontSize = base.displaySmall.fontSize * scale),

        headlineLarge = base.headlineLarge.copy(fontSize = base.headlineLarge.fontSize * scale),
        headlineMedium = base.headlineMedium.copy(fontSize = base.headlineMedium.fontSize * scale),
        headlineSmall = base.headlineSmall.copy(fontSize = base.headlineSmall.fontSize * scale),

        titleLarge = base.titleLarge.copy(fontSize = base.titleLarge.fontSize * scale),
        titleMedium = base.titleMedium.copy(fontSize = base.titleMedium.fontSize * scale),
        titleSmall = base.titleSmall.copy(fontSize = base.titleSmall.fontSize * scale),

        bodyLarge = base.bodyLarge.copy(fontSize = base.bodyLarge.fontSize * scale),
        bodyMedium = base.bodyMedium.copy(fontSize = base.bodyMedium.fontSize * scale),
        bodySmall = base.bodySmall.copy(fontSize = base.bodySmall.fontSize * scale),

        labelLarge = base.labelLarge.copy(fontSize = base.labelLarge.fontSize * scale),
        labelMedium = base.labelMedium.copy(fontSize = base.labelMedium.fontSize * scale),
        labelSmall = base.labelSmall.copy(fontSize = base.labelSmall.fontSize * scale)
    )
}