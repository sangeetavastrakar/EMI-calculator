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
import com.aksh.emicalculator.data.ToolMath
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import java.util.Locale
import kotlin.math.ceil

private fun trd(language: AppLanguage, key: String) = TranslationRepository.getString(language, key)
private fun moneyD(x: Double) = EmiMathUtils.formatCurrency(x)
private fun pctD(x: Double) = String.format(Locale.US, "%.2f%%", x)
private fun numD(x: Double, digits: Int = 2) = String.format(Locale.US, "%.${digits}f", x)

@Composable
fun PercentageScreen(language: AppLanguage) {
    var a by rememberSaveable { mutableStateOf(0.0) }
    var b by rememberSaveable { mutableStateOf(0.0) }

    val aPercentOfB = ToolMath.percentOf(a, b)
    val aIsPercentOfB = ToolMath.percentOfTotal(a, b)
    val change = ToolMath.percentChange(a, b)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_PERCENT)) {
            ToolField(trd(language, ToolStrings.VALUE_A), a, { a = it }, isDecimal = true, testTag = "percent_a")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.VALUE_B), b, { b = it }, isDecimal = true, testTag = "percent_b")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = "${numD(a, 0)}% of ${moneyD(b)}",
            mainValue = moneyD(aPercentOfB),
            rows = listOf(
                "$a → $b" to pctD(change),
                "${moneyD(a)} ÷ ${moneyD(b)}" to pctD(aIsPercentOfB)
            ),
            tag = "percentage_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_PERCENT),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.VALUE_A) to numD(a),
                trd(language, ToolStrings.VALUE_B) to numD(b),
                "${numD(a, 0)}% of ${numD(b)}" to numD(aPercentOfB),
                "${numD(a)} is % of ${numD(b)}" to pctD(aIsPercentOfB),
                "Change" to pctD(change)
            ),
            shareText = "🧮 ${trd(language, ToolStrings.TOOL_PERCENT)}: ${pctD(aIsPercentOfB)}",
            onClear = { a = 0.0; b = 0.0 }
        )
        NoteText("A% of B, A is what % of B, and % change from A to B are shown together.")
    }
}

@Composable
fun FuelScreen(language: AppLanguage) {
    var distance by rememberSaveable { mutableStateOf(0.0) }
    var mileage by rememberSaveable { mutableStateOf(0.0) }
    var price by rememberSaveable { mutableStateOf(0.0) }
    val result = ToolMath.fuelCost(distance, mileage, price)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_FUEL)) {
            ToolField(trd(language, ToolStrings.DISTANCE_KM), distance, { distance = it }, suffix = "km", testTag = "fuel_distance")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.MILEAGE), mileage, { mileage = it }, isDecimal = true, suffix = "km/l", testTag = "fuel_mileage")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.FUEL_PRICE), price, { price = it }, isDecimal = true, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "fuel_price")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.FUEL_COST),
            mainValue = moneyD(result.cost),
            rows = listOf(
                trd(language, ToolStrings.FUEL_NEEDED) to "${numD(result.litres)} L",
                "${trd(language, ToolStrings.DISTANCE_KM)}" to "${numD(distance, 0)} km"
            ),
            tag = "fuel_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_FUEL),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.DISTANCE_KM) to numD(distance, 0),
                trd(language, ToolStrings.MILEAGE) to numD(mileage),
                trd(language, ToolStrings.FUEL_PRICE) to moneyD(price),
                trd(language, ToolStrings.FUEL_NEEDED) to "${numD(result.litres)} L",
                trd(language, ToolStrings.FUEL_COST) to moneyD(result.cost)
            ),
            shareText = "⛽ ${trd(language, ToolStrings.TOOL_FUEL)}\n${trd(language, ToolStrings.FUEL_COST)}: ${moneyD(result.cost)}",
            onClear = { distance = 0.0; mileage = 0.0; price = 0.0 }
        )
        NoteText("Fuel cost = (distance ÷ mileage) × price per litre.")
    }
}

@Composable
fun GratuityScreen(language: AppLanguage) {
    var basic by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(0.0) }
    val gratuity = ToolMath.gratuity(basic, years)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_GRATUITY)) {
            ToolField(trd(language, ToolStrings.BASIC_DA), basic, { basic = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "gratuity_basic")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.SERVICE_YEARS), years, { years = it.coerceIn(0.0, 60.0) }, isDecimal = true, suffix = trd(language, ToolStrings.YEARS_SHORT), testTag = "gratuity_years")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.GRATUITY_AMT),
            mainValue = moneyD(gratuity),
            rows = listOf(
                trd(language, ToolStrings.BASIC_DA) to moneyD(basic),
                trd(language, ToolStrings.SERVICE_YEARS) to numD(years, 1)
            ),
            tag = "gratuity_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_GRATUITY),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.BASIC_DA) to moneyD(basic),
                trd(language, ToolStrings.SERVICE_YEARS) to numD(years, 1),
                trd(language, ToolStrings.GRATUITY_AMT) to moneyD(gratuity)
            ),
            shareText = "🎁 ${trd(language, ToolStrings.TOOL_GRATUITY)}: ${moneyD(gratuity)}",
            onClear = { basic = 0.0; years = 0.0 }
        )
        NoteText("Gratuity = Basic + DA × 15/26 × years of service (max ₹20 lakh). 5+ years service needed.")
    }
}

@Composable
fun AgeScreen(language: AppLanguage) {
    var day by rememberSaveable { mutableIntStateOf(1) }
    var month by rememberSaveable { mutableIntStateOf(1) }
    var year by rememberSaveable { mutableIntStateOf(2000) }
    val age = ToolMath.age(year, month, day)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.DOB)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ToolField(
                    trd(language, ToolStrings.DAY), day.toDouble(),
                    { day = it.toInt().coerceIn(1, 31) }, modifier = Modifier.weight(1f), testTag = "age_day"
                )
                ToolField(
                    trd(language, ToolStrings.MONTH), month.toDouble(),
                    { month = it.toInt().coerceIn(1, 12) }, modifier = Modifier.weight(1f), testTag = "age_month"
                )
                ToolField(
                    trd(language, ToolStrings.YEAR), year.toDouble(),
                    { year = it.toInt().coerceIn(1900, 2100) }, modifier = Modifier.weight(1.3f), testTag = "age_year"
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.AGE_RESULT),
            mainValue = "${age.years} ${trd(language, ToolStrings.YEARS_SHORT)} ${age.months} ${trd(language, ToolStrings.MONTHS_SHORT)}",
            rows = listOf(
                "${trd(language, ToolStrings.DAYS_SHORT)}" to "${age.days}",
                "${trd(language, ToolStrings.NEXT_BIRTHDAY)}" to "${age.daysToNextBirthday} ${trd(language, ToolStrings.DAYS_SHORT)}",
                "Total" to "${age.totalDays} ${trd(language, ToolStrings.DAYS_SHORT)}"
            ),
            tag = "age_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_AGE),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.DOB) to "$day/$month/$year",
                trd(language, ToolStrings.AGE_RESULT) to "${age.years} ${trd(language, ToolStrings.YEARS_SHORT)} ${age.months} ${trd(language, ToolStrings.MONTHS_SHORT)} ${age.days} ${trd(language, ToolStrings.DAYS_SHORT)}",
                "Total days" to "${age.totalDays}",
                trd(language, ToolStrings.NEXT_BIRTHDAY) to "${age.daysToNextBirthday} ${trd(language, ToolStrings.DAYS_SHORT)}"
            ),
            shareText = "🎂 ${trd(language, ToolStrings.TOOL_AGE)}: ${age.years} ${trd(language, ToolStrings.YEARS_SHORT)} ${age.months} ${trd(language, ToolStrings.MONTHS_SHORT)}"
        )

    }
}

@Composable
fun SwpScreen(language: AppLanguage) {
    var corpus by rememberSaveable { mutableStateOf(0.0) }
    var monthly by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(8.0) }
    var years by rememberSaveable { mutableStateOf(10) }
    val result = ToolMath.swp(corpus, monthly, rate, years)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_SWP)) {
            ToolField(trd(language, ToolStrings.CORPUS), corpus, { corpus = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "swp_corpus")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.MONTHLY_WITHDRAWAL), monthly, { monthly = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "swp_monthly")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.EXPECTED_RETURN_LBL), rate, { rate = it.coerceIn(0.0, 30.0) }, isDecimal = true, suffix = "%", testTag = "swp_rate")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.TIME_YEARS), years.toDouble(), { years = it.toInt().coerceIn(1, 40) }, suffix = trd(language, ToolStrings.YEARS_SHORT), testTag = "swp_years")
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.FINAL_BALANCE),
            mainValue = moneyD(result.finalBalance),
            rows = listOf(
                trd(language, ToolStrings.TOTAL_WITHDRAWN) to moneyD(result.totalWithdrawn),
                trd(language, ToolStrings.MONTHS_LASTED) to "${result.monthsLasted}"
            ),
            tag = "swp_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_SWP),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.CORPUS) to moneyD(corpus),
                trd(language, ToolStrings.MONTHLY_WITHDRAWAL) to moneyD(monthly),
                trd(language, ToolStrings.EXPECTED_RETURN_LBL) to pctD(rate),
                trd(language, ToolStrings.TOTAL_WITHDRAWN) to moneyD(result.totalWithdrawn),
                trd(language, ToolStrings.FINAL_BALANCE) to moneyD(result.finalBalance)
            ),
            shareText = "💵 ${trd(language, ToolStrings.TOOL_SWP)}\n${trd(language, ToolStrings.FINAL_BALANCE)}: ${moneyD(result.finalBalance)}",
            onClear = { corpus = 0.0; monthly = 0.0 }
        )

    }
}

@Composable
fun EpfScreen(language: AppLanguage) {
    var basic by rememberSaveable { mutableStateOf(0.0) }
    var employeePct by rememberSaveable { mutableStateOf(12.0) }
    var employerPct by rememberSaveable { mutableStateOf(12.0) }
    var current by rememberSaveable { mutableStateOf(0.0) }
    var years by rememberSaveable { mutableStateOf(20) }
    var rate by rememberSaveable { mutableStateOf(8.25) }
    val maturity = ToolMath.epfMaturity(basic, employeePct, employerPct, current, years, rate)
    val monthly = basic * (employeePct + employerPct) / 100.0

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_EPF)) {
            ToolField(trd(language, ToolStrings.BASIC_SALARY), basic, { basic = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "epf_basic")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.EMP_PCT), employeePct, { employeePct = it.coerceIn(0.0, 25.0) }, isDecimal = true, suffix = "%", testTag = "epf_emp")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.EMPLOYER_PCT), employerPct, { employerPct = it.coerceIn(0.0, 25.0) }, isDecimal = true, suffix = "%", testTag = "epf_employer")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.CURRENT_BALANCE), current, { current = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "epf_current")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.EPF_YEARS), years.toDouble(), { years = it.toInt().coerceIn(1, 40) }, suffix = trd(language, ToolStrings.YEARS_SHORT), testTag = "epf_years")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.EPF_RATE), rate, { rate = it.coerceIn(0.0, 15.0) }, isDecimal = true, suffix = "%", testTag = "epf_rate")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.EPF_MATURITY),
            mainValue = moneyD(maturity),
            rows = listOf(
                "${trd(language, ToolStrings.MONTHLY)} ${trd(language, ToolStrings.AMOUNT)}" to moneyD(monthly),
                trd(language, ToolStrings.CURRENT_BALANCE) to moneyD(current)
            ),
            tag = "epf_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_EPF),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.BASIC_SALARY) to moneyD(basic),
                trd(language, ToolStrings.EMP_PCT) to pctD(employeePct),
                trd(language, ToolStrings.EMPLOYER_PCT) to pctD(employerPct),
                trd(language, ToolStrings.EPF_YEARS) to "$years",
                trd(language, ToolStrings.EPF_RATE) to pctD(rate),
                trd(language, ToolStrings.EPF_MATURITY) to moneyD(maturity)
            ),
            shareText = "🏦 ${trd(language, ToolStrings.TOOL_EPF)}\n${trd(language, ToolStrings.EPF_MATURITY)}: ${moneyD(maturity)}",
            onClear = { basic = 0.0; current = 0.0 }
        )
        NoteText("EPF interest is credited yearly on the running balance. Employer share is approximate.")
    }
}

@Composable
fun BmiScreen(language: AppLanguage) {
    var height by rememberSaveable { mutableStateOf(0.0) }
    var weight by rememberSaveable { mutableStateOf(0.0) }
    val bmi = ToolMath.bmi(height, weight)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_BMI)) {
            ToolField(trd(language, ToolStrings.HEIGHT_CM), height, { height = it.coerceIn(0.0, 250.0) }, isDecimal = true, suffix = "cm", testTag = "bmi_height")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.WEIGHT_KG), weight, { weight = it.coerceIn(0.0, 400.0) }, isDecimal = true, suffix = "kg", testTag = "bmi_weight")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.BMI_RESULT),
            mainValue = if (bmi.bmi > 0) numD(bmi.bmi, 1) else "—",
            rows = listOf(
                trd(language, ToolStrings.BMI_CATEGORY) to trd(language, bmi.categoryKey)
            ),
            tag = "bmi_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_BMI),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.HEIGHT_CM) to numD(height),
                trd(language, ToolStrings.WEIGHT_KG) to numD(weight),
                trd(language, ToolStrings.BMI_RESULT) to numD(bmi.bmi, 1),
                trd(language, ToolStrings.BMI_CATEGORY) to trd(language, bmi.categoryKey)
            ),
            shareText = "⚖️ ${trd(language, ToolStrings.TOOL_BMI)}: ${numD(bmi.bmi, 1)} (${trd(language, bmi.categoryKey)})",
            onClear = { height = 0.0; weight = 0.0 }
        )
        NoteText("BMI = weight (kg) ÷ height (m)². Normal range: 18.5 – 24.9.")
    }
}

@Composable
fun CurrencyScreen(language: AppLanguage) {
    var amount by rememberSaveable { mutableStateOf(0.0) }
    var fromIndex by rememberSaveable { mutableIntStateOf(0) }
    var toIndex by rememberSaveable { mutableIntStateOf(1) }
    val codes = ToolMath.currencyCodes
    val converted = ToolMath.convertCurrency(amount, codes[fromIndex], codes[toIndex])

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_CURRENCY)) {
            ToolField(trd(language, ToolStrings.AMOUNT), amount, { amount = it }, isDecimal = true, testTag = "currency_amount")
            Spacer(Modifier.height(10.dp))
            ToolDropdown(trd(language, ToolStrings.FROM), codes, fromIndex) { fromIndex = it }
            Spacer(Modifier.height(10.dp))
            ToolDropdown(trd(language, ToolStrings.TO), codes, toIndex) { toIndex = it }
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = "${codes[fromIndex]} → ${codes[toIndex]}",
            mainValue = numD(converted, 2),
            rows = listOf(
                trd(language, ToolStrings.AMOUNT) to "${numD(amount, 2)} ${codes[fromIndex]}",
                "1 ${codes[fromIndex]}" to "${numD(ToolMath.convertCurrency(1.0, codes[fromIndex], codes[toIndex]), 4)} ${codes[toIndex]}"
            ),
            tag = "currency_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_CURRENCY),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.AMOUNT) to "${numD(amount, 2)} ${codes[fromIndex]}",
                trd(language, ToolStrings.FROM) to codes[fromIndex],
                trd(language, ToolStrings.TO) to codes[toIndex],
                trd(language, ToolStrings.CONVERTED) to "${numD(converted, 2)} ${codes[toIndex]}"
            ),
            shareText = "💱 ${numD(amount, 2)} ${codes[fromIndex]} = ${numD(converted, 2)} ${codes[toIndex]}",
            onClear = { amount = 0.0 }
        )
        NoteText("Rates are indicative and updated with the app. Please check your bank for the exact rate.")
    }
}

@Composable
fun TargetGoalScreen(language: AppLanguage) {
    var target by rememberSaveable { mutableStateOf(0.0) }
    var rate by rememberSaveable { mutableStateOf(7.0) }
    var years by rememberSaveable { mutableStateOf(10) }
    val monthly = ToolMath.monthlySavingForTarget(target, rate, years)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_TARGET)) {
            ToolField(trd(language, ToolStrings.TARGET_AMOUNT), target, { target = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "target_amount")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.RATE_PER_YEAR), rate, { rate = it.coerceIn(0.0, 25.0) }, isDecimal = true, suffix = "%", testTag = "target_rate")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.TIME_YEARS), years.toDouble(), { years = it.toInt().coerceIn(1, 40) }, suffix = trd(language, ToolStrings.YEARS_SHORT), testTag = "target_years")
        }

        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.MONTHLY_SAVING),
            mainValue = moneyD(monthly),
            rows = listOf(
                trd(language, ToolStrings.YEARLY_SAVING) to moneyD(monthly * 12),
                trd(language, ToolStrings.TARGET_AMOUNT) to moneyD(target)
            ),
            tag = "target_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_TARGET),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.TARGET_AMOUNT) to moneyD(target),
                trd(language, ToolStrings.RATE_PER_YEAR) to pctD(rate),
                trd(language, ToolStrings.TIME_YEARS) to "$years",
                trd(language, ToolStrings.MONTHLY_SAVING) to moneyD(monthly),
                trd(language, ToolStrings.YEARLY_SAVING) to moneyD(monthly * 12)
            ),
            shareText = "🎯 ${trd(language, ToolStrings.TOOL_TARGET)}\n${trd(language, ToolStrings.MONTHLY_SAVING)}: ${moneyD(monthly)}",
            onClear = { target = 0.0 }
        )
        NoteText("Works for both RD (monthly) and FD (yearly) goals.")
    }
}

@Composable
fun TipScreen(language: AppLanguage) {
    var bill by rememberSaveable { mutableStateOf(0.0) }
    var tipPercent by rememberSaveable { mutableStateOf(10.0) }
    var people by rememberSaveable { mutableIntStateOf(1) }
    val result = ToolMath.tip(bill, tipPercent, people)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_TIP)) {
            ToolField(trd(language, ToolStrings.BILL_AMOUNT), bill, { bill = it }, prefix = EmiMathUtils.activeCurrency.symbol, testTag = "tip_bill")
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.TIP_PCT), tipPercent, { tipPercent = it.coerceIn(0.0, 100.0) }, isDecimal = true, suffix = "%", testTag = "tip_pct")
            Spacer(Modifier.height(12.dp))
            ToolChoices(listOf("5%", "10%", "15%", "18%", "20%"), -1) {
                tipPercent = listOf(5.0, 10.0, 15.0, 18.0, 20.0)[it]
            }
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.SPLIT_BETWEEN), people.toDouble(), { people = it.toInt().coerceIn(1, 50) }, testTag = "tip_people")
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.TOTAL_BILL),
            mainValue = moneyD(result.total),
            rows = listOf(
                trd(language, ToolStrings.TIP_AMOUNT) to moneyD(result.tip),
                trd(language, ToolStrings.PER_PERSON) to moneyD(result.perPerson)
            ),
            tag = "tip_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_TIP),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.BILL_AMOUNT) to moneyD(bill),
                trd(language, ToolStrings.TIP_PCT) to pctD(tipPercent),
                trd(language, ToolStrings.SPLIT_BETWEEN) to "$people",
                trd(language, ToolStrings.TIP_AMOUNT) to moneyD(result.tip),
                trd(language, ToolStrings.TOTAL_BILL) to moneyD(result.total),
                trd(language, ToolStrings.PER_PERSON) to moneyD(result.perPerson)
            ),
            shareText = "🍽️ ${trd(language, ToolStrings.TOOL_TIP)}\n${trd(language, ToolStrings.TOTAL_BILL)}: ${moneyD(result.total)}",
            onClear = { bill = 0.0; people = 1 }
        )

    }
}

@Composable
fun UnitScreen(language: AppLanguage) {
    var typeIndex by rememberSaveable { mutableIntStateOf(0) }
    var value by rememberSaveable { mutableStateOf(0.0) }
    var fromIndex by rememberSaveable { mutableIntStateOf(0) }
    var toIndex by rememberSaveable { mutableIntStateOf(1) }

    val types = listOf(
        ToolMath.UnitType.LENGTH,
        ToolMath.UnitType.WEIGHT,
        ToolMath.UnitType.TEMPERATURE,
        ToolMath.UnitType.AREA
    )
    val typeLabels = listOf(
        trd(language, ToolStrings.LENGTH),
        trd(language, ToolStrings.WEIGHT),
        trd(language, ToolStrings.TEMPERATURE),
        trd(language, ToolStrings.AREA)
    )
    val units = ToolMath.unitsFor(types[typeIndex])
    val labels = units.map { it.label }
    val safeFrom = fromIndex.coerceIn(0, labels.lastIndex)
    val safeTo = toIndex.coerceIn(0, labels.lastIndex)
    val result = ToolMath.convertUnit(types[typeIndex], value, safeFrom, safeTo)

    ToolColumn {
        ToolSectionCard(trd(language, ToolStrings.TOOL_UNIT)) {
            ToolChoices(typeLabels, typeIndex) {
                typeIndex = it
                fromIndex = 0
                toIndex = 1
            }
            Spacer(Modifier.height(10.dp))
            ToolField(trd(language, ToolStrings.UNIT_VALUE), value, { value = it }, isDecimal = true, testTag = "unit_value")
            Spacer(Modifier.height(10.dp))
            ToolDropdown(trd(language, ToolStrings.FROM), labels, safeFrom) { fromIndex = it }
            Spacer(Modifier.height(10.dp))
            ToolDropdown(trd(language, ToolStrings.TO), labels, safeTo) { toIndex = it }
        }
        Spacer(Modifier.height(14.dp))
        ToolResultCard(
            title = trd(language, ToolStrings.UNIT_RESULT),
            mainValue = numD(result, 3),
            rows = listOf(
                labels[safeFrom] to numD(value, 3),
                labels[safeTo] to numD(result, 3)
            ),
            tag = "unit_result",
            language = language,
            pdfTitle = trd(language, ToolStrings.TOOL_UNIT),
            pdfSubtitle = "",
            pdfRows = listOf(
                trd(language, ToolStrings.UNIT_VALUE) to numD(value, 3),
                labels[safeFrom] to numD(value, 3),
                labels[safeTo] to numD(result, 3)
            ),
            shareText = "📏 ${numD(value, 3)} ${labels[safeFrom]} = ${numD(result, 3)} ${labels[safeTo]}",
            onClear = { value = 0.0 }
        )

    }
}

@Composable
fun SwpSummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        androidx.compose.material3.Text(label, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
        androidx.compose.material3.Text(value, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
    }
}
