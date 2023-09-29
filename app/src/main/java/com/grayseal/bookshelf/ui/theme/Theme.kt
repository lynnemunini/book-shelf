package com.grayseal.bookshelf.ui.theme

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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun BookShelfTheme(darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
                   dynamicColor: Boolean = true,
                   content: @Composable () -> Unit) {

    val darkColorScheme = darkColorScheme(
        background = Color.Black,
        onBackground = DarkWhite,
        primary = Pink200,
        surface = Color.Black,
        secondary = Gray,
        onSecondaryContainer = DarkWhite,
        tertiary = DarkWhite,
        primaryContainer = Color.Black,
        onTertiaryContainer = Gray700.copy(alpha = 0.8f),
        surfaceVariant = DarkWhite,
        outline = iconColorDarkMode
    )

    val lightColorScheme = lightColorScheme(
        background = Color.White,
        onBackground = Gray700.copy(alpha = 0.8f),
        primary = Pink500,
        surface = Color.White,
        secondary = Pink200,
        onSecondaryContainer = Color.White,
        tertiary = Pink700,
        primaryContainer = Pink500,
        onTertiaryContainer = Gray700.copy(alpha = 0.1f),
        surfaceVariant = Color.White,
        outline = iconColor
    )

    val colors = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity  = view.context as Activity
            activity.window.navigationBarColor = colorScheme.primary.copy(alpha = 0.08f).compositeOver(colorScheme.surface.copy()).toArgb()
            activity.window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        content = content
    )
}
