package com.aksh.emicalculator.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.AvailableCurrencies
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.HistoryEntry
import com.aksh.emicalculator.data.LoanCategory
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.viewmodel.EmiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatDateTime(ts: Long): String =
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(ts))

private fun money(v: Double, e: HistoryEntry) =
    EmiMathUtils.formatCurrency(v, AvailableCurrencies.firstOrNull { it.code == e.currencyCode } ?: EmiMathUtils.activeCurrency)

private fun tenureText(months: Int, language: AppLanguage): String {
    val y = months / 12
    val m = months % 12
    val yl = TranslationRepository.getString(language, Strings.YEARS)
    val ml = TranslationRepository.getString(language, Strings.MONTHS)
    val base = when {
        y > 0 && m > 0 -> "$y $yl $m $ml"
        y > 0 -> "$y $yl"
        else -> "$m $ml"
    }
    return if (y > 0) "$base ($months $ml)" else base
}

/** Full-screen History: list of saved calculations -> tap one to see its complete schedule. */
@Composable
fun HistoryScreen(viewModel: EmiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val history by viewModel.history.collectAsState()
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selected = history.firstOrNull { it.id == selectedId }

    BackHandler {
        if (selected != null) selectedId = null else viewModel.setShowHistory(false)
    }

    Surface(modifier = Modifier.fillMaxSize().testTag("history_screen"), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (selected != null) selectedId = null else viewModel.setShowHistory(false) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = TranslationRepository.getString(language, ExtraStrings.HISTORY),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (selected != null) {
                    IconButton(onClick = { viewModel.deleteHistory(selected.id); selectedId = null }) {
                        Icon(Icons.Default.Delete, contentDescription = TranslationRepository.getString(language, ExtraStrings.HISTORY_DELETE))
                    }
                } else if (history.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearHistory() }, modifier = Modifier.testTag("history_clear_btn")) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = TranslationRepository.getString(language, ExtraStrings.HISTORY_CLEAR))
                    }
                }
            }

            if (selected != null) {
                HistoryDetail(
                    selected, language,
                    onOpen = { viewModel.openHistoryInCalculator(selected) },
                    onNoteChange = { viewModel.updateHistoryNote(selected.id, it) }
                )
            } else if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.height(56.dp).width(56.dp))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = TranslationRepository.getString(language, ExtraStrings.HISTORY_EMPTY),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().navigationBarsPadding().testTag("history_list"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history, key = { it.id }) { e ->
                        HistoryItem(e, language) { selectedId = e.id }
                    }
                }
            }
        }
    }
}

@Composable
private fun categoryLabel(e: HistoryEntry, language: AppLanguage): String {
    val cat = LoanCategory.values().firstOrNull { it.name == e.category } ?: LoanCategory.HOME
    return TranslationRepository.getString(language, cat.titleKey)
}

@Composable
private fun HistoryItem(e: HistoryEntry, language: AppLanguage, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    if (e.note.isNotBlank()) categoryLabel(e, language) + " - " + e.note else categoryLabel(e, language),
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,
                    maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(formatDateTime(e.id), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = TranslationRepository.getString(language, Strings.LOAN_AMOUNT) + ": " + money(e.principal, e),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = TranslationRepository.getString(language, Strings.MONTHLY_EMI) + ": " + money(e.emi, e),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${e.annualRate}%  •  " + tenureText(e.tenureMonths, language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryDetail(e: HistoryEntry, language: AppLanguage, onOpen: () -> Unit, onNoteChange: (String) -> Unit) {
    var note by remember(e.id) { mutableStateOf(e.note) }
    var monthly by rememberSaveable { mutableStateOf(false) }
    val schedule = remember(e.id) {
        EmiMathUtils.generateAmortizationSchedule(e.principal, e.annualRate, e.tenureMonths)
    }
    val (years, months) = schedule

    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().testTag("history_detail"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(categoryLabel(e, language), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(formatDateTime(e.id), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(10.dp))
                    // Free text (letters + numbers) - e.g. loan name or account number
                    androidx.compose.material3.OutlinedTextField(
                        value = note,
                        onValueChange = { note = it.take(60); onNoteChange(note) },
                        label = { Text(TranslationRepository.getString(language, ExtraStrings.NOTE_LABEL)) },
                        placeholder = { Text(TranslationRepository.getString(language, ExtraStrings.NOTE_HINT)) },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("history_note_input")
                    )
                    Spacer(Modifier.height(10.dp))
                    if (e.downPayment > 0.0) {
                        DetailRow(TranslationRepository.getString(language, ExtraStrings.PRICE_HOME).let { if (e.category == "CAR") TranslationRepository.getString(language, ExtraStrings.PRICE_CAR) else it }, money(e.assetPrice, e))
                        DetailRow(TranslationRepository.getString(language, ExtraStrings.DOWN_PAYMENT), money(e.downPayment, e))
                    }
                    DetailRow(TranslationRepository.getString(language, Strings.LOAN_AMOUNT), money(e.principal, e))
                    DetailRow(TranslationRepository.getString(language, Strings.INTEREST_RATE), "${e.annualRate}%")
                    DetailRow(TranslationRepository.getString(language, Strings.LOAN_TENURE), tenureText(e.tenureMonths, language))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow(TranslationRepository.getString(language, Strings.MONTHLY_EMI), money(e.emi, e), bold = true)
                    DetailRow(TranslationRepository.getString(language, Strings.TOTAL_INTEREST), money(e.totalInterest, e))
                    DetailRow(TranslationRepository.getString(language, Strings.TOTAL_PAYMENT), money(e.totalPayment, e), bold = true)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onOpen, modifier = Modifier.fillMaxWidth().testTag("history_open_calc_btn")) {
                        Text(TranslationRepository.getString(language, ExtraStrings.HISTORY_OPEN_CALC))
                    }
                }
            }
        }
        item {
            Text(
                TranslationRepository.getString(language, ExtraStrings.HISTORY_SCHEDULE),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp)
            )
            TabRow(selectedTabIndex = if (monthly) 1 else 0, containerColor = MaterialTheme.colorScheme.background) {
                Tab(selected = !monthly, onClick = { monthly = false }, text = { Text(TranslationRepository.getString(language, Strings.YEARLY_VIEW), maxLines = 1) })
                Tab(selected = monthly, onClick = { monthly = true }, text = { Text(TranslationRepository.getString(language, Strings.MONTHLY_VIEW), maxLines = 1) })
            }
        }
        if (!monthly) {
            items(years) { y -> YearlyScheduleCard(item = y, initialPrincipal = e.principal, language = language) }
        } else {
            items(months) { m -> MonthlyScheduleCard(item = m, language = language) }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, bold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium, modifier = Modifier.padding(start = 8.dp))
    }
}
