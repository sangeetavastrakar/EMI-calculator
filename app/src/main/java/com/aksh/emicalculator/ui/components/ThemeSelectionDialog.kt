package com.aksh.emicalculator.ui.components

import com.aksh.emicalculator.data.ExtraStrings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.ThemeMode
import com.aksh.emicalculator.data.AccentColor
import com.aksh.emicalculator.data.TranslationRepository

@Composable
fun ThemeSelectionDialog(
    currentTheme: ThemeMode,
    currentAccent: AccentColor,
    language: AppLanguage,
    onThemeSelected: (ThemeMode, AccentColor) -> Unit,
    onDismissRequest: () -> Unit
) {
    var tempSelected by remember { mutableStateOf(currentTheme) }
    var tempAccent by remember { mutableStateOf(currentAccent) }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .testTag("theme_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Brightness4, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = TranslationRepository.getString(language, Strings.CHOOSE_THEME), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                ThemeOptionCard(title = TranslationRepository.getString(language, Strings.THEME_LIGHT), subtitle = TranslationRepository.getString(language, ExtraStrings.THEME_LIGHT_SUB), previewColor = Color(0xFFF59E0B), isSelected = tempSelected == ThemeMode.LIGHT, icon = Icons.Default.LightMode, onClick = { tempSelected = ThemeMode.LIGHT })
                Spacer(modifier = Modifier.height(8.dp))
                ThemeOptionCard(title = TranslationRepository.getString(language, Strings.THEME_DARK), subtitle = TranslationRepository.getString(language, ExtraStrings.THEME_DARK_SUB), previewColor = Color(0xFF1E293B), isSelected = tempSelected == ThemeMode.DARK, icon = Icons.Default.DarkMode, onClick = { tempSelected = ThemeMode.DARK })
                Spacer(modifier = Modifier.height(8.dp))
                ThemeOptionCard(title = TranslationRepository.getString(language, Strings.THEME_SYSTEM), subtitle = TranslationRepository.getString(language, ExtraStrings.THEME_SYSTEM_SUB), previewColor = Color.Gray, isSelected = tempSelected == ThemeMode.SYSTEM, icon = Icons.Default.Brightness4, onClick = { tempSelected = ThemeMode.SYSTEM })

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = TranslationRepository.getString(language, ExtraStrings.ACCENT_COLOR), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AccentColor.values().forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(color.colorValue))
                                .clickable { tempAccent = color }
                                .then(if (tempAccent == color) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { onThemeSelected(tempSelected, tempAccent) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(TranslationRepository.getString(language, ExtraStrings.APPLY_THEME), fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(title: String, subtitle: String, previewColor: Color, isSelected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(previewColor), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
