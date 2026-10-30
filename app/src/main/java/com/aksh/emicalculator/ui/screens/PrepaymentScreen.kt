package com.aksh.emicalculator.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.PrepaymentMode
import com.aksh.emicalculator.data.PrepaymentMonthRow
import com.aksh.emicalculator.data.PrepaymentType
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.theme.EmeraldLight
import com.aksh.emicalculator.utils.SchedulePdf
import com.aksh.emicalculator.viewmodel.EmiViewModel
import java.util.Locale
import kotlin.math.abs

private fun tr(language: AppLanguage, key: String) = TranslationRepository.getString(language, key)

private fun money(value: Double) = EmiMathUtils.formatCurrency(value)

private fun compactMoney(value: Double): String {
    val absValue = abs(value)
    return when {
        absValue >= 10_000_000.0 -> String.format(Locale.US, "%.2fCr", value / 10_000_000.0)
        absValue >= 100_000.0 -> String.format(Locale.US, "%.2fL", value / 100_000.0)
        absValue >= 1_000.0 -> String.format(Locale.US, "%.0fK", value / 1_000.0)
        else -> String.format(Locale.US, "%.0f", value)
    }
}

private fun tenureText(months: Int, language: AppLanguage): String {
    if (months <= 0) return "0"
    val years = months / 12
    val rest = months % 12
    val yr = tr(language, ToolStrings.YEARS_SHORT)
    val mo = tr(language, ToolStrings.MONTHS_SHORT)
    return when {
        years > 0 && rest > 0 -> "$years$yr $rest$mo"
        years > 0 -> "$years$yr"
        else -> "$rest$mo"
    }
}

@Composable
fun PrepaymentScreen(
    viewModel: EmiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val originalLoan by viewModel.prepaymentOriginalLoan.collectAsState()
    val rate by viewModel.prepaymentRate.collectAsState()
    val tenureMonths by viewModel.prepaymentTenureMonths.collectAsState()
    val isTenureInYears by viewModel.prepaymentIsTenureInYears.collectAsState()
    val emisAlreadyPaid by viewModel.emisAlreadyPaid.collectAsState()
    val prepType by viewModel.prepaymentType.collectAsState()
    val extraAmount by viewModel.extraAmount.collectAsState()
    val prepMode by viewModel.prepaymentMode.collectAsState()
    val customPayments by viewModel.customPayments.collectAsState()
    val impact by viewModel.prepaymentImpact.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val sym = EmiMathUtils.activeCurrency.symbol

    var hasCalculated by rememberSaveable {
        mutableStateOf(originalLoan > 0.0 && tenureMonths > 0)
    }
    var showAllRows by rememberSaveable { mutableStateOf(false) }
    var showAdvanced by rememberSaveable { mutableStateOf(false) }

    val schedule: List<PrepaymentMonthRow> = remember(
        originalLoan, rate, tenureMonths, emisAlreadyPaid, prepType, extraAmount, prepMode, customPayments
    ) {
        if (originalLoan <= 0.0 || tenureMonths <= 0) {
            emptyList()
        } else {
            EmiMathUtils.generatePrepaymentSchedule(
                principal = originalLoan,
                annualRatePercent = rate,
                tenureMonths = tenureMonths,
                prepaymentType = prepType,
                extraAmount = extraAmount,
                emisAlreadyPaid = emisAlreadyPaid,
                mode = prepMode,
                customPayments = customPayments
            )
        }
    }

    val originalEmi = impact.originalEmi
    val originalTotalInterest = impact.originalTotalInterest
    val totalPayment = originalLoan + originalTotalInterest
    val newTotalInterest = schedule.sumOf { it.interestPaid }
    val newTotalPayment = schedule.sumOf { it.totalPaid }
    val newTenure = if (schedule.isNotEmpty()) schedule.size else impact.newTenureMonths

    val canCalculate = originalLoan > 0.0 && tenureMonths > 0

    fun pdfData() = SchedulePdf.Data(
        principal = originalLoan,
        annualRate = rate,
        tenureMonths = impact.originalTenureMonths.coerceAtLeast(tenureMonths),
        monthlyEmi = originalEmi,
        totalInterest = if (schedule.isNotEmpty()) newTotalInterest else originalTotalInterest,
        totalPayment = if (newTotalPayment > 0.0) newTotalPayment else totalPayment,
        extraPayment = impact.totalExtraPaid,
        finalTenureMonths = newTenure,
        monthsSaved = impact.monthsSaved,
        interestSaved = impact.interestSaved,
        rows = schedule
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(com.aksh.emicalculator.ui.components.CalcColors.Pale2, com.aksh.emicalculator.ui.components.CalcColors.Pale, com.aksh.emicalculator.ui.components.CalcColors.Card)
                )
            )
            .testTag("prepayment_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // ---------------------------------------------------------------- header
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tr(language, Strings.PREPAYMENT_TITLE),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = tr(language, ToolStrings.SAVINGS_SUB),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.syncPrepaymentWithCalculator()
                            hasCalculated = true
                        }
                        .testTag("sync_prepayment_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = tr(language, ExtraStrings.SYNC_LOAN),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ---------------------------------------------------------------- loan details
            Card(
                modifier = Modifier.fillMaxWidth().testTag("loan_details_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = tr(language, ToolStrings.LOAN_DETAILS),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tr(language, ToolStrings.ENTER_LOAN_INFO),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    ToolField(
                        title = tr(language, Strings.ORIGINAL_LOAN_AMOUNT),
                        value = originalLoan,
                        onValueChange = { viewModel.updatePrepaymentOriginalLoan(it) },
                        prefix = sym,
                        testTag = "prepayment_original_loan"
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            ToolField(
                                title = tr(language, Strings.INTEREST_RATE),
                                value = rate,
                                onValueChange = { viewModel.updatePrepaymentRate(it.coerceIn(0.0, 40.0)) },
                                suffix = "%",
                                isDecimal = true,
                                testTag = "prepayment_rate"
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            val displayTenure = if (isTenureInYears) (tenureMonths / 12.0) else tenureMonths.toDouble()
                            ToolField(
                                title = tr(language, Strings.ORIGINAL_LOAN_TENURE),
                                value = displayTenure,
                                onValueChange = {
                                    viewModel.updatePrepaymentTenure(it.toInt().coerceAtLeast(0), isTenureInYears)
                                },
                                suffix = if (isTenureInYears) tr(language, Strings.YEARS)
                                else tr(language, Strings.MONTHS),
                                testTag = "prepayment_original_tenure"
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.togglePrepaymentTenureUnit() },
                            modifier = Modifier.testTag("toggle_prepayment_tenure_btn")
                        ) {
                            Icon(
                                Icons.Default.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isTenureInYears) tr(language, Strings.MONTHS)
                                else tr(language, Strings.YEARS),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) {
                            ToolField(
                                title = tr(language, Strings.EMIS_ALREADY_PAID),
                                value = emisAlreadyPaid.toDouble(),
                                onValueChange = { viewModel.updateEmisAlreadyPaid(it.toInt()) },
                                testTag = "prepayment_emis_already_paid"
                            )
                        }
                        Box(Modifier.weight(1f)) {
                            val typeOptions = remember(language) {
                                listOf(
                                    PrepaymentType.MONTHLY_EXTRA to tr(language, Strings.MONTHLY_EXTRA),
                                    PrepaymentType.QUARTERLY to tr(language, ExtraStrings.PREP_QUARTERLY),
                                    PrepaymentType.HALF_YEARLY to tr(language, ExtraStrings.PREP_HALF),
                                    PrepaymentType.YEARLY to tr(language, ExtraStrings.PREP_YEARLY),
                                    PrepaymentType.ONE_TIME to tr(language, Strings.ONE_TIME),
                                    PrepaymentType.CUSTOM to tr(language, ExtraStrings.PREP_CUSTOM)
                                )
                            }
                            Text(
                                text = tr(language, ToolStrings.EXTRA_TYPE),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(5.dp))
                            ToolDropdown(
                                label = "",
                                options = typeOptions.map { it.second },
                                selectedIndex = typeOptions.indexOfFirst { it.first == prepType }.coerceAtLeast(0),
                                onSelect = { viewModel.updatePrepaymentType(typeOptions[it].first) }
                            )
                        }
                    }

                    if (prepType != PrepaymentType.CUSTOM) {
                        Spacer(Modifier.height(10.dp))
                        ToolField(
                            title = tr(language, Strings.EXTRA_PAYMENT),
                            value = extraAmount,
                            onValueChange = { viewModel.updatePrepaymentExtraAmount(it) },
                            prefix = sym,
                            suffix = when (prepType) {
                                PrepaymentType.MONTHLY_EXTRA -> "/mo"
                                PrepaymentType.QUARTERLY -> "/qtr"
                                PrepaymentType.HALF_YEARLY -> "/half"
                                PrepaymentType.YEARLY -> "/yr"
                                else -> ""
                            },
                            testTag = "prepayment_extra_amount"
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    TextButton(
                        onClick = { showAdvanced = !showAdvanced },
                        modifier = Modifier.testTag("advanced_options_btn")
                    ) {
                        Icon(
                            imageVector = if (showAdvanced) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = tr(language, ToolStrings.ADVANCED_OPTIONS),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showAdvanced) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrepChip(
                                label = tr(language, ExtraStrings.PREP_MODE_TENURE),
                                selected = prepMode == PrepaymentMode.REDUCE_TENURE
                            ) { viewModel.updatePrepaymentMode(PrepaymentMode.REDUCE_TENURE) }
                            PrepChip(
                                label = tr(language, ExtraStrings.PREP_MODE_EMI),
                                selected = prepMode == PrepaymentMode.REDUCE_EMI
                            ) { viewModel.updatePrepaymentMode(PrepaymentMode.REDUCE_EMI) }
                        }

                        if (prepType == PrepaymentType.CUSTOM) {
                            Spacer(Modifier.height(10.dp))
                            CustomPaymentsEditor(
                                language = language,
                                payments = customPayments,
                                maxMonth = (tenureMonths - emisAlreadyPaid).coerceAtLeast(1),
                                onAdd = { m, a -> viewModel.addCustomPayment(m, a) },
                                onRemove = { viewModel.removeCustomPayment(it) }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!canCalculate) {
                                Toast.makeText(
                                    context,
                                    tr(language, ToolStrings.ENTER_LOAN_INFO),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                hasCalculated = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("prepayment_calculate_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = tr(language, ToolStrings.CALCULATE),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            if (hasCalculated && canCalculate) {
                Spacer(Modifier.height(14.dp))

                // ------------------------------------------------------------ 4 metrics
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("prepayment_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricTile(
                                label = tr(language, ToolStrings.MONTHLY_EMI),
                                value = money(originalEmi),
                                icon = Icons.Default.Payments,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.weight(1f)
                            )
                            MetricTile(
                                label = tr(language, ToolStrings.TOTAL_INTEREST_LBL),
                                value = money(originalTotalInterest),
                                icon = Icons.Default.TrendingDown,
                                tint = Color(0xFFE11D48),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            MetricTile(
                                label = tr(language, ToolStrings.TOTAL_PAYMENT_LBL),
                                value = money(totalPayment),
                                icon = Icons.Default.AccountBalance,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.weight(1f)
                            )
                            MetricTile(
                                label = tr(language, ToolStrings.NEW_TENURE_LBL),
                                value = tenureText(newTenure, language),
                                icon = Icons.Default.Schedule,
                                tint = Color(0xFF0D9488),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.clearPrepayment()
                                    hasCalculated = false
                                    showAllRows = false
                                },
                                modifier = Modifier.weight(1f).testTag("clear_prepayment_btn"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    tr(language, ExtraStrings.CLEAR_BTN),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = {
                                    if (schedule.isEmpty()) {
                                        Toast.makeText(context, "Please calculate first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        SchedulePdf.downloadAndOpen(context, pdfData(), language)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("prepayment_pdf_btn"),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 6.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "PDF",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                            OutlinedButton(
                                onClick = {
                                    if (schedule.isEmpty()) {
                                        Toast.makeText(context, "Please calculate first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val shareText = buildString {
                                            append("💸 ${tr(language, Strings.PREPAYMENT_TITLE)}\n")
                                            append("${tr(language, ToolStrings.TOTAL_INTEREST_LBL)}: ${money(originalTotalInterest)}\n")
                                            append("${tr(language, ToolStrings.YOUR_SAVINGS)}: ${money(impact.interestSaved)}\n")
                                            append("${tr(language, Strings.TENURE_REDUCED_BY)}: ${impact.monthsSaved} ${tr(language, Strings.MONTHS)}\n")
                                        }
                                        SchedulePdf.sharePdf(context, pdfData(), language, shareText)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("prepayment_share_btn"),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    horizontal = 6.dp,
                                    vertical = 8.dp
                                )
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    tr(language, Strings.SHARE_BTN),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ------------------------------------------------------------ repayment schedule
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("repayment_schedule_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = tr(language, ToolStrings.REPAYMENT_SCHEDULE),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${schedule.size} ${tr(language, ToolStrings.MONTHS_LBL)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        TableHeader(language)

                        val visibleRows = if (showAllRows) schedule else schedule.take(6)
                        visibleRows.forEachIndexed { index, row ->
                            ScheduleRow(row = row, zebra = index % 2 == 1)
                        }

                        if (schedule.size > 6) {
                            TextButton(
                                onClick = { showAllRows = !showAllRows },
                                modifier = Modifier.fillMaxWidth().testTag("schedule_show_more_btn")
                            ) {
                                Icon(
                                    imageVector = if (showAllRows) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (showAllRows) tr(language, ToolStrings.SHOW_LESS)
                                    else tr(language, ToolStrings.SHOW_MORE),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ------------------------------------------------------------ savings card
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("your_savings_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = com.aksh.emicalculator.ui.components.CalcColors.GreenLight)
                ) {
                    Column(Modifier.fillMaxWidth().padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(com.aksh.emicalculator.ui.components.CalcColors.Green.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = tr(language, ToolStrings.YOUR_SAVINGS),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = com.aksh.emicalculator.ui.components.CalcColors.GreenDark
                                )
                                Text(
                                    text = tr(language, ToolStrings.SAVINGS_SUB),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = tr(language, Strings.TOTAL_INTEREST_SAVED),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF047857)
                        )
                        Text(
                            text = money(impact.interestSaved),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = com.aksh.emicalculator.ui.components.CalcColors.GreenDark,
                            modifier = Modifier.testTag("interest_saved_amount_text")
                        )

                        Spacer(Modifier.height(10.dp))
                        Divider(color = Color(0xFFA7F3D0))
                        Spacer(Modifier.height(10.dp))

                        SavingsRow(
                            label = tr(language, Strings.TENURE_REDUCED_BY),
                            value = tenureText(impact.monthsSaved, language),
                            icon = Icons.Default.Schedule
                        )
                        Spacer(Modifier.height(7.dp))
                        SavingsRow(
                            label = tr(language, ExtraStrings.PREP_TOTAL_EXTRA),
                            value = money(impact.totalExtraPaid),
                            icon = Icons.Default.Payments
                        )
                        if (prepMode == PrepaymentMode.REDUCE_EMI && impact.newEmi > 0.0) {
                            Spacer(Modifier.height(7.dp))
                            SavingsRow(
                                label = tr(language, ExtraStrings.PREP_NEW_EMI),
                                value = money(impact.newEmi),
                                icon = Icons.Default.TrendingDown,
                                testTag = "new_emi_text"
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
            } else {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = tr(language, ToolStrings.ENTER_LOAN_INFO),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// --------------------------------------------------------------------------- small pieces

@Composable
private fun MetricTile(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.fillMaxWidth().padding(11.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(tint.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TableHeader(language: AppLanguage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(1.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(tr(language, ToolStrings.PDF_NO), 0.45f, Color.White, FontWeight.Bold, TextAlign.Center)
        TableCell(tr(language, ToolStrings.MONTH_SHORT), 0.9f, Color.White, FontWeight.Bold, TextAlign.Center)
        TableCell(tr(language, ToolStrings.PRINCIPAL_LBL), 1.1f, Color.White, FontWeight.Bold, TextAlign.End)
        TableCell(tr(language, ToolStrings.TOTAL_INTEREST_LBL), 1.1f, Color.White, FontWeight.Bold, TextAlign.End)
        TableCell(tr(language, ToolStrings.BALANCE_LBL), 1.2f, Color.White, FontWeight.Bold, TextAlign.End)
    }
}

@Composable
private fun ScheduleRow(row: PrepaymentMonthRow, zebra: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (zebra) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color.Transparent
            )
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val base = MaterialTheme.colorScheme.onSurface
        TableCell(row.monthNumber.toString(), 0.45f, base, FontWeight.Medium, TextAlign.Center)
        TableCell("M${row.monthNumber}", 0.9f, base, FontWeight.Medium, TextAlign.Center)
        TableCell(compactMoney(row.principalPaid), 1.1f, base, FontWeight.Medium, TextAlign.End)
        TableCell(compactMoney(row.interestPaid), 1.1f, base, FontWeight.Medium, TextAlign.End)
        TableCell(compactMoney(row.closingBalance), 1.2f, base, FontWeight.Bold, TextAlign.End)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TableCell(
    text: String,
    weight: Float,
    color: Color,
    fontWeight: FontWeight,
    align: TextAlign
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight).padding(horizontal = 2.dp),
        fontSize = 9.sp,
        lineHeight = 11.sp,
        fontWeight = fontWeight,
        color = color,
        textAlign = align,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun SavingsRow(
    label: String,
    value: String,
    icon: ImageVector,
    testTag: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF047857),
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 2
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = com.aksh.emicalculator.ui.components.CalcColors.GreenDark,
            maxLines = 1,
            modifier = if (testTag != null) Modifier.testTag(testTag) else Modifier
        )
    }
}

@Composable
private fun PrepChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CustomPaymentsEditor(
    language: AppLanguage,
    payments: List<com.aksh.emicalculator.data.CustomPrepayment>,
    maxMonth: Int,
    onAdd: (Int, Double) -> Unit,
    onRemove: (Int) -> Unit
) {
    var monthText by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("custom_payments_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(
                text = tr(language, ExtraStrings.PREP_CUSTOM_HELP) + " (1-$maxMonth)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = monthText,
                    onValueChange = { v -> monthText = v.filter { it.isDigit() }.take(3) },
                    label = { Text(tr(language, ExtraStrings.PREP_MONTH)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(0.8f).testTag("custom_month_input")
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { v -> amountText = v.filter { it.isDigit() || it == '.' }.take(12) },
                    label = { Text(tr(language, ExtraStrings.PREP_AMOUNT)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1.2f).testTag("custom_amount_input")
                )
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val m = monthText.toIntOrNull()
                    val a = amountText.toDoubleOrNull()
                    if (m != null && a != null && m >= 1 && a > 0.0) {
                        onAdd(m, a)
                        monthText = ""
                        amountText = ""
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("custom_add_btn")
            ) {
                Text(tr(language, ExtraStrings.PREP_ADD))
            }

            payments.forEach { p ->
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tr(language, ExtraStrings.PREP_MONTH) + " ${p.month}:  " + money(p.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(onClick = { onRemove(p.month) }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}
