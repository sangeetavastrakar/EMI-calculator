package com.aksh.emicalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.aksh.emicalculator.data.ThemeMode
import com.aksh.emicalculator.data.AccentColor

val LocalCalcDark = staticCompositionLocalOf { false }

// Premium Dark UI Colors (The requested default)
private fun getDarkColorScheme(accentColor: AccentColor) = darkColorScheme(
    primary = Color(accentColor.colorValue),
    onPrimary = Color.White,
    primaryContainer = Color(accentColor.colorValue).copy(alpha = 0.3f),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFF97316),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9A3412),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color.Black,
    background = ObsidianBg,
    onBackground = TextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = ObsidianBorder,
    error = RoseRed,
    onError = Color.White
)

private fun getLightColorScheme(accentColor: AccentColor) = lightColorScheme(
    primary = Color(accentColor.colorValue),
    onPrimary = Color.White,
    primaryContainer = Color(accentColor.colorValue).copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFFF97316),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = Color(0xFFFBBF24),
    onTertiary = Color.White,
    background = SlateBg,
    onBackground = TextPrimaryLight,
    surface = SlateSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = SlateBorder,
    error = RoseRed,
    onError = Color.White
)

@Composable
fun resolveDarkTheme(themeMode: ThemeMode): Boolean = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun EmiCalculatorTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    accentColor: AccentColor = AccentColor.ROYAL_BLUE,
    content: @Composable () -> Unit
) {
    val dark = resolveDarkTheme(themeMode)
    CompositionLocalProvider(LocalCalcDark provides dark) {
        MaterialTheme(
            colorScheme = if (dark) getDarkColorScheme(accentColor) else getLightColorScheme(accentColor),
            typography = Typography,
            content = content
        )
    }
}
