package com.ioskeyboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private object IOSColors {
    val Blue = Color(0xFF007AFF)
    val Indigo = Color(0xFF5856D6)
    val Green = Color(0xFF34C759)
    val Red = Color(0xFFFF3B30)
    val Orange = Color(0xFFFF9500)
    val Yellow = Color(0xFFFFCC00)

    val Gray1 = Color(0xFF8E8E93)
    val Gray2 = Color(0xFFAEAEB2)
    val Gray3 = Color(0xFFC7C7CC)
    val Gray4 = Color(0xFFD1D1D6)
    val Gray5 = Color(0xFFE5E5EA)
    val Gray6 = Color(0xFFF2F2F7)

    val Label = Color(0xFF000000)
    val SecondaryLabel = Color(0xFF3C3C43).copy(alpha = 0.6f)
    val TertiaryLabel = Color(0xFF3C3C43).copy(alpha = 0.3f)

    val Separator = Color(0xFF3C3C43).copy(alpha = 0.29f)
    val GroupedBg = Color(0xFFF2F2F7)
}

private val LightScheme = lightColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.15f),
    onPrimaryContainer = IOSColors.Blue,
    secondary = IOSColors.Gray1,
    onSecondary = Color.White,
    background = IOSColors.GroupedBg,
    onBackground = IOSColors.Label,
    surface = Color.White,
    onSurface = IOSColors.Label,
    surfaceVariant = IOSColors.Gray5,
    onSurfaceVariant = IOSColors.SecondaryLabel,
    outline = IOSColors.Gray4,
    outlineVariant = IOSColors.Gray5,
    error = IOSColors.Red,
    onError = Color.White
)

private val DarkScheme = darkColorScheme(
    primary = IOSColors.Blue,
    onPrimary = Color.White,
    primaryContainer = IOSColors.Blue.copy(alpha = 0.2f),
    onPrimaryContainer = IOSColors.Blue.copy(alpha = 0.9f),
    secondary = IOSColors.Gray2,
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF1C1C1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = IOSColors.Gray2,
    outline = Color(0xFF38383A),
    outlineVariant = Color(0xFF48484A),
    error = IOSColors.Red.copy(alpha = 0.9f),
    onError = Color.Black
)

@Composable
fun IOSStyleKeyboardTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content
    )
}
