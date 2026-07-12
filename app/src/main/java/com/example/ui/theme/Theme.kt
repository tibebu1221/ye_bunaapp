package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = WarmAmberAccent,
    secondary = LightCremaSecondary,
    tertiary = MilkChocolateTertiary,
    background = EspressoDarkBg,
    surface = CoffeeBeanDarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = LightGrayText,
    onSurface = LightGrayText,
    surfaceVariant = Color(0xFF2C201D),
    onSurfaceVariant = Color(0xFFD7CCC8)
)

private val LightColorScheme = lightColorScheme(
    primary = RoastedEspressoPrimary,
    secondary = LatteSecondary,
    tertiary = ClayTertiary,
    background = WarmCremaLightBg,
    surface = SoftLinenLightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkGrayText,
    onSurface = DarkGrayText,
    surfaceVariant = Color(0xFFEFE5E0),
    onSurfaceVariant = Color(0xFF5D4037)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark theme for a premium Yebuna experience!
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
