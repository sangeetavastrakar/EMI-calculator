package com.aksh.emicalculator.data

import java.util.Calendar
import kotlin.math.abs
import kotlin.math.pow

/** Pure math for the extra calculators. No Android / Compose code, so it is easy to unit test. */
object ToolMath {

    // ---------------------------------------------------------------- simple & compound interest
    fun simpleInterest(principal: Double, annualRatePercent: Double, years: Double): Pair<Double, Double> {
        if (principal <= 0.0 || years <= 0.0) return 0.0 to principal.coerceAtLeast(0.0)
        val interest = principal * annualRatePercent / 100.0 * years
        return interest to (principal + interest)
    }

    fun compoundInterest(
        principal: Double,
        annualRatePercent: Double,
        years: Double,
        compoundsPerYear: Int
    ): Pair<Double, Double> {
        if (principal <= 0.0 || years <= 0.0) return 0.0 to principal.coerceAtLeast(0.0)
        val n = compoundsPerYear.coerceAtLeast(1)
        val total = principal * (1.0 + annualRatePercent / 100.0 / n).pow(n * years)
        return (total - principal) to total
    }

    // ---------------------------------------------------------------- CAGR
    fun cagr(initial: Double, final: Double, years: Double): Double {
        if (initial <= 0.0 || final <= 0.0 || years <= 0.0) return 0.0
        return ((final / initial).pow(1.0 / years) - 1.0) * 100.0
    }

    // ---------------------------------------------------------------- inflation
    fun futureCost(amount: Double, inflationPercent: Double, years: Double): Double {
        if (amount <= 0.0 || years <= 0.0) return amount.coerceAtLeast(0.0)
        return amount * (1.0 + inflationPercent / 100.0).pow(years)
    }

    // ---------------------------------------------------------------- gratuity (India, 15/26 rule)
    const val GRATUITY_CAP = 2_000_000.0

    fun gratuity(basicDaMonthly: Double, years: Double): Double {
        if (basicDaMonthly <= 0.0 || years <= 0.0) return 0.0
        return (basicDaMonthly * 15.0 / 26.0 * years).coerceAtMost(GRATUITY_CAP)
    }

    // ---------------------------------------------------------------- SWP
    data class SwpResult(val monthsLasted: Int, val finalBalance: Double, val totalWithdrawn: Double)

    fun swp(corpus: Double, monthlyWithdrawal: Double, annualReturnPercent: Double, years: Int): SwpResult {
        if (corpus <= 0.0 || monthlyWithdrawal <= 0.0) return SwpResult(0, corpus.coerceAtLeast(0.0), 0.0)
        val r = annualReturnPercent / 1200.0
        var balance = corpus
        var withdrawn = 0.0
        var months = 0
        val maxMonths = 12 * 60
        while (months < maxMonths && balance > 0.0) {
            balance *= (1.0 + r)
            if (balance < monthlyWithdrawal) {
                withdrawn += balance
                balance = 0.0
                months++
                break
            }
            balance -= monthlyWithdrawal
            withdrawn += monthlyWithdrawal
            months++
        }
        if (months == 0) months = 1
        return SwpResult(months, balance, withdrawn)
    }

    // ---------------------------------------------------------------- EPF
    fun epfMaturity(
        basicMonthly: Double,
        employeePercent: Double,
        employerPercent: Double,
        currentBalance: Double,
        years: Int,
        annualRatePercent: Double
    ): Double {
        if (years <= 0) return currentBalance.coerceAtLeast(0.0)
        val monthly = basicMonthly * (employeePercent + employerPercent) / 100.0
        var balance = currentBalance.coerceAtLeast(0.0)
        repeat(years) {
            balance = (balance + monthly * 12.0) * (1.0 + annualRatePercent / 100.0)
        }
        return balance
    }

    // ---------------------------------------------------------------- BMI
    data class BmiResult(val bmi: Double, val categoryKey: String)

    fun bmi(heightCm: Double, weightKg: Double): BmiResult {
        if (heightCm <= 0.0 || weightKg <= 0.0) return BmiResult(0.0, "t_bmi_normal")
        val m = heightCm / 100.0
        val value = weightKg / (m * m)
        val key = when {
            value < 18.5 -> "t_bmi_under"
            value < 25.0 -> "t_bmi_normal"
            value < 30.0 -> "t_bmi_over"
            else -> "t_bmi_obese"
        }
        return BmiResult(value, key)
    }

    // ---------------------------------------------------------------- break-even
    fun breakEvenUnits(fixedCost: Double, pricePerUnit: Double, variableCostPerUnit: Double): Double {
        val contribution = pricePerUnit - variableCostPerUnit
        if (fixedCost <= 0.0 || contribution <= 0.0) return 0.0
        return fixedCost / contribution
    }

    // ---------------------------------------------------------------- brokerage
    data class BrokerageResult(
        val turnover: Double,
        val brokerage: Double,
        val taxes: Double,
        val totalCharges: Double,
        val grossProfit: Double,
        val netProfit: Double
    )

    fun brokerage(
        buyPrice: Double,
        sellPrice: Double,
        quantity: Double,
        brokeragePercent: Double
    ): BrokerageResult {
        if (buyPrice <= 0.0 || sellPrice <= 0.0 || quantity <= 0.0) {
            return BrokerageResult(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }
        val turnover = (buyPrice + sellPrice) * quantity
        val brokerage = turnover * brokeragePercent / 100.0
        val gst = brokerage * 0.18
        // STT + exchange + SEBI + stamp duty (approx. 0.12% of turnover for delivery trades)
        val taxes = turnover * 0.0012
        val totalCharges = brokerage + gst + taxes
        val gross = (sellPrice - buyPrice) * quantity
        return BrokerageResult(turnover, brokerage, gst + taxes, totalCharges, gross, gross - totalCharges)
    }

    // ---------------------------------------------------------------- salary
    data class SalaryBreakup(
        val basic: Double,
        val pfEmployee: Double,
        val pfEmployer: Double,
        val tax: Double,
        val totalDeductions: Double,
        val yearlyInHand: Double,
        val monthlyInHand: Double
    )

    fun salaryBreakup(ctcYearly: Double, basicPercent: Double, pfPercent: Double, taxPercent: Double): SalaryBreakup {
        if (ctcYearly <= 0.0) return SalaryBreakup(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val basic = ctcYearly * basicPercent.coerceIn(0.0, 100.0) / 100.0
        val pf = basic * pfPercent.coerceIn(0.0, 100.0) / 100.0
        val tax = ctcYearly * taxPercent.coerceIn(0.0, 60.0) / 100.0
        val deductions = pf + pf + tax
        val yearly = (ctcYearly - deductions).coerceAtLeast(0.0)
        return SalaryBreakup(basic, pf, pf, tax, deductions, yearly, yearly / 12.0)
    }

    // ---------------------------------------------------------------- target saving (FD/RD goal)
    fun monthlySavingForTarget(target: Double, annualRatePercent: Double, years: Int): Double {
        val n = years * 12
        if (target <= 0.0 || n <= 0) return 0.0
        val i = annualRatePercent / 1200.0
        if (i <= 0.0) return target / n
        return target * i / ((1.0 + i).pow(n.toDouble()) - 1.0)
    }

    // ---------------------------------------------------------------- tip
    data class TipResult(val tip: Double, val total: Double, val perPerson: Double, val tipPerPerson: Double)

    fun tip(bill: Double, tipPercent: Double, people: Int): TipResult {
        val safeBill = bill.coerceAtLeast(0.0)
        val tip = safeBill * tipPercent.coerceAtLeast(0.0) / 100.0
        val total = safeBill + tip
        val count = people.coerceAtLeast(1)
        return TipResult(tip, total, total / count, tip / count)
    }

    // ---------------------------------------------------------------- fuel
    data class FuelResult(val litres: Double, val cost: Double, val costPerKm: Double)

    fun fuelCost(distanceKm: Double, mileageKmPerLitre: Double, pricePerLitre: Double): FuelResult {
        if (distanceKm <= 0.0 || mileageKmPerLitre <= 0.0) return FuelResult(0.0, 0.0, 0.0)
        val litres = distanceKm / mileageKmPerLitre
        return FuelResult(litres, litres * pricePerLitre, if (distanceKm > 0) litres * pricePerLitre / distanceKm else 0.0)
    }

    // ---------------------------------------------------------------- percentage
    fun percentOf(percent: Double, value: Double): Double = value * percent / 100.0

    fun percentOfTotal(value: Double, total: Double): Double =
        if (total == 0.0) 0.0 else value / total * 100.0

    fun percentChange(from: Double, to: Double): Double =
        if (from == 0.0) 0.0 else (to - from) / abs(from) * 100.0

    // ---------------------------------------------------------------- age
    data class AgeResult(
        val years: Int,
        val months: Int,
        val days: Int,
        val totalDays: Long,
        val daysToNextBirthday: Long
    )

    fun age(dobYear: Int, dobMonth: Int, dobDay: Int, today: Calendar = Calendar.getInstance()): AgeResult {
        val dob = Calendar.getInstance().apply {
            set(dobYear, (dobMonth - 1).coerceIn(0, 11), dobDay.coerceIn(1, 31), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (dob.after(today)) return AgeResult(0, 0, 0, 0, 0)

        var years = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        var months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
        var days = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH)
        if (days < 0) {
            months--
            days += today.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }

        val totalDays = ((today.timeInMillis - dob.timeInMillis) / 86_400_000L).coerceAtLeast(0L)

        val nextBirthday = Calendar.getInstance().apply {
            set(today.get(Calendar.YEAR), dob.get(Calendar.MONTH), dob.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (!nextBirthday.after(today)) nextBirthday.add(Calendar.YEAR, 1)
        val daysToBirthday = ((nextBirthday.timeInMillis - today.timeInMillis) / 86_400_000L).coerceAtLeast(0L)

        return AgeResult(years, months.coerceAtLeast(0), days.coerceAtLeast(0), totalDays, daysToBirthday)
    }

    // ---------------------------------------------------------------- unit conversion
    enum class UnitType { LENGTH, WEIGHT, TEMPERATURE, AREA }

    data class UnitOption(val label: String, val factor: Double)

    val lengthUnits = listOf(
        UnitOption("Millimetre (mm)", 0.001),
        UnitOption("Centimetre (cm)", 0.01),
        UnitOption("Metre (m)", 1.0),
        UnitOption("Kilometre (km)", 1000.0),
        UnitOption("Inch (in)", 0.0254),
        UnitOption("Foot (ft)", 0.3048),
        UnitOption("Yard (yd)", 0.9144),
        UnitOption("Mile (mi)", 1609.344)
    )

    val weightUnits = listOf(
        UnitOption("Milligram (mg)", 0.000001),
        UnitOption("Gram (g)", 0.001),
        UnitOption("Kilogram (kg)", 1.0),
        UnitOption("Ton (t)", 1000.0),
        UnitOption("Pound (lb)", 0.45359237),
        UnitOption("Ounce (oz)", 0.028349523)
    )

    val areaUnits = listOf(
        UnitOption("Square foot (sq ft)", 0.09290304),
        UnitOption("Square metre (sq m)", 1.0),
        UnitOption("Square yard (sq yd)", 0.83612736),
        UnitOption("Acre", 4046.8564224),
        UnitOption("Hectare", 10000.0),
        UnitOption("Square kilometre (sq km)", 1_000_000.0)
    )

    val temperatureUnits = listOf(
        UnitOption("Celsius (°C)", 0.0),
        UnitOption("Fahrenheit (°F)", 0.0),
        UnitOption("Kelvin (K)", 0.0)
    )

    fun unitsFor(type: UnitType): List<UnitOption> = when (type) {
        UnitType.LENGTH -> lengthUnits
        UnitType.WEIGHT -> weightUnits
        UnitType.AREA -> areaUnits
        UnitType.TEMPERATURE -> temperatureUnits
    }

    fun convertUnit(type: UnitType, value: Double, fromIndex: Int, toIndex: Int): Double {
        if (type == UnitType.TEMPERATURE) {
            val celsius = when (fromIndex) {
                0 -> value
                1 -> (value - 32.0) * 5.0 / 9.0
                else -> value - 273.15
            }
            return when (toIndex) {
                0 -> celsius
                1 -> celsius * 9.0 / 5.0 + 32.0
                else -> celsius + 273.15
            }
        }
        val units = unitsFor(type)
        val from = units.getOrNull(fromIndex) ?: units.first()
        val to = units.getOrNull(toIndex) ?: units.first()
        if (from.factor <= 0.0) return value
        return value * from.factor / to.factor
    }

    // ---------------------------------------------------------------- currency (indicative rates)
    /**
     * Indicative mid-market rates (1 unit = X INR). They are only for quick estimates —
     * the app has no internet permission for live rates on purpose.
     */
    val currencyPerInr: Map<String, Double> = mapOf(
        "INR" to 1.0,
        "USD" to 83.5,
        "EUR" to 90.5,
        "GBP" to 106.0,
        "AED" to 22.7,
        "SAR" to 22.3,
        "QAR" to 22.9,
        "KWD" to 272.0,
        "OMR" to 217.0,
        "BHD" to 221.0,
        "CAD" to 61.0,
        "AUD" to 55.0,
        "NZD" to 51.0,
        "SGD" to 62.0,
        "CHF" to 94.0,
        "JPY" to 0.55,
        "CNY" to 11.5,
        "PKR" to 0.30,
        "BDT" to 0.72,
        "LKR" to 0.28,
        "NPR" to 0.625,
        "MYR" to 18.5,
        "IDR" to 0.0053,
        "THB" to 2.4,
        "PHP" to 1.45,
        "ZAR" to 4.6,
        "NGN" to 0.055,
        "KES" to 0.55,
        "EGP" to 1.70,
        "TRY" to 2.50
    )

    val currencyCodes: List<String> = currencyPerInr.keys.toList()

    fun convertCurrency(amount: Double, fromCode: String, toCode: String): Double {
        val from = currencyPerInr[fromCode] ?: return amount
        val to = currencyPerInr[toCode] ?: return amount
        return amount * from / to
    }
}
