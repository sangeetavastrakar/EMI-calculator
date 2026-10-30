package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.LoanOfferInput
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.BankSpinnerDropdown
import com.aksh.emicalculator.ui.components.CalcActionsRow
import com.aksh.emicalculator.ui.components.CalcCard
import com.aksh.emicalculator.ui.components.CalcColors
import com.aksh.emicalculator.ui.components.CalcGhostButton
import com.aksh.emicalculator.ui.components.CalcPageGradient
import com.aksh.emicalculator.utils.PdfExportUtils
import com.aksh.emicalculator.viewmodel.EmiViewModel

/** One comparison form: both offers are edited and compared on the same page. */
@Composable
fun CompareLoansScreen(viewModel: EmiViewModel, modifier: Modifier = Modifier) {
    val language by viewModel.currentLanguage.collectAsState()
    val a by viewModel.compareLoanA.collectAsState()
    val b by viewModel.compareLoanB.collectAsState()
    val result by viewModel.comparisonResult.collectAsState()
    val currency by viewModel.selectedCurrency.collectAsState()
    val context = LocalContext.current
    fun t(key: String) = TranslationRepository.getString(language, key)
    fun money(v: Double) = EmiMathUtils.formatCurrency(v, currency)
    fun updateA(v: LoanOfferInput) = viewModel.updateLoanA(v.principal, v.annualRatePercent, v.tenureMonths, v.processingFeePercent)
    fun updateB(v: LoanOfferInput) = viewModel.updateLoanB(v.principal, v.annualRatePercent, v.tenureMonths, v.processingFeePercent)
    val hindi = language == AppLanguage.HINDI
    val title = t(Strings.COMPARE_TITLE)
    val feeLabel = if (hindi) "प्रोसेसिंग फीस (%)" else "Processing fee (%)"
    val costLabel = if (hindi) "कुल भुगतान + फीस" else "Total payment + fees"
    val ready = a.principal > 0 && b.principal > 0 && a.tenureMonths > 0 && b.tenureMonths > 0
    val costA = result.offerA.totalPayment + result.offerA.processingFeeAmount
    val costB = result.offerB.totalPayment + result.offerB.processingFeeAmount
    val samePrincipal = kotlin.math.abs(a.principal - b.principal) < 0.01
    val better = when (result.betterOfferIndex) {
        0 -> result.offerA.name
        1 -> result.offerB.name
        else -> t(ExtraStrings.BOTH_EQUAL)
    }
    val reportRows = listOf(
        "A • ${t(Strings.LOAN_AMOUNT)}" to money(a.principal),
        "B • ${t(Strings.LOAN_AMOUNT)}" to money(b.principal),
        "A • ${t(Strings.INTEREST_RATE)}" to "${a.annualRatePercent}%",
        "B • ${t(Strings.INTEREST_RATE)}" to "${b.annualRatePercent}%",
        "A • ${t(Strings.LOAN_TENURE)}" to "${a.tenureMonths} ${t(Strings.MONTHS)}",
        "B • ${t(Strings.LOAN_TENURE)}" to "${b.tenureMonths} ${t(Strings.MONTHS)}",
        "A • ${t(Strings.MONTHLY_EMI)}" to money(result.offerA.monthlyEmi),
        "B • ${t(Strings.MONTHLY_EMI)}" to money(result.offerB.monthlyEmi),
        "A • ${t(Strings.TOTAL_INTEREST)}" to money(result.offerA.totalInterest),
        "B • ${t(Strings.TOTAL_INTEREST)}" to money(result.offerB.totalInterest),
        "A • $feeLabel" to "${a.processingFeePercent}% (${money(result.offerA.processingFeeAmount)})",
        "B • $feeLabel" to "${b.processingFeePercent}% (${money(result.offerB.processingFeeAmount)})",
        "A • $costLabel" to money(costA), "B • $costLabel" to money(costB)
    )
    val comparisonNote = if (!samePrincipal)
        if (hindi) "ऋण राशि अलग है। सही तुलना के लिए दोनों में समान राशि रखें।"
        else "Loan amounts differ. Use the same amount for a like-for-like comparison."
    else if (result.betterOfferIndex == -1) t(ExtraStrings.BOTH_EQUAL)
    else t(ExtraStrings.IS_BETTER).replace("%s", better)

    Box(modifier.fillMaxSize().background(CalcPageGradient).testTag("compare_loans_screen"), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 600.dp).fillMaxSize().verticalScroll(rememberScrollState())
            .padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CalcCard {
                Text(title, color = CalcColors.BlueDark, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(t(Strings.LOAN_A), color = CalcColors.Blue, fontWeight = FontWeight.Bold)
                        BankSpinnerDropdown(a.name, language, CalcColors.Blue,
                            onBankSelected = { name, rate -> viewModel.updateLoanAName(name); if (rate != null) updateA(a.copy(annualRatePercent = rate)) },
                            testTagPrefix = "loan_a")
                    }
                    Column(Modifier.weight(1f)) {
                        Text(t(Strings.LOAN_B), color = CalcColors.Purple, fontWeight = FontWeight.Bold)
                        BankSpinnerDropdown(b.name, language, CalcColors.Purple,
                            onBankSelected = { name, rate -> viewModel.updateLoanBName(name); if (rate != null) updateB(b.copy(annualRatePercent = rate)) },
                            testTagPrefix = "loan_b")
                    }
                }
                Spacer(Modifier.height(8.dp))
                CalcGhostButton(if (hindi) "A की details → B" else "Copy A details → B",
                    onClick = { updateB(a.copy(name = b.name)) }, modifier = Modifier.fillMaxWidth(), tag = "copy_loan_a_to_b")
                CompareInputRow(t(Strings.LOAN_AMOUNT), a.principal, b.principal, currency.symbol, "", false, "amount",
                    { updateA(a.copy(principal = it.coerceIn(0.0, 100000000.0))) },
                    { updateB(b.copy(principal = it.coerceIn(0.0, 100000000.0))) })
                CompareInputRow(t(Strings.INTEREST_RATE), a.annualRatePercent, b.annualRatePercent, "", "%", true, "rate",
                    { updateA(a.copy(annualRatePercent = it.coerceIn(0.0, 36.0))) },
                    { updateB(b.copy(annualRatePercent = it.coerceIn(0.0, 36.0))) })
                CompareInputRow(t(Strings.LOAN_TENURE), a.tenureMonths.toDouble(), b.tenureMonths.toDouble(), "", t(Strings.MONTHS), false, "tenure",
                    { updateA(a.copy(tenureMonths = it.toInt().coerceIn(0, 480))) },
                    { updateB(b.copy(tenureMonths = it.toInt().coerceIn(0, 480))) })
                CompareInputRow(feeLabel, a.processingFeePercent, b.processingFeePercent, "", "%", true, "fee",
                    { updateA(a.copy(processingFeePercent = it.coerceIn(0.0, 20.0))) },
                    { updateB(b.copy(processingFeePercent = it.coerceIn(0.0, 20.0))) })
            }
            CalcCard {
                if (ready) {
                    Text(comparisonNote, color = CalcColors.GreenDark, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    CompareResultRow("", "A", "B")
                    CompareResultRow(t(Strings.MONTHLY_EMI), money(result.offerA.monthlyEmi), money(result.offerB.monthlyEmi))
                    CompareResultRow(t(Strings.TOTAL_INTEREST), money(result.offerA.totalInterest), money(result.offerB.totalInterest))
                    CompareResultRow(costLabel, money(costA), money(costB))
                    Spacer(Modifier.height(8.dp))
                    Text(t(Strings.DIFFERENCE_MONTHLY) + ": " + money(result.emiDifferenceMonthly),
                        color = CalcColors.BlueDark, fontSize = 12.sp)
                    Text((if (hindi) "कुल लागत का अंतर: " else "Total cost difference: ") + money(kotlin.math.abs(costA - costB)),
                        color = CalcColors.BlueDark, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                    CalcActionsRow(language, { viewModel.clearCompareLoans() },
                        onPdf = { PdfExportUtils.downloadAndOpenGenericPdf(context, title, "A: ${a.name} • B: ${b.name}", reportRows, comparisonNote) },
                        onShare = {
                            val file = PdfExportUtils.generateGenericPdf(context, title, "A: ${a.name} • B: ${b.name}", reportRows, comparisonNote)
                            if (file != null) PdfExportUtils.shareGeneric(context, title, comparisonNote, file)
                        })
                } else {
                    Text(if (hindi) "तुलना के लिए दोनों ऋण की राशि और अवधि भरें।" else "Enter the amount and tenure for both loans to compare.", color = CalcColors.Muted)
                    Spacer(Modifier.height(8.dp))
                    CalcGhostButton(t(ExtraStrings.CLEAR_BTN), { viewModel.clearCompareLoans() }, tag = "clear_compare_btn")
                }
            }
        }
    }
}

@Composable
private fun CompareInputRow(label: String, a: Double, b: Double, prefix: String, suffix: String,
                            decimal: Boolean, tag: String, onA: (Double) -> Unit, onB: (Double) -> Unit) {
    Spacer(Modifier.height(7.dp))
    Text(label, fontSize = 12.sp, color = CalcColors.BlueDark, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ToolField("A", a, onA, Modifier.weight(1f), prefix, suffix, decimal, "loan_a_${tag}_edit_text")
        ToolField("B", b, onB, Modifier.weight(1f), prefix, suffix, decimal, "loan_b_${tag}_edit_text")
    }
}

@Composable
private fun CompareResultRow(label: String, a: String, b: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, Modifier.weight(1.2f), color = CalcColors.Muted, fontSize = 11.sp)
        Text(a, Modifier.weight(1f), color = CalcColors.BlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(b, Modifier.weight(1f), color = CalcColors.BlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
