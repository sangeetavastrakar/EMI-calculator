package com.aksh.emicalculator

import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.createComposeRule
import com.aksh.emicalculator.data.ThemeMode
import com.aksh.emicalculator.ui.components.CalcColors
import com.aksh.emicalculator.ui.theme.EmiCalculatorTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ThemeModeTest {
    @get:Rule val compose = createComposeRule()
    @Test fun manualThemeChangesBothAppAndCalculatorSurfaces() {
        val mode = mutableStateOf(ThemeMode.LIGHT)
        var app = 0f
        var field = 0f
        var text = 0f
        compose.setContent {
            EmiCalculatorTheme(themeMode = mode.value) {
                val a = MaterialTheme.colorScheme.surface.luminance()
                val f = CalcColors.Field.luminance()
                val t = CalcColors.BlueDark.luminance()
                SideEffect { app = a; field = f; text = t }
            }
        }
        compose.runOnIdle { assertTrue(app > .8f); assertTrue(field > .8f); mode.value = ThemeMode.DARK }
        compose.runOnIdle { assertTrue(app < .1f); assertTrue(field < .1f); assertTrue(text > .5f) }
        compose.runOnIdle { mode.value = ThemeMode.LIGHT }
        compose.runOnIdle { assertTrue(app > .8f); assertTrue(field > .8f) }
    }
}
