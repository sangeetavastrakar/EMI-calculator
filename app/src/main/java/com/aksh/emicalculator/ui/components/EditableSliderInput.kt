package com.aksh.emicalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong

/**
 * Shared EMI-style numeric field with optional quick-amount presets.
 */
@Composable
fun EditableSliderInputCard(
    title: String,
    value: Double,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    prefix: String = "",
    suffix: String = "",
    isDecimal: Boolean = false,
    quickChips: List<Pair<String, Double>> = emptyList(),
    headerTrailingContent: (@Composable () -> Unit)? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    testTagPrefix: String = "slider_input"
) {
    var textInput by remember { mutableStateOf(if (value == 0.0) "" else java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()) }
    androidx.compose.runtime.LaunchedEffect(value) {
        if (textInput.toDoubleOrNull() != value) {
            textInput = if (value == 0.0) "" else java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
        }
    }
    Column(modifier = modifier.fillMaxWidth().testTag("${testTagPrefix}_card")) {
        CalcFieldSurface(
            label = title,
            icon = if (suffix.contains("%")) Icons.Default.Percent else Icons.Default.Calculate,
            tint = if (suffix.contains("%")) CalcColors.Green else CalcColors.Blue,
            suffix = suffix,
            trailing = headerTrailingContent
        ) {
            CalcBasicTextField(
                text = textInput, isDecimal = isDecimal, prefix = prefix,
                modifier = Modifier.testTag("${testTagPrefix}_text_field"),
                onTextChange = { raw ->
                    val clean = raw.filter { it.isDigit() || (isDecimal && it == '.') }.take(14)
                    if (clean.count { it == '.' } <= 1) {
                        textInput = clean
                        if (clean.isBlank()) onValueChange(0.0)
                        else clean.toDoubleOrNull()?.takeIf { it.isFinite() }?.let {
                            onValueChange(it.coerceIn(valueRange.start.toDouble(), valueRange.endInclusive.toDouble()))
                        }
                    }
                }
            )
        }
        if (quickChips.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(quickChips) { (label, preset) ->
                    Surface(
                        shape = RoundedCornerShape(9.dp), color = CalcColors.Pale2,
                        modifier = Modifier.clip(RoundedCornerShape(9.dp)).clickable {
                            onValueChange(preset.coerceIn(valueRange.start.toDouble(), valueRange.endInclusive.toDouble()))
                        }
                    ) { Text(label, fontSize = 11.sp, color = CalcColors.Blue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) }
                }
            }
        }
    }
}