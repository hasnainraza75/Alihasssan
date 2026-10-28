package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LonaDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepCosmicBlack,
    primaryContainer = Color(0xFF00382E),
    onPrimaryContainer = NeonCyan,
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B1561),
    onSecondaryContainer = Color(0xFFE0B0FF),
    tertiary = ElectricPink,
    onTertiary = Color.White,
    background = DeepCosmicBlack,
    onBackground = TextWhite,
    surface = DarkNavySurface,
    onSurface = TextWhite,
    surfaceVariant = DarkNavyCard,
    onSurfaceVariant = TextMuted,
    outline = DarkNavyBorder,
    error = StatusErrorRed,
    onError = Color.White
)

@Composable
fun LonaTheme(
    accentColor: Color = NeonCyan,
    content: @Composable () -> Unit
) {
    val scheme = LonaDarkColorScheme.copy(
        primary = accentColor
    )
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        content = content
    )
}
