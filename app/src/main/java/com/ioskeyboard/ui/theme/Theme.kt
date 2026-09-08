package com.ioskeyboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IOSLightPrimary = Color(0xFF007AFF)
private val IOSLightOnPrimary = Color.White
private val IOSLightBackground = Color(0xFFF2F2F7)
private val IOSLightOnBackground = Color.Black
private val IOSLightSurface = Color(0xFFFFFFFF)
private val IOSLightOnSurface = Color.Black
private val IOSLightSurfaceVariant = Color(0xFFE5E5EA)
private val IOSLightOnSurfaceVariant = Color(0xFF3C3C43)
private val IOSLightOutline = Color(0xFFC6C6C8)

private val IOSDarkPrimary = Color(0xFF0A84FF)
private val IOSDarkOnPrimary = Color.White
private val IOSDarkBackground = Color(0xFF000000)
private val IOSDarkOnBackground = Color.White
private val IOSDarkSurface = Color(0xFF1C1C1E)
private val IOSDarkOnSurface = Color.White
private val IOSDarkSurfaceVariant = Color(0xFF2C2C2E)
private val IOSDarkOnSurfaceVariant = Color(0xFFAEAEB2)
private val IOSDarkOutline = Color(0xFF38383A)

private val LightColorScheme = lightColorScheme(
    primary = IOSLightPrimary,
    onPrimary = IOSLightOnPrimary,
    background = IOSLightBackground,
    onBackground = IOSLightOnBackground,
    surface = IOSLightSurface,
    onSurface = IOSLightOnSurface,
    surfaceVariant = IOSLightSurfaceVariant,
    onSurfaceVariant = IOSLightOnSurfaceVariant,
    outline = IOSLightOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = IOSDarkPrimary,
    onPrimary = IOSDarkOnPrimary,
    background = IOSDarkBackground,
    onBackground = IOSDarkOnBackground,
    surface = IOSDarkSurface,
    onSurface = IOSDarkOnSurface,
    surfaceVariant = IOSDarkSurfaceVariant,
    onSurfaceVariant = IOSDarkOnSurfaceVariant,
    outline = IOSDarkOutline
)

@Composable
fun IOSStyleKeyboardTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
