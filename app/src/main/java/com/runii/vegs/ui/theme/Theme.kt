package com.runii.vegs.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Fantasy dark palette
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE8C547),
    onPrimary = Color(0xFF1A0F0A),
    primaryContainer = Color(0xFF5C4A1F),
    onPrimaryContainer = Color(0xFFFFE9A8),
    secondary = Color(0xFFC4A77D),
    onSecondary = Color(0xFF1A0F0A),
    tertiary = Color(0xFF8B5A2B),
    background = Color(0xFF0F0A07),
    onBackground = Color(0xFFEDE0D0),
    surface = Color(0xFF1A120C),
    onSurface = Color(0xFFEDE0D0),
    surfaceVariant = Color(0xFF2A1F16),
    onSurfaceVariant = Color(0xFFD4C4A8),
    error = Color(0xFFFF6B6B),
    outline = Color(0xFF6B5A45)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B6914),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE9A8),
    onPrimaryContainer = Color(0xFF2A1F00),
    secondary = Color(0xFF6B5A45),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF8B5A2B),
    background = Color(0xFFFFF8F0),
    onBackground = Color(0xFF1A120C),
    surface = Color(0xFFFFFBF5),
    onSurface = Color(0xFF1A120C),
    surfaceVariant = Color(0xFFF0E6D8),
    onSurfaceVariant = Color(0xFF4A3F32),
    error = Color(0xFFB3261E),
    outline = Color(0xFFA09080)
)

@Composable
fun RuniiTheme(
    themeMode: String = "system",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
