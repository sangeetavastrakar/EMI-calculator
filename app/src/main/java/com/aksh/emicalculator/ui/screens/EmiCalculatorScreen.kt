package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.LoanCategory
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.DonutChart
import com.aksh.emicalculator.ui.theme.AmberInterest
import com.aksh.emicalculator.utils.PdfExportUtils
import com.aksh.emicalculator.viewmodel.EmiViewModel

@Composable
fun EmiCalculatorScreen(
    viewModel: EmiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val category by viewModel.loanCategory.collectAsState()
    val principal by viewModel.principal.collectAsState()
    val rate by viewModel.annualRate.collectAsState()
    val tenureMonths by viewModel.tenureMonths.collectAsState()
    val isTenureInYears by viewModel.isTenureInYears.collectAsState()
    val emiResult by viewModel.emiResult.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val assetPrice by viewModel.assetPrice.collectAsState()
    val downPayment by viewModel.downPayment.collectAsState()
    val context = LocalContext.current
    fun t(key: String) = TranslationRepository.getString(language, key)
    val sym = currency.symbol

    // PDF / Share sabke liye free aur turant (koi ad beech me nahi) — bada ad ab kahin aur,
    // natural pause par (tab switch) dikhta hai, in dono zaroori actions ko block nahi karta.
    val pdfPrice = if (category.hasDownPayment) assetPrice else 0.0
    val pdfDown = if (category.hasDownPayment) downPayment else 0.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(com.aksh.emicalculator.ui.components.CalcColors.Pale2, com.aksh.emicalculator.ui.components.CalcColors.Pale, com.aksh.emicalculator.ui.components.CalcColors.Card)
                )
            )
            .testTag("emi_calculator_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Loan categories; Clear is available once in the result actions.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f).testTag("loan_type_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LoanCategory.values()) { cat ->
                        LoanTypeChip(
                            category = cat,
                            isSelected = cat == category,
                            label = t(cat.titleKey),
                            onClick = { viewModel.setLoanCategory(cat) }
                        )
                    }
                }
            }

            // Input Fields (Compact)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (category.hasDownPayment) {
                    CompactField(
                        title = t(if (category == LoanCategory.HOME) ExtraStrings.PRICE_HOME else ExtraStrings.PRICE_CAR),
                        value = assetPrice, prefix = sym, suffix = "", step = category.priceStep.toDouble(),
                        min = 0.0, max = category.maxPrice, isDecimal = false, tag = "price_input"
                    ) { viewModel.updateAssetPrice(it) }

                    val downPct = if (assetPrice > 0.0) downPayment / assetPrice * 100.0 else 0.0
                    CompactField(
                        title = t(ExtraStrings.DOWN_PAYMENT) + " (" + String.format(java.util.Locale.US, "%.0f", downPct) + "%)",
                        value = downPayment, prefix = sym, suffix = "", step = category.downStep.toDouble(),
                        min = 0.0, max = assetPrice, isDecimal = false, tag = "down_payment_input"
                    ) { viewModel.updateDownPayment(it) }
                } else {
                    CompactField(
                        title = t(Strings.LOAN_AMOUNT), value = principal, prefix = sym, suffix = "", step = 10000.0,
                        min = 0.0, max = 100000000.0, isDecimal = false, tag = "principal_input"
                    ) { viewModel.updatePrincipal(it) }
                }

                CompactField(
                    title = t(Strings.INTEREST_RATE), value = rate, prefix = "", suffix = "%", step = 0.1,
                    min = 0.0, max = 36.0, isDecimal = true, tag = "rate_input"
                ) { viewModel.updateAnnualRate(it) }

                val displayTenure = if (isTenureInYears) tenureMonths / 12.0 else tenureMonths.toDouble()
                CompactField(
                    title = t(Strings.LOAN_TENURE), value = displayTenure, prefix = "",
                    suffix = " " + if (isTenureInYears) t(Strings.YEARS) else t(Strings.MONTHS),
                    step = 1.0, min = 0.0, max = if (isTenureInYears) 40.0 else 480.0, isDecimal = false, tag = "tenure_input",
                    trailing = {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { viewModel.toggleTenureUnit() }
                                .testTag("toggle_tenure_unit_btn")
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isTenureInYears) t(Strings.YEARS) else t(Strings.MONTHS),
                                    style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Unit", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                ) { viewModel.updateTenure(it.toInt(), isTenureInYears) }
            }

            if (emiResult.monthlyEmi > 0.0) {
                ToolResultCard(
                    title = t(Strings.MONTHLY_EMI),
                    mainValue = EmiMathUtils.formatCurrency(emiResult.monthlyEmi, currency),
                    rows = listOf(t(Strings.TOTAL_INTEREST) to EmiMathUtils.formatCurrency(emiResult.totalInterest, currency),
                        t(Strings.TOTAL_PAYMENT) to EmiMathUtils.formatCurrency(emiResult.totalPayment, currency)),
                    tag = "emi_result_card", language = language, pdfTitle = "EMI Calculator",
                    pdfSubtitle = "Loan report", pdfRows = emptyList(), shareText = "Loan report",
                    onClear = { viewModel.clearEmiCalculator() },
                    onPdfOverride = { PdfExportUtils.downloadAndOpenPdf(context, principal, rate, tenureMonths,
                        isTenureInYears, emiResult, language, pdfPrice, pdfDown) },
                    onShareOverride = { PdfExportUtils.shareLoanDetails(context, principal, rate, tenureMonths,
                        isTenureInYears, emiResult, language, pdfPrice, pdfDown) }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ResultIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    desc: String,
    locked: Boolean,
    tag: String,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp).testTag(tag)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = desc, tint = tint, modifier = Modifier.size(22.dp))
            if (locked) {
                Icon(
                    Icons.Default.Lock, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(10.dp).align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
private fun CompactField(
    title: String,
    value: Double,
    prefix: String,
    suffix: String,
    step: Double,
    min: Double,
    max: Double,
    isDecimal: Boolean,
    tag: String,
    trailing: (@Composable () -> Unit)? = null,
    onValueChange: (Double) -> Unit
) {
    fun fmt(v: Double): String {
        if (v == 0.0) return ""
        return if (isDecimal) java.math.BigDecimal(v).setScale(2, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
        else v.toLong().toString()
    }

    var text by remember { mutableStateOf(fmt(value)) }
    androidx.compose.runtime.LaunchedEffect(value) {
        if (value == 0.0 && text.toDoubleOrNull() != 0.0) text = ""
        else if (text.toDoubleOrNull() != value) text = fmt(value)
    }
    val safeStep = if (step <= 0.0) 1.0 else step

    com.aksh.emicalculator.ui.components.CalcFieldSurface(
        label = title, icon = Icons.Default.Add,
        tint = com.aksh.emicalculator.ui.components.CalcColors.Blue,
        suffix = suffix, trailing = trailing, modifier = Modifier.testTag(tag)
    ) {
        com.aksh.emicalculator.ui.components.CalcBasicTextField(
            text = text, prefix = prefix, isDecimal = isDecimal,
            onTextChange = { raw ->
                val clean = raw.filter { it.isDigit() || (isDecimal && it == '.') }.take(14)
                if (clean.count { it == '.' } <= 1) {
                    text = clean
                    if (clean.isBlank()) onValueChange(0.0)
                    else clean.toDoubleOrNull()?.takeIf { it.isFinite() }?.let { onValueChange(it.coerceIn(min, max)) }
                }
            }, modifier = Modifier.testTag(tag + "_text_field")
        )
    }
}

/**
 * Chhota reusable "Clear" button jo kisi bhi calculator screen me use ho sakta hai.
 * Tap karne par saare inputs 0 par reset ho jaate hain aur result/chart wapas gayab ho jaata hai.
 */
@Composable
internal fun ClearButton(
    label: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun LoanTypeChip(
    category: LoanCategory,
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    val icon = when (category) {
        LoanCategory.HOME -> Icons.Default.Home
        LoanCategory.CAR -> Icons.Default.DirectionsCar
        LoanCategory.PERSONAL -> Icons.Default.Person
        LoanCategory.EDUCATION -> Icons.Default.School
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("loan_chip_${category.name}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}