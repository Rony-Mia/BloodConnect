package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    secondary = SlateLight,
    tertiary = CrimsonAccent,
    background = CharcoalDark,
    surface = CharcoalSurface,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = SlateLight,
    onSurface = PureWhite,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = SlateLight
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonPrimary,
    secondary = SlateSecondary,
    tertiary = CrimsonAccent,
    background = SoftPinkBackground,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = CharcoalDark,
    onSurface = CharcoalDark,
    surfaceVariant = SlateLight,
    onSurfaceVariant = SlateSecondary
)

@Composable
fun BloodConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
@Deprecated("Use BloodConnectTheme instead", ReplaceWith("BloodConnectTheme(darkTheme, content)"))
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BloodConnectTheme(darkTheme = darkTheme, content = content)
}
