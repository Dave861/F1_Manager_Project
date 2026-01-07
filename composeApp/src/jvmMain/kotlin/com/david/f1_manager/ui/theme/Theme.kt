package com.david.f1_manager.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val F1DarkColorScheme = darkColorScheme(
    primary = F1Red,
    onPrimary = TextPrimary,
    primaryContainer = F1RedDark,
    onPrimaryContainer = TextPrimary,

    secondary = PodiumGold,
    onSecondary = CarbonBlack,
    secondaryContainer = Color(0xFFB8860B), // Dark gold
    onSecondaryContainer = TextPrimary,

    tertiary = FlagGreen,
    onTertiary = CarbonBlack,
    tertiaryContainer = Color(0xFF00A038), // Dark green
    onTertiaryContainer = TextPrimary,

    error = DangerRed,
    onError = TextPrimary,
    errorContainer = Color(0xFFCC2E24),
    onErrorContainer = TextPrimary,

    background = BackgroundDark,
    onBackground = TextPrimary,

    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,

    outline = TextTertiary,
    outlineVariant = Color(0xFF404040)
)

@Composable
fun F1Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = F1DarkColorScheme,
        typography = F1Typography,
        content = content
    )
}
