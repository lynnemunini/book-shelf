package com.grayseal.bookshelf.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    background = Color.Black,
    onBackground = DarkWhite,
    primary = Pink200,
    surface = Color.Black,
    secondary = Gray,
    onSecondaryContainer = DarkWhite,
    tertiary = DarkWhite,
    primaryContainer = Color.Black,
    onTertiaryContainer = Gray700.copy(alpha = 0.8f),
    surfaceVariant = DarkWhite
)

private val LightColorPalette = lightColorScheme(
    background = Color.White,
    onBackground = Gray700.copy(alpha = 0.8f),
    primary = Pink500,
    surface = Color.White,
    secondary = Pink200,
    onSecondaryContainer = Color.White,
    tertiary = Pink700,
    primaryContainer = Pink500,
    onTertiaryContainer = Gray700.copy(alpha = 0.1f),
    surfaceVariant = Color.White
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun BookShelfTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: androidx.compose.material3.ColorScheme =
        if (darkTheme) {
            LightColorPalette
        } else {
            LightColorPalette
        }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        content = content
    )
}