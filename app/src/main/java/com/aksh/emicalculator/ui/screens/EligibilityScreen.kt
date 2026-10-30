package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.EditableSliderInputCard
import com.aksh.emicalculator.viewmodel.EmiViewModel

@Composable
fun EligibilityScreen(viewModel: EmiViewModel, modifier: Modifier = Modifier) {
    val language by viewModel.currentLanguage.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val sym = currency.symbol

    // Sab kuch 0.0 se start
    var income by rememberSaveable { mutableStateOf(0.0) }
    var existingEmi by rememberSaveable { mutableStateOf(0.0) }
    var foirPercent by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }

    val maxEmi = (income * foirPercent / 100.0 - existingEmi).coerceAtLeast(0.0)
    val maxLoan = EmiMathUtils.maxLoanForEmi(maxEmi, rate, (years * 12).toInt())
    val estPrice = maxLoan / 0.8

    Box(
        modifier = modifier.fillMaxSize().testTag("eligibility_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TranslationRepository.getString(language, ExtraStrings.ELIG_TITLE),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                ClearButton(
                    label = TranslationRepository.getString(language, ExtraStrings.CLEAR_BTN),
                    tag = "clear_eligibility_btn"
                ) {
                    income = 0.0
                    existingEmi = 0.0
                    foirPercent = 0.0
                    rate = 0.0
                    years = 0.0
                }
            }
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth().testTag("eligibility_result_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = TranslationRepository.getString(language, ExtraStrings.ELIG_MAX_LOAN),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = EmiMathUtils.formatCurrency(maxLoan, currency),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("eligible_loan_text")
                    )
                    Spacer(Modifier.height(12.dp))
                    ResultRow(TranslationRepository.getString(language, ExtraStrings.ELIG_MAX_EMI), EmiMathUtils.formatCurrency(maxEmi, currency))
                    Spacer(Modifier.height(6.dp))
                    ResultRow(TranslationRepository.getString(language, ExtraStrings.ELIG_EST_PRICE), EmiMathUtils.formatCurrency(estPrice, currency))
                }
            }

            Spacer(Modifier.height(14.dp))
            EditableSliderInputCard(
                title = TranslationRepository.getString(language, ExtraStrings.ELIG_INCOME),
                value = income, valueRange = 0f..1000000f, stepSize = 1000f, prefix = sym,
                quickChips = listOf("25K" to 25000.0, "50K" to 50000.0, "75K" to 75000.0, "1L" to 100000.0, "2L" to 200000.0),
                onValueChange = { income = it }, testTagPrefix = "elig_income"
            )
            Spacer(Modifier.height(14.dp))
            EditableSliderInputCard(
                title = TranslationRepository.getString(language, ExtraStrings.ELIG_EXISTING_EMI),
                value = existingEmi, valueRange = 0f..500000f, stepSize = 500f, prefix = sym,
                onValueChange = { existingEmi = it }, testTagPrefix = "elig_existing"
            )
            Spacer(Modifier.height(14.dp))
            EditableSliderInputCard(
                title = TranslationRepository.getString(language, ExtraStrings.ELIG_FOIR),
                value = foirPercent, valueRange = 0f..70f, stepSize = 5f, suffix = "%",
                quickChips = listOf("40%" to 40.0, "50%" to 50.0, "60%" to 60.0),
                onValueChange = { foirPercent = it }, testTagPrefix = "elig_foir"
            )
            Spacer(Modifier.height(14.dp))
            EditableSliderInputCard(
                title = TranslationRepository.getString(language, Strings.INTEREST_RATE),
                value = rate, valueRange = 0f..30f, stepSize = 0.05f, suffix = "%", isDecimal = true,
                quickChips = listOf("8.35%" to 8.35, "9.0%" to 9.0, "10.5%" to 10.5, "12.0%" to 12.0),
                onValueChange = { rate = it.coerceIn(0.0, 36.0) }, testTagPrefix = "elig_rate"
            )
            Spacer(Modifier.height(14.dp))
            EditableSliderInputCard(
                title = TranslationRepository.getString(language, Strings.LOAN_TENURE),
                value = years, valueRange = 0f..30f, stepSize = 1f,
                suffix = " " + TranslationRepository.getString(language, Strings.YEARS),
                quickChips = listOf("5" to 5.0, "10" to 10.0, "15" to 15.0, "20" to 20.0, "30" to 30.0),
                onValueChange = { years = it.coerceIn(0.0, 40.0) }, testTagPrefix = "elig_tenure"
            )

            val reportRows = listOf(
                TranslationRepository.getString(language, ExtraStrings.ELIG_INCOME) to EmiMathUtils.formatCurrency(income, currency),
                TranslationRepository.getString(language, ExtraStrings.ELIG_EXISTING_EMI) to EmiMathUtils.formatCurrency(existingEmi, currency),
                TranslationRepository.getString(language, Strings.INTEREST_RATE) to "$rate%",
                TranslationRepository.getString(language, Strings.LOAN_TENURE) to "$years years",
                TranslationRepository.getString(language, ExtraStrings.ELIG_MAX_LOAN) to EmiMathUtils.formatCurrency(maxLoan, currency),
                TranslationRepository.getString(language, ExtraStrings.ELIG_MAX_EMI) to EmiMathUtils.formatCurrency(maxEmi, currency)
            )
            if (maxLoan > 0.0) {
                ToolResultCard(
                    title = TranslationRepository.getString(language, ExtraStrings.ELIG_MAX_LOAN),
                    mainValue = EmiMathUtils.formatCurrency(maxLoan, currency), rows = emptyList(),
                    tag = "eligibility_export", language = language, pdfTitle = "Loan Eligibility",
                    pdfSubtitle = "Estimated eligibility", pdfRows = reportRows,
                    pdfFooter = TranslationRepository.getString(language, ExtraStrings.ELIG_NOTE),
                    shareText = "Loan eligibility estimate",
                    onClear = { income = 0.0; existingEmi = 0.0; foirPercent = 0.0; rate = 0.0; years = 0.0 }
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = TranslationRepository.getString(language, ExtraStrings.ELIG_NOTE),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
