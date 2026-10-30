package com.aksh.emicalculator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aksh.emicalculator.data.ToolMath
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import java.util.Locale
import kotlin.math.ceil

private fun tr(language: AppLanguage, key: String) = TranslationRepository.getString(language, key)
private fun fmtMoney(x: Double) = EmiMathUtils.formatCurrency(x)
private fun fmtPct(x: Double) = String.format(Locale.US, "%.2f%%", x)
private fun fmtNum(x: Double) = String.format(Locale.US, "%.2f", x)

/** Sukanya Samriddhi Yojana — same yearly-compounding maths as PPF. */
@Composable
fun SsyScreen(language: AppLanguage) {
    var yearly by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(8.2) }
    var years by rememberSaveable { mutableStateOf(15) }

    val maturity = FinanceMath.ppfMaturity(yearly, rate, years)
    val deposited = yearly * years
    val interest = (maturity - deposited).coerceAtLeast(0.0)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.AMOUNT)) {
            ToolField(
                title = tr(language, ToolStrings.YEARLY_DEPOSIT),
                value = yearly,
                onValueChange = { yearly = it.coerceIn(0.0, 150000.0) },
                prefix = EmiMathUtils.activeCurrency.symbol,
                testTag = "ssy_yearly"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                title = tr(language, ToolStrings.RATE_PER_YEAR),
                value = rate,
                onValueChange = { rate = it.coerceIn(0.0, 20.0) },
                isDecimal = true,
                suffix = "%",
                testTag = "ssy_rate"
            )
            Spacer(Modifier.height(10.dp))
            ToolChoices(
                options = listOf("15", "16", "17", "18", "19", "20", "21"),
                selectedIndex = (years - 15).coerceIn(0, 6)
            ) { years = it + 15 }
            Spacer(Modifier.height(10.dp))
            ToolField(
                title = tr(language, ToolStrings.TIME_YEARS),
                value = years.toDouble(),
                onValueChange = { years = it.toInt().coerceIn(1, 21) },
                suffix = tr(language, ToolStrings.YEARS_SHORT),
                testTag = "ssy_years"
            )
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.MATURITY_AMT),
            mainValue = fmtMoney(maturity),
            rows = listOf(
                tr(language, ToolStrings.TOTAL_DEPOSITED) to fmtMoney(deposited),
                tr(language, ToolStrings.INTEREST_EARNED_LBL) to fmtMoney(interest)
            ),
            splitFirst = deposited,
            splitSecond = interest,
            tag = "ssy_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_SSY),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.YEARLY_DEPOSIT) to fmtMoney(yearly),
                tr(language, ToolStrings.RATE_PER_YEAR) to fmtPct(rate),
                tr(language, ToolStrings.TOTAL_DEPOSITED) to fmtMoney(deposited),
                tr(language, ToolStrings.INTEREST_EARNED_LBL) to fmtMoney(interest),
                tr(language, ToolStrings.MATURITY_AMT) to fmtMoney(maturity)
            ),
            shareText = "👧 ${tr(language, ToolStrings.TOOL_SSY)}\n${tr(language, ToolStrings.MATURITY_AMT)}: ${fmtMoney(maturity)}",
            onClear = { yearly = 0.0; rate = 8.2; years = 15 }
        )
        NoteText("SSY: max ₹1,50,000 per year, account matures after 21 years. Rate is set by the government every quarter.")
    }
}

@Composable
fun SimpleInterestScreen(language: AppLanguage) {
    var principal by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }

    val (interest, total) = ToolMath.simpleInterest(principal, rate, years)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.AMOUNT)) {
            ToolField(
                tr(language, ToolStrings.PRINCIPAL_AMT), principal, { principal = it },
                prefix = EmiMathUtils.activeCurrency.symbol, testTag = "si_principal"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.RATE_PER_YEAR), rate, { rate = it.coerceIn(0.0, 100.0) },
                isDecimal = true, suffix = "%", testTag = "si_rate"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.TIME_YEARS), years, { years = it.coerceIn(0.0, 50.0) },
                isDecimal = true, suffix = tr(language, ToolStrings.YEARS_SHORT), testTag = "si_years"
            )
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.TOTAL_INTEREST_LBL),
            mainValue = fmtMoney(interest),
            rows = listOf(
                tr(language, ToolStrings.PRINCIPAL_AMT) to fmtMoney(principal),
                tr(language, ToolStrings.TOTAL_AMOUNT) to fmtMoney(total)
            ),
            splitFirst = principal,
            splitSecond = interest,
            tag = "simple_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_SIMPLE),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.PRINCIPAL_AMT) to fmtMoney(principal),
                tr(language, ToolStrings.RATE_PER_YEAR) to fmtPct(rate),
                tr(language, ToolStrings.TIME_YEARS) to fmtNum(years),
                tr(language, ToolStrings.TOTAL_INTEREST_LBL) to fmtMoney(interest),
                tr(language, ToolStrings.TOTAL_AMOUNT) to fmtMoney(total)
            ),
            shareText = "📊 ${tr(language, ToolStrings.TOOL_SIMPLE)}\n${tr(language, ToolStrings.TOTAL_INTEREST_LBL)}: ${fmtMoney(interest)}",
            onClear = { principal = 0.0; rate = 0.0; years = 0.0 }
        )

    }
}

@Composable
fun CompoundInterestScreen(language: AppLanguage) {
    var principal by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }
    var compoundingIndex by rememberSaveable { mutableIntStateOf(0) }

    val options = listOf(
        tr(language, ExtraStrings.FD_YEARLY),
        tr(language, ExtraStrings.FD_HALF),
        tr(language, ExtraStrings.FD_QUARTERLY),
        tr(language, ExtraStrings.FD_MONTHLY)
    )
    val compoundsPerYear = listOf(1, 2, 4, 12)[compoundingIndex]
    val (interest, total) = ToolMath.compoundInterest(principal, rate, years, compoundsPerYear)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.AMOUNT)) {
            ToolField(
                tr(language, ToolStrings.PRINCIPAL_AMT), principal, { principal = it },
                prefix = EmiMathUtils.activeCurrency.symbol, testTag = "ci_principal"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.RATE_PER_YEAR), rate, { rate = it.coerceIn(0.0, 100.0) },
                isDecimal = true, suffix = "%", testTag = "ci_rate"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.TIME_YEARS), years, { years = it.coerceIn(0.0, 50.0) },
                isDecimal = true, suffix = tr(language, ToolStrings.YEARS_SHORT), testTag = "ci_years"
            )
            Spacer(Modifier.height(12.dp))
            ToolChoices(options, compoundingIndex) { compoundingIndex = it }
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.TOTAL_INTEREST_LBL),
            mainValue = fmtMoney(interest),
            rows = listOf(
                tr(language, ToolStrings.PRINCIPAL_AMT) to fmtMoney(principal),
                tr(language, ToolStrings.TOTAL_AMOUNT) to fmtMoney(total)
            ),
            splitFirst = principal,
            splitSecond = interest,
            tag = "compound_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_COMPOUND),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.PRINCIPAL_AMT) to fmtMoney(principal),
                tr(language, ToolStrings.RATE_PER_YEAR) to fmtPct(rate),
                tr(language, ToolStrings.TIME_YEARS) to fmtNum(years),
                tr(language, ToolStrings.COMPOUNDING) to options[compoundingIndex],
                tr(language, ToolStrings.TOTAL_INTEREST_LBL) to fmtMoney(interest),
                tr(language, ToolStrings.TOTAL_AMOUNT) to fmtMoney(total)
            ),
            shareText = "📈 ${tr(language, ToolStrings.TOOL_COMPOUND)}\n${tr(language, ToolStrings.TOTAL_AMOUNT)}: ${fmtMoney(total)}",
            onClear = { principal = 0.0; rate = 0.0; years = 0.0 }
        )

    }
}

@Composable
fun CagrScreen(language: AppLanguage) {
    var initial by rememberSaveable { mutableStateOf(0.0) }
    var final by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }
    val cagr = ToolMath.cagr(initial, final, years)
    val growth = final - initial

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.AMOUNT)) {
            ToolField(
                tr(language, ToolStrings.INITIAL_VALUE), initial, { initial = it },
                prefix = EmiMathUtils.activeCurrency.symbol, testTag = "cagr_initial"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.FINAL_VALUE), final, { final = it },
                prefix = EmiMathUtils.activeCurrency.symbol, testTag = "cagr_final"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.DURATION_YEARS), years, { years = it.coerceIn(0.0, 60.0) },
                isDecimal = true, suffix = tr(language, ToolStrings.YEARS_SHORT), testTag = "cagr_years"
            )
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.CAGR_RESULT),
            mainValue = fmtPct(cagr),
            rows = listOf(
                tr(language, ToolStrings.INITIAL_VALUE) to fmtMoney(initial),
                tr(language, ToolStrings.FINAL_VALUE) to fmtMoney(final),
                tr(language, ToolStrings.ABS_GROWTH) to fmtMoney(growth)
            ),
            tag = "cagr_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_CAGR),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.INITIAL_VALUE) to fmtMoney(initial),
                tr(language, ToolStrings.FINAL_VALUE) to fmtMoney(final),
                tr(language, ToolStrings.DURATION_YEARS) to fmtNum(years),
                tr(language, ToolStrings.CAGR_RESULT) to fmtPct(cagr)
            ),
            shareText = "📊 ${tr(language, ToolStrings.TOOL_CAGR)}: ${fmtPct(cagr)}",
            onClear = { initial = 0.0; final = 0.0; years = 0.0 }
        )

    }
}

@Composable
fun VatScreen(language: AppLanguage) {
    var amount by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(0.0) }
    var includesTax by rememberSaveable { mutableStateOf(false) }

    val (net, tax, total) = FinanceMath.gst(amount, rate, includesTax)

    ToolColumn {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ToolChip(tr(language, ToolStrings.ADD_TAX), !includesTax, "vat_add") { includesTax = false }
            ToolChip(tr(language, ToolStrings.REMOVE_TAX), includesTax, "vat_remove") { includesTax = true }
        }
        Spacer(Modifier.height(12.dp))

        Spacer(Modifier.height(14.dp))
        ToolSectionCard(tr(language, ToolStrings.AMOUNT)) {
            ToolField(
                tr(language, ToolStrings.AMOUNT), amount, { amount = it },
                prefix = EmiMathUtils.activeCurrency.symbol, testTag = "vat_amount"
            )
            Spacer(Modifier.height(10.dp))
            ToolField(
                tr(language, ToolStrings.VAT_RATE), rate, { rate = it.coerceIn(0.0, 100.0) },
                isDecimal = true, suffix = "%", testTag = "vat_rate"
            )
            Spacer(Modifier.height(12.dp))
            ToolChoices(listOf("5%", "12%", "18%", "20%", "28%"), -1) { rate = listOf(5.0, 12.0, 18.0, 20.0, 28.0)[it] }
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.TOTAL_WITH_TAX),
            mainValue = fmtMoney(total),
            rows = listOf(
                tr(language, ToolStrings.AMOUNT) to fmtMoney(net),
                tr(language, ToolStrings.TAX_AMOUNT) to fmtMoney(tax)
            ),
            splitFirst = net,
            splitSecond = tax,
            tag = "vat_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_VAT),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.AMOUNT) to fmtMoney(amount),
                tr(language, ToolStrings.VAT_RATE) to fmtPct(rate),
                tr(language, ToolStrings.TAX_AMOUNT) to fmtMoney(tax),
                tr(language, ToolStrings.TOTAL_WITH_TAX) to fmtMoney(total)
            ),
            shareText = "🧾 ${tr(language, ToolStrings.TOOL_VAT)}\n${tr(language, ToolStrings.TOTAL_WITH_TAX)}: ${fmtMoney(total)}",
            onClear = { amount = 0.0; rate = 0.0 }
        )

    }
}

@Composable
fun BrokerageScreen(language: AppLanguage) {
    var buy by rememberSaveable { mutableStateOf(0.0) }
    var sell by rememberSaveable { mutableStateOf(0.0) }
    var quantity by rememberSaveable { mutableStateOf(0.0) }
    var brokerage by rememberSaveable { mutableStateOf(0.3) }
    val result = ToolMath.brokerage(buy, sell, quantity, brokerage)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.TOOL_BROKERAGE)) {
            ToolField(tr(language, ToolStrings.BUY_PRICE), buy, { buy = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "brokerage_buy")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.SELL_PRICE), sell, { sell = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "brokerage_sell")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.QUANTITY), quantity, { quantity = it }, testTag = "brokerage_qty")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.BROKERAGE_PCT), brokerage, { brokerage = it.coerceIn(0.0, 5.0) }, isDecimal = true, suffix = "%", testTag = "brokerage_pct")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.NET_PROFIT),
            mainValue = fmtMoney(result.netProfit),
            rows = listOf(
                tr(language, ToolStrings.TURNOVER) to fmtMoney(result.turnover),
                tr(language, ToolStrings.TOTAL_CHARGES) to fmtMoney(result.totalCharges)
            ),
            tag = "brokerage_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_BROKERAGE),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.BUY_PRICE) to fmtMoney(buy),
                tr(language, ToolStrings.SELL_PRICE) to fmtMoney(sell),
                tr(language, ToolStrings.QUANTITY) to fmtNum(quantity),
                tr(language, ToolStrings.TURNOVER) to fmtMoney(result.turnover),
                tr(language, ToolStrings.TOTAL_CHARGES) to fmtMoney(result.totalCharges),
                tr(language, ToolStrings.NET_PROFIT) to fmtMoney(result.netProfit)
            ),
            shareText = "📈 ${tr(language, ToolStrings.TOOL_BROKERAGE)}\n${tr(language, ToolStrings.NET_PROFIT)}: ${fmtMoney(result.netProfit)}",
            onClear = { buy = 0.0; sell = 0.0; quantity = 0.0 }
        )
        NoteText("Charges include brokerage + 18% GST + approx. STT/exchange charges (0.12% of turnover).")
    }
}

@Composable
fun InflationScreen(language: AppLanguage) {
    var amount by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(6.0) }
    var years by rememberSaveable { mutableStateOf(10.0) }
    val future = ToolMath.futureCost(amount, rate, years)
    val lost = (future - amount).coerceAtLeast(0.0)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.TOOL_INFLATION)) {
            ToolField(tr(language, ToolStrings.TODAY_AMOUNT), amount, { amount = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "inflation_amount")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.INFLATION_RATE), rate, { rate = it.coerceIn(0.0, 30.0) }, isDecimal = true, suffix = "%", testTag = "inflation_rate")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.TIME_YEARS), years, { years = it.coerceIn(0.0, 60.0) }, isDecimal = true, suffix = tr(language, ToolStrings.YEARS_SHORT), testTag = "inflation_years")
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.FUTURE_COST),
            mainValue = fmtMoney(future),
            rows = listOf(
                tr(language, ToolStrings.TODAY_AMOUNT) to fmtMoney(amount),
                tr(language, ToolStrings.VALUE_LOST) to fmtMoney(lost)
            ),
            tag = "inflation_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_INFLATION),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.TODAY_AMOUNT) to fmtMoney(amount),
                tr(language, ToolStrings.INFLATION_RATE) to fmtPct(rate),
                tr(language, ToolStrings.YEARS_LBL) to fmtNum(years),
                tr(language, ToolStrings.FUTURE_COST) to fmtMoney(future)
            ),
            shareText = "📉 ${tr(language, ToolStrings.TOOL_INFLATION)}\n${tr(language, ToolStrings.FUTURE_COST)}: ${fmtMoney(future)}",
            onClear = { amount = 0.0; rate = 6.0; years = 10.0 }
        )

    }
}

@Composable
fun SalaryScreen(language: AppLanguage) {
    var ctc by rememberSaveable { mutableStateOf(0.0) }
    var basicPct by rememberSaveable { mutableStateOf(40.0) }
    var pfPct by rememberSaveable { mutableStateOf(12.0) }
    var taxPct by rememberSaveable { mutableStateOf(5.0) }
    val s = ToolMath.salaryBreakup(ctc, basicPct, pfPct, taxPct)

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.TOOL_SALARY)) {
            ToolField(tr(language, ToolStrings.CTC_YEARLY), ctc, { ctc = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "salary_ctc")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.BASIC_PCT), basicPct, { basicPct = it.coerceIn(0.0, 100.0) }, isDecimal = true, suffix = "%", testTag = "salary_basic")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.PF_PCT), pfPct, { pfPct = it.coerceIn(0.0, 25.0) }, isDecimal = true, suffix = "%", testTag = "salary_pf")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.TAX_PCT), taxPct, { taxPct = it.coerceIn(0.0, 40.0) }, isDecimal = true, suffix = "%", testTag = "salary_tax")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.INHAND_MONTHLY),
            mainValue = fmtMoney(s.monthlyInHand),
            rows = listOf(
                tr(language, ToolStrings.INHAND_YEARLY) to fmtMoney(s.yearlyInHand),
                tr(language, ToolStrings.TOTAL_DEDUCTIONS) to fmtMoney(s.totalDeductions)
            ),
            tag = "salary_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_SALARY),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.CTC_YEARLY) to fmtMoney(ctc),
                tr(language, ToolStrings.BASIC_SALARY) to fmtMoney(s.basic / 12.0),
                tr(language, ToolStrings.PF_PCT) to fmtMoney(s.pfEmployee),
                tr(language, ToolStrings.TAX_PCT) to fmtMoney(s.tax),
                tr(language, ToolStrings.INHAND_YEARLY) to fmtMoney(s.yearlyInHand),
                tr(language, ToolStrings.INHAND_MONTHLY) to fmtMoney(s.monthlyInHand)
            ),
            shareText = "💼 ${tr(language, ToolStrings.TOOL_SALARY)}\n${tr(language, ToolStrings.INHAND_MONTHLY)}: ${fmtMoney(s.monthlyInHand)}",
            onClear = { ctc = 0.0 }
        )
        NoteText("Approximate in-hand salary. Actual tax depends on your regime, exemptions and city.")
    }
}

@Composable
fun BreakEvenScreen(language: AppLanguage) {
    var fixed by rememberSaveable { mutableStateOf(0.0) }
    var price by rememberSaveable { mutableStateOf(0.0) }
    var variable by rememberSaveable { mutableStateOf(0.0) }
    val units = ToolMath.breakEvenUnits(fixed, price, variable)
    val revenue = units * price

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.TOOL_BREAKEVEN)) {
            ToolField(tr(language, ToolStrings.FIXED_COST), fixed, { fixed = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "be_fixed")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.PRICE_PER_UNIT), price, { price = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "be_price")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.VAR_COST), variable, { variable = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "be_var")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.BREAKEVEN_UNITS),
            mainValue = if (units > 0) fmtNum(ceil(units)) else "0",
            rows = listOf(
                tr(language, ToolStrings.BREAKEVEN_REVENUE) to fmtMoney(revenue),
                tr(language, ToolStrings.FIXED_COST) to fmtMoney(fixed)
            ),
            tag = "breakeven_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_BREAKEVEN),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ToolStrings.FIXED_COST) to fmtMoney(fixed),
                tr(language, ToolStrings.PRICE_PER_UNIT) to fmtMoney(price),
                tr(language, ToolStrings.VAR_COST) to fmtMoney(variable),
                tr(language, ToolStrings.BREAKEVEN_UNITS) to fmtNum(ceil(units)),
                tr(language, ToolStrings.BREAKEVEN_REVENUE) to fmtMoney(revenue)
            ),
            shareText = "⚖️ ${tr(language, ToolStrings.TOOL_BREAKEVEN)}\n${tr(language, ToolStrings.BREAKEVEN_UNITS)}: ${fmtNum(ceil(units))}",
            onClear = { fixed = 0.0; price = 0.0; variable = 0.0 }
        )
        NoteText("Break-even = fixed costs ÷ (selling price − variable cost per unit).")
    }
}

@Composable
fun DiscountMarkupScreen(language: AppLanguage) {
    var cost by rememberSaveable { mutableStateOf(0.0) }
    var markup by rememberSaveable { mutableStateOf(20.0) }
    var discount by rememberSaveable { mutableStateOf(10.0) }
    val selling = cost * (1.0 + markup / 100.0)
    val finalPrice = selling * (1.0 - discount.coerceIn(0.0, 100.0) / 100.0)
    val profit = finalPrice - cost
    val margin = if (finalPrice > 0) profit / finalPrice * 100.0 else 0.0

    ToolColumn {
        ToolSectionCard(tr(language, ToolStrings.TOOL_DISCOUNT_MARKUP)) {
            ToolField(tr(language, ExtraStrings.COST_PRICE), cost, { cost = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "dm_cost")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.MARKUP_PCT), markup, { markup = it.coerceIn(0.0, 1000.0) }, isDecimal = true, suffix = "%", testTag = "dm_markup")
            Spacer(Modifier.height(10.dp))
            ToolField(tr(language, ToolStrings.DISC_RATE), discount, { discount = it.coerceIn(0.0, 100.0) }, isDecimal = true, suffix = "%", testTag = "dm_discount")
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = tr(language, ToolStrings.FINAL_PRICE),
            mainValue = fmtMoney(finalPrice),
            rows = listOf(
                tr(language, ToolStrings.SELL_PRICE) to fmtMoney(selling),
                tr(language, ExtraStrings.PROFIT) to fmtMoney(profit),
                tr(language, ExtraStrings.MARGIN_PCT) to fmtPct(margin)
            ),
            tag = "discountmarkup_result",
            language = language,
            pdfTitle = tr(language, ToolStrings.TOOL_DISCOUNT_MARKUP),
            pdfSubtitle = "",
            pdfRows = listOf(
                tr(language, ExtraStrings.COST_PRICE) to fmtMoney(cost),
                tr(language, ToolStrings.MARKUP_PCT) to fmtPct(markup),
                tr(language, ToolStrings.DISC_RATE) to fmtPct(discount),
                tr(language, ToolStrings.FINAL_PRICE) to fmtMoney(finalPrice),
                tr(language, ExtraStrings.PROFIT) to fmtMoney(profit)
            ),
            shareText = "🏷️ ${tr(language, ToolStrings.TOOL_DISCOUNT_MARKUP)}\n${tr(language, ToolStrings.FINAL_PRICE)}: ${fmtMoney(finalPrice)}",
            onClear = { cost = 0.0; markup = 20.0; discount = 10.0 }
        )

    }
}
