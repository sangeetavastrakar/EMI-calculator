package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aksh.emicalculator.ui.components.CalcBasicTextField
import com.aksh.emicalculator.ui.components.CalcCard
import com.aksh.emicalculator.ui.components.CalcColors
import com.aksh.emicalculator.ui.components.CalcFieldSurface
import java.util.Locale
import kotlin.math.abs

/**
 * Simple, consistent form pieces used by all the extra calculators.
 * They look like the rest of the app (rounded cards, M3 colours) and work in every theme.
 */

@Composable
fun ToolField(
    title: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    isDecimal: Boolean = false,
    testTag: String = "tool_field"
) {
    fun display(v: Double): String = when {
        v == 0.0 -> ""
        isDecimal -> String.format(Locale.US, "%.2f", v)
        else -> v.toLong().toString()
    }

    var text by remember { mutableStateOf(display(value)) }
    var focused by remember { mutableStateOf(false) }

    // Bahar se value badle (slider/chip/sync) to text update karo, par jab user type kar raha ho
    // tab bilkul chhedo nahi — warna "8." likhte hi field "8.00" ban jata hai.
    LaunchedEffect(value) {
        // External actions (Clear / Copy A to B) must update even a focused field.
        // Numerically identical input such as "8." stays intact while typing.
        val current = text.toDoubleOrNull()
        if (current == null || abs(current - value) > 0.0001) text = display(value)
    }
    LaunchedEffect(focused) {
        if (!focused) text = display(value)
    }

    CalcFieldSurface(
        label = title,
        icon = Icons.Default.Payments,
        tint = if (suffix.contains("%")) CalcColors.Green else CalcColors.Blue,
        modifier = modifier.padding(vertical = 4.dp),
        suffix = suffix
    ) {
        CalcBasicTextField(
            text = text,
            onTextChange = { raw ->
                val filtered = raw.filter { it.isDigit() || (isDecimal && it == '.') }.take(14)
                if (filtered.count { it == '.' } <= 1) {
                    text = filtered
                    if (filtered.isBlank()) onValueChange(0.0)
                    else filtered.toDoubleOrNull()?.takeIf { it.isFinite() }?.let(onValueChange)
                }
            },
            isDecimal = isDecimal,
            prefix = prefix,
            modifier = Modifier.onFocusChanged { focused = it.isFocused }.testTag(testTag)
        )
    }
}

@Composable
fun ToolChoices(
    options: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            ToolChip(
                label = label,
                selected = index == selectedIndex,
                tag = "tool_choice_$index"
            ) { onSelect(index) }
        }
    }
}

@Composable
fun ToolDropdown(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CalcColors.Field,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { expanded = true }
                .testTag("tool_dropdown")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (label.isNotBlank()) {
                        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        options.getOrElse(selectedIndex) { options.firstOrNull() ?: "" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        expanded = false
                        onSelect(index)
                    }
                )
            }
        }
    }
}

@Composable
fun ToolSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CalcColors.Card),
        border = androidx.compose.foundation.BorderStroke(1.dp, CalcColors.FieldBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = CalcColors.BlueDark)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}
