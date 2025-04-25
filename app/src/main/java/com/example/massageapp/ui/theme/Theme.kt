package com.example.massageapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF8BC34A),
    primaryVariant = Color(0xFF689F38),
    secondary = Color(0xFFCDDC39),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColorPalette = darkColors(
    primary = Color(0xFF8BC34A),
    primaryVariant = Color(0xFF689F38),
    secondary = Color(0xFFCDDC39)
)

@Composable
fun MassageAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}