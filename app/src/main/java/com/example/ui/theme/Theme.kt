package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FleetCyanAccent,
    onPrimary = FleetNavy900,
    primaryContainer = FleetNavy700,
    onPrimaryContainer = Color.White,
    secondary = FleetTeal,
    onSecondary = FleetNavy900,
    background = SurfaceDark,
    surface = SurfaceCardDark,
    surfaceVariant = FleetNavy800,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    error = StatusAlert,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = FleetBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = FleetNavy900,
    secondary = FleetTeal,
    onSecondary = Color.White,
    background = SurfaceLight,
    surface = SurfaceCardLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    error = StatusAlert,
    onError = Color.White
)

@Composable
fun VeloFleetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // false to preserve cohesive corporate identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
