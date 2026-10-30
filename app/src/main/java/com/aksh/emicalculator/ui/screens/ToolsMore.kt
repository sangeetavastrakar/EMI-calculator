package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.FinanceMath
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.EditableSliderInputCard
import java.util.Locale

private fun pct(x: Double) = String.format(Locale.US, "%.2f%%", x)
private fun money(x: Double) = EmiMathUtils.formatCurrency(x)
private fun tr(language: AppLanguage, key: String) = TranslationRepository.getString(language, key)

// ---------------------------------------------------------------- SIP / Lump sum
@Composable
fun SipScreen(language: AppLanguage, initialLumpsum: Boolean = false) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var lumpsum by rememberSaveable { mutableStateOf(initialLumpsum) }
    var monthly by rememberSaveable { mutableStateOf(0.0) }
    var lump by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }

    val y = years.toInt()
    val invested = if (lumpsum) lump else monthly * y * 12
    val total = if (lumpsum) FinanceMath.lumpsumFutureValue(lump, rate, y)
    else FinanceMath.sipFutureValue(monthly, rate, y)
    val returns = (total - invested).coerceAtLeast(0.0)

    ToolColumn {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
            ToolChip(tr(language, ExtraStrings.SIP_MONTHLY), !lumpsum, "sip_mode_monthly") { lumpsum = false }
            ToolChip(tr(language, ExtraStrings.SIP_LUMPSUM), lumpsum, "sip_mode_lump") { lumpsum = true }
        }

        Spacer(Modifier.height(14.dp))
        if (lumpsum) {
            EditableSliderInputCard(
                title = tr(language, ExtraStrings.LUMP_AMOUNT),
                value = lump, valueRange = 0f..10000000f, stepSize = 5000f, prefix = sym,
                quickChips = listOf("${sym}50K" to 50000.0, "${sym}1L" to 100000.0, "${sym}5L" to 500000.0, "${sym}10L" to 1000000.0),
                onValueChange = { lump = it }, testTagPrefix = "sip_lump"
            )
        } else {
            EditableSliderInputCard(
                title = tr(language, ExtraStrings.SIP_AMOUNT),
                value = monthly, valueRange = 0f..500000f, stepSize = 500f, prefix = sym,
                quickChips = listOf("${sym}1K" to 1000.0, "${sym}5K" to 5000.0, "${sym}10K" to 10000.0, "${sym}25K" to 25000.0),
                onValueChange = { monthly = it }, testTagPrefix = "sip_monthly"
            )
        }
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.EXPECTED_RETURN),
            value = rate, valueRange = 0f..30f, stepSize = 0.5f, suffix = "%", isDecimal = true,
            quickChips = listOf("8%" to 8.0, "10%" to 10.0, "12%" to 12.0, "15%" to 15.0),
            onValueChange = { rate = it.coerceIn(0.0, 40.0) }, testTagPrefix = "sip_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.PERIOD_YEARS),
            value = years, valueRange = 0f..40f, stepSize = 1f,
            suffix = " " + tr(language, Strings.YEARS),
            quickChips = listOf("5" to 5.0, "10" to 10.0, "15" to 15.0, "20" to 20.0, "30" to 30.0),
            onValueChange = { years = it.coerceIn(0.0, 50.0) }, testTagPrefix = "sip_years"
        )

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.TOTAL_VALUE),
            mainValue = money(total),
            rows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.EST_RETURNS) to money(returns)
            ),
            splitFirst = invested,
            splitSecond = returns,
            tag = "sip_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_SIP),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.EST_RETURNS) to money(returns),
                tr(language, ExtraStrings.TOTAL_VALUE) to money(total)
            ),
            shareText = buildString {
                append("📈 ${tr(language, ExtraStrings.TOOL_SIP)}\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.INVESTED_AMOUNT)}: ${money(invested)}\n")
                append("${tr(language, ExtraStrings.EST_RETURNS)}: ${money(returns)}\n")
                append("${tr(language, ExtraStrings.TOTAL_VALUE)}: ${money(total)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                monthly = 0.0
                lump = 0.0
                rate = 0.0
                years = 0.0
            }
        )
        NoteText(tr(language, ExtraStrings.INVEST_NOTE))
    }
}

// ---------------------------------------------------------------- FD
@Composable
fun FdScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var amount by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var months by rememberSaveable { mutableStateOf(0.0) }
    var compounding by rememberSaveable { mutableStateOf(4) }

    val m = months.toInt()
    val maturity = FinanceMath.fdMaturity(amount, rate, m, compounding)
    val interest = (maturity - amount).coerceAtLeast(0.0)
    val yl = tr(language, Strings.YEARS)
    val ml = tr(language, Strings.MONTHS)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_AMOUNT),
            value = amount, valueRange = 0f..10000000f, stepSize = 5000f, prefix = sym,
            quickChips = listOf("${sym}25K" to 25000.0, "${sym}50K" to 50000.0, "${sym}1L" to 100000.0, "${sym}5L" to 500000.0, "${sym}10L" to 1000000.0),
            onValueChange = { amount = it }, testTagPrefix = "fd_amount"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_RATE),
            value = rate, valueRange = 0f..15f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            quickChips = listOf("6.5%" to 6.5, "7%" to 7.0, "7.5%" to 7.5, "8%" to 8.0),
            onValueChange = { rate = it.coerceIn(0.0, 25.0) }, testTagPrefix = "fd_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_TENURE),
            value = months, valueRange = 0f..120f, stepSize = 1f, suffix = " $ml",
            quickChips = listOf("6 M" to 6.0, "12 M" to 12.0, "24 M" to 24.0, "36 M" to 36.0, "60 M" to 60.0),
            onValueChange = { months = it.coerceIn(0.0, 240.0) }, testTagPrefix = "fd_tenure"
        )
        Spacer(Modifier.height(14.dp))
        Text(
            tr(language, ExtraStrings.FD_COMPOUNDING),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                12 to ExtraStrings.FD_MONTHLY, 4 to ExtraStrings.FD_QUARTERLY, 2 to ExtraStrings.FD_HALF,
                1 to ExtraStrings.FD_YEARLY, 0 to ExtraStrings.FD_SIMPLE
            ).forEach { (n, key) ->
                ToolChip(tr(language, key), compounding == n, "fd_comp_$n") { compounding = n }
            }
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.MATURITY_VALUE),
            mainValue = money(maturity),
            rows = listOf(
                tr(language, ExtraStrings.FD_AMOUNT) to money(amount),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest)
            ),
            splitFirst = amount,
            splitSecond = interest,
            tag = "fd_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_FD),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.FD_AMOUNT) to money(amount),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest),
                tr(language, ExtraStrings.MATURITY_VALUE) to money(maturity)
            ),
            shareText = buildString {
                append("🏦 ${tr(language, ExtraStrings.TOOL_FD)}\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.FD_AMOUNT)}: ${money(amount)}\n")
                append("${tr(language, ExtraStrings.INTEREST_EARNED)}: ${money(interest)}\n")
                append("${tr(language, ExtraStrings.MATURITY_VALUE)}: ${money(maturity)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                amount = 0.0
                rate = 0.0
                months = 0.0
                compounding = 4
            }
        )
        NoteText(tr(language, ExtraStrings.INVEST_NOTE))
    }
}

// ---------------------------------------------------------------- RD
@Composable
fun RdScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var monthly by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var months by rememberSaveable { mutableStateOf(0.0) }
    val m = months.toInt()
    val maturity = FinanceMath.rdMaturity(monthly, rate, m)
    val invested = monthly * m
    val interest = (maturity - invested).coerceAtLeast(0.0)
    val ml = tr(language, Strings.MONTHS)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.RD_MONTHLY), value = monthly, valueRange = 0f..200000f, stepSize = 500f, prefix = sym,
            quickChips = listOf("${sym}1K" to 1000.0, "${sym}5K" to 5000.0, "${sym}10K" to 10000.0, "${sym}25K" to 25000.0),
            onValueChange = { monthly = it }, testTagPrefix = "rd_monthly"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_RATE), value = rate, valueRange = 0f..12f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            quickChips = listOf("6%" to 6.0, "6.5%" to 6.5, "7%" to 7.0, "7.5%" to 7.5),
            onValueChange = { rate = it.coerceIn(0.0, 25.0) }, testTagPrefix = "rd_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_TENURE), value = months, valueRange = 0f..120f, stepSize = 1f, suffix = " $ml",
            quickChips = listOf("12 M" to 12.0, "24 M" to 24.0, "36 M" to 36.0, "60 M" to 60.0, "120 M" to 120.0),
            onValueChange = { months = it.coerceIn(0.0, 240.0) }, testTagPrefix = "rd_tenure"
        )

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.MATURITY_VALUE),
            mainValue = money(maturity),
            rows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest)
            ),
            splitFirst = invested,
            splitSecond = interest,
            tag = "rd_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_RD),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest),
                tr(language, ExtraStrings.MATURITY_VALUE) to money(maturity)
            ),
            shareText = buildString {
                append("🏦 ${tr(language, ExtraStrings.TOOL_RD)}\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.INVESTED_AMOUNT)}: ${money(invested)}\n")
                append("${tr(language, ExtraStrings.INTEREST_EARNED)}: ${money(interest)}\n")
                append("${tr(language, ExtraStrings.MATURITY_VALUE)}: ${money(maturity)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                monthly = 0.0
                rate = 0.0
                months = 0.0
            }
        )
        NoteText(tr(language, ExtraStrings.INVEST_NOTE))
    }
}

// ---------------------------------------------------------------- PPF
@Composable
fun PpfScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var yearly by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }
    val y = years.toInt()
    val deposit = yearly.coerceAtMost(150000.0)
    val maturity = FinanceMath.ppfMaturity(deposit, rate, y)
    val invested = deposit * y
    val interest = (maturity - invested).coerceAtLeast(0.0)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.PPF_YEARLY), value = deposit, valueRange = 0f..150000f, stepSize = 500f, prefix = sym,
            quickChips = listOf("${sym}25K" to 25000.0, "${sym}50K" to 50000.0, "${sym}1L" to 100000.0, "${sym}1.5L" to 150000.0),
            onValueChange = { yearly = it.coerceIn(0.0, 150000.0) }, testTagPrefix = "ppf_yearly"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.FD_RATE), value = rate, valueRange = 0f..12f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            onValueChange = { rate = it.coerceIn(0.0, 25.0) }, testTagPrefix = "ppf_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.PPF_YEARS), value = years, valueRange = 0f..50f, stepSize = 1f,
            suffix = " " + tr(language, Strings.YEARS),
            quickChips = listOf("15" to 15.0, "20" to 20.0, "25" to 25.0, "30" to 30.0),
            onValueChange = { years = it.coerceIn(0.0, 50.0) }, testTagPrefix = "ppf_years"
        )

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.MATURITY_VALUE),
            mainValue = money(maturity),
            rows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest)
            ),
            splitFirst = invested,
            splitSecond = interest,
            tag = "ppf_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_PPF),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.INVESTED_AMOUNT) to money(invested),
                tr(language, ExtraStrings.INTEREST_EARNED) to money(interest),
                tr(language, ExtraStrings.MATURITY_VALUE) to money(maturity)
            ),
            shareText = buildString {
                append("🏦 ${tr(language, ExtraStrings.TOOL_PPF)}\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.INVESTED_AMOUNT)}: ${money(invested)}\n")
                append("${tr(language, ExtraStrings.INTEREST_EARNED)}: ${money(interest)}\n")
                append("${tr(language, ExtraStrings.MATURITY_VALUE)}: ${money(maturity)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                yearly = 0.0
                rate = 0.0
                years = 0.0
            }
        )
        NoteText(tr(language, ExtraStrings.PPF_NOTE))
    }
}

// ---------------------------------------------------------------- Gold loan
@Composable
fun GoldLoanScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var grams by rememberSaveable { mutableStateOf(0.0) }
    var karat by rememberSaveable { mutableStateOf(22) }
    var goldRate by rememberSaveable { mutableStateOf(0.0) }
    var ltv by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var months by rememberSaveable { mutableStateOf(0.0) }
    var bullet by rememberSaveable { mutableStateOf(false) }

    val m = months.toInt()
    val value = FinanceMath.goldValue(grams, karat, goldRate)
    val loan = value * ltv / 100.0
    val emiCalc = EmiMathUtils.calculateEmi(loan, rate, m)
    val interest = if (bullet) FinanceMath.bulletInterest(loan, rate, m) else emiCalc.totalInterest
    val totalPay = loan + interest
    val ml = tr(language, Strings.MONTHS)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.TOOL_GOLD_WEIGHT),
            value = grams, valueRange = 0f..1000f, stepSize = 1f, suffix = " g", isDecimal = true,
            quickChips = listOf("10 g" to 10.0, "25 g" to 25.0, "50 g" to 50.0, "100 g" to 100.0),
            onValueChange = { grams = it }, testTagPrefix = "gold_grams"
        )
        Spacer(Modifier.height(14.dp))
        Text(
            tr(language, ExtraStrings.TOOL_GOLD_PURITY),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(18, 20, 22, 24).forEach { k ->
                ToolChip("${k}K", karat == k, "gold_karat_$k") { karat = k }
            }
        }
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.TOOL_GOLD_RATE),
            value = goldRate, valueRange = 0f..100000f, stepSize = 50f, prefix = sym,
            onValueChange = { goldRate = it }, testTagPrefix = "gold_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.TOOL_GOLD_LTV),
            value = ltv, valueRange = 0f..90f, stepSize = 1f, suffix = "%",
            quickChips = listOf("60%" to 60.0, "70%" to 70.0, "75%" to 75.0, "80%" to 80.0, "85%" to 85.0),
            onValueChange = { ltv = it.coerceIn(0.0, 100.0) }, testTagPrefix = "gold_ltv"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.INTEREST_RATE),
            value = rate, valueRange = 0f..30f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            quickChips = listOf("8%" to 8.0, "9%" to 9.0, "10.5%" to 10.5, "12%" to 12.0, "18%" to 18.0),
            onValueChange = { rate = it.coerceIn(0.0, 40.0) }, testTagPrefix = "gold_rate_pct"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.LOAN_TENURE),
            value = months, valueRange = 0f..60f, stepSize = 1f, suffix = " $ml",
            quickChips = listOf("3 M" to 3.0, "6 M" to 6.0, "12 M" to 12.0, "24 M" to 24.0, "36 M" to 36.0),
            onValueChange = { months = it.coerceIn(0.0, 120.0) }, testTagPrefix = "gold_tenure"
        )
        Spacer(Modifier.height(14.dp))
        Text(
            tr(language, ExtraStrings.GOLD_REPAY),
            style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolChip(tr(language, ExtraStrings.GOLD_EMI), !bullet, "gold_repay_emi") { bullet = false }
            ToolChip(tr(language, ExtraStrings.GOLD_BULLET), bullet, "gold_repay_bullet") { bullet = true }
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.TOOL_ELIGIBLE_LOAN),
            mainValue = money(loan),
            rows = buildList {
                add(tr(language, ExtraStrings.TOOL_GOLD_VALUE) to money(value))
                if (!bullet) add(tr(language, ExtraStrings.TOOL_MONTHLY_EMI) to money(emiCalc.monthlyEmi))
                add(tr(language, ExtraStrings.TOOL_TOTAL_INTEREST) to money(interest))
                add(tr(language, ExtraStrings.TOOL_TOTAL_PAYMENT) to money(totalPay))
            },
            splitFirst = loan,
            splitSecond = interest,
            tag = "gold_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_GOLD),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.TOOL_GOLD_WEIGHT) to "${grams}g",
                tr(language, ExtraStrings.TOOL_GOLD_PURITY) to "${karat}K",
                tr(language, ExtraStrings.TOOL_GOLD_RATE) to money(goldRate),
                tr(language, ExtraStrings.TOOL_GOLD_VALUE) to money(value),
                tr(language, ExtraStrings.TOOL_GOLD_LTV) to pct(ltv),
                tr(language, ExtraStrings.TOOL_ELIGIBLE_LOAN) to money(loan),
                tr(language, Strings.INTEREST_RATE) to "${rate}% p.a.",
                tr(language, Strings.LOAN_TENURE) to "$m months",
                tr(language, ExtraStrings.GOLD_REPAY) to if (bullet) tr(language, ExtraStrings.GOLD_BULLET) else tr(language, ExtraStrings.GOLD_EMI),
                if (!bullet) tr(language, ExtraStrings.TOOL_MONTHLY_EMI) to money(emiCalc.monthlyEmi) else tr(language, ExtraStrings.TOOL_TOTAL_INTEREST) to money(interest),
                tr(language, ExtraStrings.TOOL_TOTAL_PAYMENT) to money(totalPay)
            ),
            shareText = buildString {
                append("🥇 ${tr(language, ExtraStrings.TOOL_GOLD)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.TOOL_GOLD_WEIGHT)}: ${grams}g ${karat}K\n")
                append("${tr(language, ExtraStrings.TOOL_GOLD_VALUE)}: ${money(value)}\n")
                append("${tr(language, ExtraStrings.TOOL_GOLD_LTV)}: ${pct(ltv)}\n")
                append("${tr(language, ExtraStrings.TOOL_ELIGIBLE_LOAN)}: ${money(loan)}\n")
                append("${tr(language, ExtraStrings.TOOL_TOTAL_INTEREST)}: ${money(interest)}\n")
                append("${tr(language, ExtraStrings.TOOL_TOTAL_PAYMENT)}: ${money(totalPay)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                grams = 0.0
                karat = 22
                goldRate = 0.0
                ltv = 0.0
                rate = 0.0
                months = 0.0
                bullet = false
            }
        )
        NoteText(tr(language, ExtraStrings.GOLD_NOTE))
    }
}

// ---------------------------------------------------------------- Car loan
@Composable
fun CarLoanToolScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var price by rememberSaveable { mutableStateOf(0.0) }
    var downPct by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }

    val down = price * downPct / 100.0
    val loan = (price - down).coerceAtLeast(0.0)
    val calc = EmiMathUtils.calculateEmi(loan, rate, years.toInt() * 12)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.CAR_PRICE), value = price, valueRange = 0f..10000000f, stepSize = 10000f, prefix = sym,
            quickChips = listOf("${sym}5L" to 500000.0, "${sym}8L" to 800000.0, "${sym}12L" to 1200000.0, "${sym}20L" to 2000000.0),
            onValueChange = { price = it }, testTagPrefix = "car_price"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.CAR_DOWN), value = downPct, valueRange = 0f..90f, stepSize = 1f, suffix = "%",
            quickChips = listOf("10%" to 10.0, "20%" to 20.0, "30%" to 30.0, "40%" to 40.0),
            onValueChange = { downPct = it.coerceIn(0.0, 100.0) }, testTagPrefix = "car_down"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.INTEREST_RATE), value = rate, valueRange = 0f..30f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            quickChips = listOf("8.5%" to 8.5, "9.25%" to 9.25, "10%" to 10.0, "12%" to 12.0),
            onValueChange = { rate = it.coerceIn(0.0, 40.0) }, testTagPrefix = "car_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.LOAN_TENURE), value = years, valueRange = 0f..10f, stepSize = 1f,
            suffix = " " + tr(language, Strings.YEARS),
            quickChips = listOf("3" to 3.0, "5" to 5.0, "7" to 7.0),
            onValueChange = { years = it.coerceIn(0.0, 15.0) }, testTagPrefix = "car_years"
        )
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, Strings.MONTHLY_EMI),
            mainValue = money(calc.monthlyEmi),
            rows = listOf(
                tr(language, ExtraStrings.CAR_LOAN) to money(loan),
                tr(language, ExtraStrings.DOWN_PAYMENT) to money(down),
                tr(language, Strings.TOTAL_INTEREST) to money(calc.totalInterest),
                tr(language, ExtraStrings.CAR_TOTAL) to money(down + calc.totalPayment)
            ),
            splitFirst = loan,
            splitSecond = calc.totalInterest,
            tag = "car_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_CAR),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.CAR_PRICE) to money(price),
                tr(language, ExtraStrings.CAR_DOWN) to pct(downPct),
                tr(language, ExtraStrings.CAR_LOAN) to money(loan),
                tr(language, Strings.INTEREST_RATE) to "${rate}% p.a.",
                tr(language, Strings.LOAN_TENURE) to "${years.toInt()} years",
                tr(language, Strings.MONTHLY_EMI) to money(calc.monthlyEmi),
                tr(language, Strings.TOTAL_INTEREST) to money(calc.totalInterest),
                tr(language, ExtraStrings.CAR_TOTAL) to money(down + calc.totalPayment)
            ),
            shareText = buildString {
                append("🚗 ${tr(language, ExtraStrings.TOOL_CAR)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.CAR_PRICE)}: ${money(price)}\n")
                append("${tr(language, ExtraStrings.CAR_DOWN)}: ${money(down)} ($downPct%)\n")
                append("${tr(language, ExtraStrings.CAR_LOAN)}: ${money(loan)}\n")
                append("${tr(language, Strings.MONTHLY_EMI)}: ${money(calc.monthlyEmi)}\n")
                append("${tr(language, Strings.TOTAL_INTEREST)}: ${money(calc.totalInterest)}\n")
                append("${tr(language, ExtraStrings.CAR_TOTAL)}: ${money(down + calc.totalPayment)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                price = 0.0
                downPct = 0.0
                rate = 0.0
                years = 0.0
            }
        )

    }
}

// ---------------------------------------------------------------- Health insurance (80D)
@Composable
fun HealthInsuranceScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var selfPremium by rememberSaveable { mutableStateOf(0.0) }
    var parentsPremium by rememberSaveable { mutableStateOf(0.0) }
    var selfSenior by rememberSaveable { mutableStateOf(false) }
    var parentsSenior by rememberSaveable { mutableStateOf(false) }
    var slab by rememberSaveable { mutableStateOf(0.0) }

    val deduction = FinanceMath.section80dDeduction(selfPremium, selfSenior, parentsPremium, parentsSenior)
    val saved = FinanceMath.taxSaved(deduction, slab)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.HEALTH_SELF), value = selfPremium, valueRange = 0f..100000f, stepSize = 500f, prefix = sym,
            quickChips = listOf("${sym}10K" to 10000.0, "${sym}25K" to 25000.0, "${sym}50K" to 50000.0),
            onValueChange = { selfPremium = it }, testTagPrefix = "health_self"
        )
        Row(modifier = Modifier.padding(top = 8.dp)) {
            ToolChip(tr(language, ExtraStrings.HEALTH_SELF_SENIOR), selfSenior, "health_self_senior") { selfSenior = !selfSenior }
        }
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.HEALTH_PARENTS), value = parentsPremium, valueRange = 0f..100000f, stepSize = 500f, prefix = sym,
            quickChips = listOf("${sym}10K" to 10000.0, "${sym}25K" to 25000.0, "${sym}50K" to 50000.0),
            onValueChange = { parentsPremium = it }, testTagPrefix = "health_parents"
        )
        Row(modifier = Modifier.padding(top = 8.dp)) {
            ToolChip(tr(language, ExtraStrings.HEALTH_PARENTS_SENIOR), parentsSenior, "health_parents_senior") { parentsSenior = !parentsSenior }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(tr(language, ExtraStrings.HEALTH_SLAB), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(5.0, 10.0, 15.0, 20.0, 30.0).forEach { s ->
                ToolChip("${s.toInt()}%", slab == s, "health_slab_${s.toInt()}") { slab = s }
            }
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.HEALTH_SAVED),
            mainValue = money(saved),
            rows = listOf(
                tr(language, ExtraStrings.HEALTH_DEDUCTION) to money(deduction),
                tr(language, ExtraStrings.HEALTH_SLAB) to pct(slab)
            ),
            tag = "health_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_HEALTH),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.HEALTH_SELF) to money(selfPremium),
                tr(language, ExtraStrings.HEALTH_PARENTS) to money(parentsPremium),
                tr(language, ExtraStrings.HEALTH_DEDUCTION) to money(deduction),
                tr(language, ExtraStrings.HEALTH_SAVED) to money(saved)
            ),
            shareText = buildString {
                append("🏥 ${tr(language, ExtraStrings.TOOL_HEALTH)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.HEALTH_SELF)}: ${money(selfPremium)}\n")
                append("${tr(language, ExtraStrings.HEALTH_PARENTS)}: ${money(parentsPremium)}\n")
                append("${tr(language, ExtraStrings.HEALTH_DEDUCTION)}: ${money(deduction)}\n")
                append("${tr(language, ExtraStrings.HEALTH_SAVED)}: ${money(saved)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                selfPremium = 0.0
                parentsPremium = 0.0
                selfSenior = false
                parentsSenior = false
                slab = 0.0
            }
        )
        NoteText(tr(language, ExtraStrings.HEALTH_NOTE))
    }
}

// ---------------------------------------------------------------- Skip EMI
@Composable
fun SkipEmiScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var principal by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var tenure by rememberSaveable { mutableStateOf(0.0) }
    var paid by rememberSaveable { mutableStateOf(0.0) }
    var skip by rememberSaveable { mutableStateOf(0.0) }

    val tenureMonths = tenure.toInt()
    val paidEmis = paid.toInt().coerceIn(0, (tenureMonths - 1).coerceAtLeast(0))
    val r = FinanceMath.skipEmi(principal, rate, tenureMonths, paidEmis, skip.toInt())
    val ml = tr(language, Strings.MONTHS)

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, Strings.LOAN_AMOUNT), value = principal, valueRange = 0f..50000000f, stepSize = 10000f, prefix = sym,
            quickChips = listOf("${sym}5L" to 500000.0, "${sym}10L" to 1000000.0, "${sym}25L" to 2500000.0, "${sym}50L" to 5000000.0),
            onValueChange = { principal = it }, testTagPrefix = "skip_principal"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.INTEREST_RATE), value = rate, valueRange = 0f..30f, stepSize = 0.05f, suffix = "%", isDecimal = true,
            onValueChange = { rate = it.coerceIn(0.0, 40.0) }, testTagPrefix = "skip_rate"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, Strings.LOAN_TENURE), value = tenure, valueRange = 0f..360f, stepSize = 1f, suffix = " $ml",
            quickChips = listOf("60 M" to 60.0, "120 M" to 120.0, "240 M" to 240.0),
            onValueChange = { tenure = it.coerceIn(0.0, 480.0) }, testTagPrefix = "skip_tenure"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.SKIP_AFTER), value = paidEmis.toDouble(),
            valueRange = 0f..((tenureMonths - 1).coerceAtLeast(1)).toFloat(), stepSize = 1f,
            onValueChange = { paid = it }, testTagPrefix = "skip_paid"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.SKIP_MONTHS), value = skip, valueRange = 0f..12f, stepSize = 1f, suffix = " $ml",
            quickChips = listOf("1" to 1.0, "2" to 2.0, "3" to 3.0, "6" to 6.0),
            onValueChange = { skip = it.coerceIn(0.0, 24.0) }, testTagPrefix = "skip_months"
        )

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.SKIP_EXTRA),
            mainValue = money(r.extraInterest),
            rows = listOf(
                tr(language, Strings.MONTHLY_EMI) to money(r.emi),
                tr(language, ExtraStrings.SKIP_ORIG_TENURE) to "${r.originalMonths} $ml",
                tr(language, ExtraStrings.SKIP_NEW_TENURE) to "${r.newMonths} $ml",
                tr(language, Strings.TOTAL_INTEREST) to money(r.newInterest)
            ),
            tag = "skip_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_SKIP),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.SKIP_EXTRA) to money(r.extraInterest),
                tr(language, Strings.MONTHLY_EMI) to money(r.emi),
                tr(language, ExtraStrings.SKIP_ORIG_TENURE) to "${r.originalMonths} $ml",
                tr(language, ExtraStrings.SKIP_NEW_TENURE) to "${r.newMonths} $ml",
                tr(language, Strings.TOTAL_INTEREST) to money(r.newInterest)
            ),
            shareText = buildString {
                append("⏸️ ${tr(language, ExtraStrings.TOOL_SKIP)} Report\n")
                append("─────────────────\n")
                append("${tr(language, Strings.MONTHLY_EMI)}: ${money(r.emi)}\n")
                append("${tr(language, ExtraStrings.SKIP_EXTRA)}: ${money(r.extraInterest)}\n")
                append("${tr(language, ExtraStrings.SKIP_NEW_TENURE)}: ${r.newMonths} $ml\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                principal = 0.0
                rate = 0.0
                tenure = 0.0
                paid = 0.0
                skip = 0.0
            }
        )
        NoteText(tr(language, ExtraStrings.SKIP_NOTE))
    }
}

// ---------------------------------------------------------------- GST
@Composable
fun GstScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var amount by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var includes by rememberSaveable { mutableStateOf(false) }

    val (net, tax, total) = FinanceMath.gst(amount, rate, includes)

    ToolColumn {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
            ToolChip(tr(language, ExtraStrings.GST_ADD), !includes, "gst_add") { includes = false }
            ToolChip(tr(language, ExtraStrings.GST_REMOVE), includes, "gst_remove") { includes = true }
        }

        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.GST_AMOUNT), value = amount, valueRange = 0f..10000000f, stepSize = 100f, prefix = sym,
            quickChips = listOf("${sym}1K" to 1000.0, "${sym}10K" to 10000.0, "${sym}1L" to 100000.0),
            onValueChange = { amount = it }, testTagPrefix = "gst_amount"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.GST_RATE), value = rate, valueRange = 0f..50f, stepSize = 0.5f, suffix = "%", isDecimal = true,
            quickChips = listOf("3%" to 3.0, "5%" to 5.0, "12%" to 12.0, "18%" to 18.0, "28%" to 28.0, "40%" to 40.0),
            onValueChange = { rate = it.coerceIn(0.0, 100.0) }, testTagPrefix = "gst_rate"
        )

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.GST_TOTAL),
            mainValue = money(total),
            rows = listOf(
                tr(language, ExtraStrings.GST_NET) to money(net),
                tr(language, ExtraStrings.GST_TAX) to money(tax),
                tr(language, ExtraStrings.GST_SPLIT) to money(tax / 2.0)
            ),
            splitFirst = net,
            splitSecond = tax,
            tag = "gst_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_GST),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.GST_AMOUNT) to money(amount),
                tr(language, ExtraStrings.GST_RATE) to pct(rate),
                tr(language, ExtraStrings.GST_NET) to money(net),
                tr(language, ExtraStrings.GST_TAX) to money(tax),
                tr(language, ExtraStrings.GST_TOTAL) to money(total)
            ),
            shareText = buildString {
                append("🧾 ${tr(language, ExtraStrings.TOOL_GST)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.GST_AMOUNT)}: ${money(amount)}\n")
                append("${tr(language, ExtraStrings.GST_RATE)}: ${pct(rate)}\n")
                append("${tr(language, ExtraStrings.GST_TOTAL)}: ${money(total)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                amount = 0.0
                rate = 0.0
                includes = false
            }
        )
        NoteText(tr(language, ExtraStrings.GST_NOTE))
    }
}

// ---------------------------------------------------------------- Margin
@Composable
fun MarginScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var findPrice by rememberSaveable { mutableStateOf(false) }
    var cost by rememberSaveable { mutableStateOf(0.0) }
    var sell by rememberSaveable { mutableStateOf(0.0) }
    var margin by rememberSaveable { mutableStateOf(0.0) }

    val sellingPrice = if (findPrice) FinanceMath.sellingPriceForMargin(cost, margin) else sell
    val profit = sellingPrice - cost
    val marginPct = FinanceMath.marginPercent(cost, sellingPrice)
    val markupPct = FinanceMath.markupPercent(cost, sellingPrice)

    ToolColumn {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 10.dp)) {
            ToolChip(tr(language, ExtraStrings.MARGIN_FIND), !findPrice, "margin_find") { findPrice = false }
            ToolChip(tr(language, ExtraStrings.MARGIN_PRICE), findPrice, "margin_price") { findPrice = true }
        }

        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.COST_PRICE), value = cost, valueRange = 0f..1000000f, stepSize = 10f, prefix = sym,
            onValueChange = { cost = it }, testTagPrefix = "margin_cost"
        )
        Spacer(Modifier.height(14.dp))
        if (findPrice) {
            EditableSliderInputCard(
                title = tr(language, ExtraStrings.MARGIN_PCT), value = margin, valueRange = 0f..95f, stepSize = 0.5f, suffix = "%", isDecimal = true,
                quickChips = listOf("10%" to 10.0, "20%" to 20.0, "30%" to 30.0, "50%" to 50.0),
                onValueChange = { margin = it.coerceIn(0.0, 99.0) }, testTagPrefix = "margin_pct"
            )
        } else {
            EditableSliderInputCard(
                title = tr(language, ExtraStrings.SELL_PRICE), value = sell, valueRange = 0f..1000000f, stepSize = 10f, prefix = sym,
                onValueChange = { sell = it }, testTagPrefix = "margin_sell"
            )
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = if (findPrice) tr(language, ExtraStrings.SELL_PRICE) else tr(language, ExtraStrings.MARGIN_PCT),
            mainValue = if (findPrice) money(sellingPrice) else pct(marginPct),
            rows = buildList {
                add(tr(language, ExtraStrings.PROFIT) to money(profit))
                if (!findPrice) add(tr(language, ExtraStrings.SELL_PRICE) to money(sellingPrice))
                add(tr(language, ExtraStrings.MARGIN_PCT) to pct(marginPct))
                add(tr(language, ExtraStrings.MARKUP) to pct(markupPct))
            },
            tag = "margin_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_MARGIN),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.COST_PRICE) to money(cost),
                tr(language, ExtraStrings.SELL_PRICE) to money(sellingPrice),
                tr(language, ExtraStrings.PROFIT) to money(profit),
                tr(language, ExtraStrings.MARGIN_PCT) to pct(marginPct),
                tr(language, ExtraStrings.MARKUP) to pct(markupPct)
            ),
            shareText = buildString {
                append("📊 ${tr(language, ExtraStrings.TOOL_MARGIN)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.COST_PRICE)}: ${money(cost)}\n")
                append("${tr(language, ExtraStrings.SELL_PRICE)}: ${money(sellingPrice)}\n")
                append("${tr(language, ExtraStrings.PROFIT)}: ${money(profit)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                findPrice = false
                cost = 0.0
                sell = 0.0
                margin = 0.0
            }
        )

    }
}

// ---------------------------------------------------------------- Discount
@Composable
fun DiscountScreen(language: AppLanguage) {
    val sym = EmiMathUtils.activeCurrency.symbol
    var price by rememberSaveable { mutableStateOf(0.0) }
    var d1 by rememberSaveable { mutableStateOf(0.0) }
    var d2 by rememberSaveable { mutableStateOf(0.0) }

    val finalPrice = FinanceMath.discountedPrice(price, d1, d2)
    val saved = (price - finalPrice).coerceAtLeast(0.0)
    val effective = if (price > 0.0) saved / price * 100.0 else 0.0

    ToolColumn {
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.DISC_PRICE), value = price, valueRange = 0f..1000000f, stepSize = 50f, prefix = sym,
            quickChips = listOf("${sym}500" to 500.0, "${sym}1K" to 1000.0, "${sym}5K" to 5000.0, "${sym}10K" to 10000.0),
            onValueChange = { price = it }, testTagPrefix = "disc_price"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.DISC_PCT), value = d1, valueRange = 0f..90f, stepSize = 1f, suffix = "%",
            quickChips = listOf("10%" to 10.0, "20%" to 20.0, "30%" to 30.0, "50%" to 50.0),
            onValueChange = { d1 = it.coerceIn(0.0, 100.0) }, testTagPrefix = "disc_pct"
        )
        Spacer(Modifier.height(14.dp))
        EditableSliderInputCard(
            title = tr(language, ExtraStrings.DISC_EXTRA), value = d2, valueRange = 0f..50f, stepSize = 1f, suffix = "%",
            quickChips = listOf("5%" to 5.0, "10%" to 10.0, "15%" to 15.0),
            onValueChange = { d2 = it.coerceIn(0.0, 100.0) }, testTagPrefix = "disc_extra"
        )
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ExtraStrings.DISC_FINAL),
            mainValue = money(finalPrice),
            rows = listOf(
                tr(language, ExtraStrings.DISC_SAVED) to money(saved),
                tr(language, ExtraStrings.DISC_PCT) to pct(effective)
            ),
            splitFirst = finalPrice,
            splitSecond = saved,
            tag = "discount_result_card",
            language = language,
            pdfTitle = tr(language, ExtraStrings.TOOL_DISCOUNT),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.DISC_PRICE) to money(price),
                tr(language, ExtraStrings.DISC_PCT) to pct(d1),
                tr(language, ExtraStrings.DISC_FINAL) to money(finalPrice),
                tr(language, ExtraStrings.DISC_SAVED) to money(saved)
            ),
            shareText = buildString {
                append("🏷️ ${tr(language, ExtraStrings.TOOL_DISCOUNT)} Report\n")
                append("─────────────────\n")
                append("${tr(language, ExtraStrings.DISC_PRICE)}: ${money(price)}\n")
                append("${tr(language, ExtraStrings.DISC_FINAL)}: ${money(finalPrice)}\n")
                append("${tr(language, ExtraStrings.DISC_SAVED)}: ${money(saved)}\n")
                append("\nVia EMI Calculator Pro")
            },
            onClear = {
                price = 0.0
                d1 = 0.0
                d2 = 0.0
            }
        )

    }
}