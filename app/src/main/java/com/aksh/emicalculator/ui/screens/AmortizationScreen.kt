package com.aksh.emicalculator.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.AmortizationMonth
import com.aksh.emicalculator.data.AmortizationYear
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.theme.AmberInterest
import com.aksh.emicalculator.ui.theme.EmeraldLight
import com.aksh.emicalculator.data.PrepaymentMonthRow
import com.aksh.emicalculator.utils.PdfExportUtils
import com.aksh.emicalculator.utils.SchedulePdf
import com.aksh.emicalculator.viewmodel.EmiViewModel

@Composable
fun AmortizationScreen(
    viewModel: EmiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.currentLanguage.collectAsState()
    val principal by viewModel.principal.collectAsState()
    val emiResult by viewModel.emiResult.collectAsState()
    val (yearsList, monthsList) = viewModel.amortizationSchedule.collectAsState().value

    var isMonthlyView by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("amortization_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = TranslationRepository.getString(language, Strings.SCHEDULE_TITLE),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // PDF Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldLight.copy(alpha = 0.2f),
                        modifier = Modifier
                            .testTag("download_pdf_schedule_btn")
                            .clickable {
                                // Designed A4 report with the dynamic 1ST / 2ND / 3RD badge.
                                val rows = monthsList.map { m ->
                                    PrepaymentMonthRow(
                                        monthNumber = m.monthNumber,
                                        openingBalance = m.openingBalance,
                                        principalPaid = m.principalComponent,
                                        interestPaid = m.interestComponent,
                                        emi = m.emi,
                                        extraPaid = 0.0,
                                        closingBalance = m.closingBalance
                                    )
                                }
                                SchedulePdf.downloadAndOpen(
                                    context = context,
                                    data = SchedulePdf.Data(
                                        principal = principal,
                                        annualRate = emiResult.annualRatePercent,
                                        tenureMonths = emiResult.tenureMonths,
                                        monthlyEmi = emiResult.monthlyEmi,
                                        totalInterest = emiResult.totalInterest,
                                        totalPayment = emiResult.totalPayment,
                                        extraPayment = 0.0,
                                        finalTenureMonths = emiResult.tenureMonths,
                                        monthsSaved = 0,
                                        interestSaved = 0.0,
                                        rows = rows
                                    ),
                                    language = language
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF",
                                tint = EmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PDF",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }

                    // Share Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .testTag("share_summary_btn")
                            .clickable {
                                PdfExportUtils.shareLoanDetails(
                                    context = context,
                                    principal = principal,
                                    rate = emiResult.annualRatePercent,
                                    tenureMonths = emiResult.tenureMonths,
                                    isTenureInYears = true,
                                    emiResult = emiResult,
                                    language = language
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = TranslationRepository.getString(language, Strings.SHARE_BTN),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prominent Baseline Loan Amount Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("schedule_loan_summary_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = TranslationRepository.getString(language, Strings.PRINCIPAL_AMOUNT),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = EmiMathUtils.formatCurrency(principal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = TranslationRepository.getString(language, Strings.LOAN_TENURE),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${emiResult.tenureMonths / 12} Yrs (${emiResult.tenureMonths} Mo)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // View Mode Tab (Yearly vs Monthly)
            TabRow(
                selectedTabIndex = if (isMonthlyView) 1 else 0,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("schedule_tab_row")
            ) {
                Tab(
                    selected = !isMonthlyView,
                    onClick = { isMonthlyView = false },
                    text = {
                        Text(
                            text = TranslationRepository.getString(language, Strings.YEARLY_VIEW),
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = isMonthlyView,
                    onClick = { isMonthlyView = true },
                    text = {
                        Text(
                            text = TranslationRepository.getString(language, Strings.MONTHLY_VIEW),
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule List
            if (!isMonthlyView) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("yearly_schedule_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(yearsList) { item ->
                        YearlyScheduleCard(
                            item = item,
                            initialPrincipal = principal,
                            language = language
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("monthly_schedule_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(monthsList) { item ->
                        MonthlyScheduleCard(
                            item = item,
                            language = language
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun YearlyScheduleCard(
    item: AmortizationYear,
    initialPrincipal: Double,
    language: com.aksh.emicalculator.data.AppLanguage
) {
    val progress = if (initialPrincipal > 0.0) {
        ((initialPrincipal - item.closingBalance) / initialPrincipal).toFloat().coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${TranslationRepository.getString(language, Strings.YEAR)} ${item.yearNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Paid: ${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = EmeraldLight,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = TranslationRepository.getString(language, Strings.PRINCIPAL_WITHOUT_INTEREST),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = EmiMathUtils.formatCurrency(item.principalPaid),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = TranslationRepository.getString(language, Strings.TOTAL_INTEREST),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = EmiMathUtils.formatCurrency(item.interestPaid),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberInterest
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = TranslationRepository.getString(language, Strings.BALANCE),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = EmiMathUtils.formatCurrency(item.closingBalance),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
internal fun MonthlyScheduleCard(
    item: AmortizationMonth,
    language: com.aksh.emicalculator.data.AppLanguage
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Mo ${item.monthNumber} (Yr ${item.yearNumber})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "EMI: ${EmiMathUtils.formatCurrency(item.emi)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row {
                    Text(
                        text = "P (No Int): ${EmiMathUtils.formatCurrency(item.principalComponent)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Int: ${EmiMathUtils.formatCurrency(item.interestComponent)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AmberInterest
                    )
                }
                Text(
                    text = "Bal: ${EmiMathUtils.formatCurrency(item.closingBalance)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
