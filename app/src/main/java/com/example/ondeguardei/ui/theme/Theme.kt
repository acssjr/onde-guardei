package com.example.ondeguardei.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Dark Theme Premium Color Scheme
 * Design inspirado em apps premium como Spotify/Netflix
 */
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,

    secondary = Secondary,
    onSecondary = OnSecondary,

    tertiary = Tertiary,
    onTertiary = OnTertiary,

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,

    error = Error,
    onError = OnPrimary,

    // Container colors
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimary,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondary,
    tertiaryContainer = Tertiary,
    onTertiaryContainer = OnTertiary,

    // Outline colors
    outline = White50,
    outlineVariant = White70,

    // Inverse colors
    inverseSurface = OnSurface,
    inverseOnSurface = Surface,
    inversePrimary = Primary,

    // Surface tints
    surfaceTint = Primary,
)

@Composable
fun OndeGuardeiTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Edge-to-edge: Status bar transparente
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Surface.toArgb()

            // Ícones da status bar em branco (dark theme)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
