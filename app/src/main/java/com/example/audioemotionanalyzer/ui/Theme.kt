package com.example.audioemotionanalyzer.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    primaryContainer = PrimaryLight,
    onPrimary = White,
    secondary = PrimaryDark,
    background = Background,
    surface = Surface,
    error = Error,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = White
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    primaryContainer = PrimaryDark,
    onPrimary = White,
    secondary = PrimaryLight,
    background = TextPrimary,
    surface = TextSecondary,
    error = Error,
    onBackground = White,
    onSurface = White,
    onError = Black
)

@Composable
fun AudioEmotionAnalyzerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}