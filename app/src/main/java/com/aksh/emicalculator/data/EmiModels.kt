package com.aksh.emicalculator.data

import kotlin.math.pow
import kotlin.math.roundToLong

enum class LoanCategory(
    val titleKey: String,
    val defaultAmount: Double,
    val defaultRate: Double,
    val defaultTenureYears: Int,
    val defaultPrice: Double = 0.0,        // > 0 means this loan type has a down payment
    val maxPrice: Double = 0.0,
    val priceStep: Float = 0f,
    val downStep: Float = 0f
) {
    // price 30,00,000 - 20% down = 24,00,000 loan
    HOME(Strings.HOME_LOAN, 2400000.0, 8.50, 20, 3000000.0, 50000000.0, 10000f, 5000f),
    // price 10,00,000 - 20% down = 8,00,000 loan
    CAR(Strings.CAR_LOAN, 800000.0, 9.25, 5, 1000000.0, 10000000.0, 5000f, 1000f),
    PERSONAL(Strings.PERSONAL_LOAN, 300000.0, 11.50, 3),
    EDUCATION(Strings.EDUCATION_LOAN, 1000000.0, 9.00, 7);

    val hasDownPayment: Boolean get() = defaultPrice > 0.0
}

data class Currency(
    val code: String,
    val symbol: String,
    val name: String,
    val locale: java.util.Locale
)

data class EmiCalculation(
    val principal: Double,
    val annualRatePercent: Double,
    val tenureMonths: Int,
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val principalRatio: Float,
    val interestRatio: Float
)

data class LoanOfferInput(
    val name: String,
    val principal: Double,
    val annualRatePercent: Double,
    val tenureMonths: Int,
    val processingFeePercent: Double = 0.5
)

data class ComparisonResult(
    val offerA: LoanOfferOutput,
    val offerB: LoanOfferOutput,
    val emiDifferenceMonthly: Double,
    val interestDifferenceTotal: Double,
    val betterOfferIndex: Int // 0 for A, 1 for B, -1 for equal
)

data class LoanOfferOutput(
    val name: String,
    val principal: Double,
    val annualRatePercent: Double,
    val tenureMonths: Int,
    val monthlyEmi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val processingFeeAmount: Double
)

enum class PrepaymentType {
    ONE_TIME,
    MONTHLY_EXTRA,
    QUARTERLY,
    HALF_YEARLY,
    YEARLY,
    CUSTOM   // any amount in any month
}

/** What the extra payment does: shorten the loan, or lower the EMI. */
enum class PrepaymentMode {
    REDUCE_TENURE,
    REDUCE_EMI
}

/** One custom part-payment: [month] counted from today (1 = next EMI). */
data class CustomPrepayment(val month: Int, val amount: Double)

data class PrepaymentImpact(
    val originalEmi: Double,
    val originalTotalInterest: Double,
    val originalTenureMonths: Int,
    val emisAlreadyPaid: Int,
    val remainingBalanceBeforePrepayment: Double,
    val newTotalInterest: Double,
    val interestSaved: Double,
    val newTenureMonths: Int,
    val monthsSaved: Int,
    val newEmi: Double = 0.0,
    val totalExtraPaid: Double = 0.0
)

data class AmortizationYear(
    val yearNumber: Int,
    val openingBalance: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val totalPaymentPaid: Double,
    val closingBalance: Double
)

data class AmortizationMonth(
    val monthNumber: Int,
    val yearNumber: Int,
    val openingBalance: Double,
    val emi: Double,
    val principalComponent: Double,
    val interestComponent: Double,
    val closingBalance: Double
)

data class PrepaymentMonthRow(
    val monthNumber: Int,
    val openingBalance: Double,
    val principalPaid: Double,
    val interestPaid: Double,
    val emi: Double,
    val extraPaid: Double,
    val closingBalance: Double
) {
    /** Everything the borrower actually pays this month. */
    val totalPaid: Double get() = emi + extraPaid
}

data class BankPreset(
    val bankNameKey: String,
    val defaultHomeRate: Double,
    val defaultCarRate: Double,
    val defaultPersonalRate: Double
)

data class IndianBankInfo(
    val id: String,
    val name: String,
    val defaultRate: Double
)

val MajorIndianBanks = listOf(
    IndianBankInfo("sbi", "SBI (State Bank of India)", 8.50),
    IndianBankInfo("hdfc", "HDFC Bank", 8.70),
    IndianBankInfo("icici", "ICICI Bank", 8.75),
    IndianBankInfo("pnb", "PNB (Punjab National Bank)", 8.45),
    IndianBankInfo("axis", "Axis Bank", 8.90),
    IndianBankInfo("boi", "BOI (Bank of India)", 8.65),
    IndianBankInfo("bob", "Bank of Baroda", 8.50),
    IndianBankInfo("kotak", "Kotak Mahindra Bank", 8.70),
    IndianBankInfo("canara", "Canara Bank", 8.55),
    IndianBankInfo("union", "Union Bank of India", 8.60)
)

object EmiMathUtils {
    /**
     * Standard Banking Compound EMI Formula:
     * E = P * r * (1 + r)^n / ((1 + r)^n - 1)
     */
    fun calculateEmi(principal: Double, annualRatePercent: Double, tenureMonths: Int): EmiCalculation {
        if (principal <= 0.0 || tenureMonths <= 0) {
            return EmiCalculation(0.0, annualRatePercent, tenureMonths, 0.0, 0.0, 0.0, 1f, 0f)
        }

        val monthlyRate = (annualRatePercent / 12.0) / 100.0

        val monthlyEmi = if (monthlyRate <= 0.0) {
            principal / tenureMonths
        } else {
            val factor = (1.0 + monthlyRate).pow(tenureMonths.toDouble())
            (principal * monthlyRate * factor) / (factor - 1.0)
        }

        val totalPayment = monthlyEmi * tenureMonths
        val totalInterest = totalPayment - principal
        val principalRatio = if (totalPayment > 0.0) (principal / totalPayment).toFloat() else 1f
        val interestRatio = if (totalPayment > 0.0) (totalInterest / totalPayment).toFloat() else 0f

        return EmiCalculation(
            principal = principal,
            annualRatePercent = annualRatePercent,
            tenureMonths = tenureMonths,
            monthlyEmi = monthlyEmi,
            totalInterest = totalInterest,
            totalPayment = totalPayment,
            principalRatio = principalRatio.coerceIn(0f, 1f),
            interestRatio = interestRatio.coerceIn(0f, 1f)
        )
    }

    /** Max loan whose EMI equals [emi] for the given rate/tenure (inverse of the EMI formula). */
    fun maxLoanForEmi(emi: Double, annualRatePercent: Double, tenureMonths: Int): Double {
        if (emi <= 0.0 || tenureMonths <= 0) return 0.0
        val r = (annualRatePercent / 12.0) / 100.0
        if (r <= 0.0) return emi * tenureMonths
        val f = (1.0 + r).pow(tenureMonths.toDouble())
        return emi * (f - 1.0) / (r * f)
    }

    fun compareLoans(loanA: LoanOfferInput, loanB: LoanOfferInput): ComparisonResult {
        val calcA = calculateEmi(loanA.principal, loanA.annualRatePercent, loanA.tenureMonths)
        val calcB = calculateEmi(loanB.principal, loanB.annualRatePercent, loanB.tenureMonths)

        val feeA = loanA.principal * (loanA.processingFeePercent / 100.0)
        val feeB = loanB.principal * (loanB.processingFeePercent / 100.0)

        val totalCostA = calcA.totalPayment + feeA
        val totalCostB = calcB.totalPayment + feeB

        val emiDiff = (calcA.monthlyEmi - calcB.monthlyEmi)
        val interestDiff = (calcA.totalInterest - calcB.totalInterest)

        val betterIndex = when {
            totalCostA < totalCostB - 5.0 -> 0
            totalCostB < totalCostA - 5.0 -> 1
            else -> -1
        }

        return ComparisonResult(
            offerA = LoanOfferOutput(
                name = loanA.name,
                principal = loanA.principal,
                annualRatePercent = loanA.annualRatePercent,
                tenureMonths = loanA.tenureMonths,
                monthlyEmi = calcA.monthlyEmi,
                totalInterest = calcA.totalInterest,
                totalPayment = calcA.totalPayment,
                processingFeeAmount = feeA
            ),
            offerB = LoanOfferOutput(
                name = loanB.name,
                principal = loanB.principal,
                annualRatePercent = loanB.annualRatePercent,
                tenureMonths = loanB.tenureMonths,
                monthlyEmi = calcB.monthlyEmi,
                totalInterest = calcB.totalInterest,
                totalPayment = calcB.totalPayment,
                processingFeeAmount = feeB
            ),
            emiDifferenceMonthly = kotlin.math.abs(emiDiff),
            interestDifferenceTotal = kotlin.math.abs(interestDiff),
            betterOfferIndex = betterIndex
        )
    }

    private fun pmt(balance: Double, monthlyRate: Double, months: Int): Double {
        if (balance <= 0.0 || months <= 0) return 0.0
        if (monthlyRate <= 0.0) return balance / months
        val f = (1.0 + monthlyRate).pow(months.toDouble())
        return balance * monthlyRate * f / (f - 1.0)
    }

    fun calculatePrepayment(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
        prepaymentType: PrepaymentType,
        extraAmount: Double,
        emisAlreadyPaid: Int = 0,
        mode: PrepaymentMode = PrepaymentMode.REDUCE_TENURE,
        customPayments: List<CustomPrepayment> = emptyList()
    ): PrepaymentImpact {
        val baseCalc = calculateEmi(principal, annualRatePercent, tenureMonths)
        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val validPaid = emisAlreadyPaid.coerceIn(0, (tenureMonths - 1).coerceAtLeast(0))

        if (principal <= 0.0 || tenureMonths <= 0) {
            return PrepaymentImpact(0.0, 0.0, tenureMonths, 0, 0.0, 0.0, 0.0, tenureMonths, 0)
        }

        var balance = principal
        val baseEmi = baseCalc.monthlyEmi
        var interestPaidSoFar = 0.0

        // 1. EMIs already paid
        for (m in 1..validPaid) {
            val interestMonth = balance * monthlyRate
            interestPaidSoFar += interestMonth
            val principalPayment = (baseEmi - interestMonth).coerceAtMost(balance)
            balance = (balance - principalPayment).coerceAtLeast(0.0)
            if (balance <= 0.0) break
        }
        val remainingBalanceBefore = balance

        val hasExtra = if (prepaymentType == PrepaymentType.CUSTOM)
            customPayments.any { it.amount > 0.0 } else extraAmount > 0.0

        if (!hasExtra || balance <= 0.0) {
            return PrepaymentImpact(
                originalEmi = baseCalc.monthlyEmi,
                originalTotalInterest = baseCalc.totalInterest,
                originalTenureMonths = tenureMonths,
                emisAlreadyPaid = validPaid,
                remainingBalanceBeforePrepayment = remainingBalanceBefore,
                newTotalInterest = baseCalc.totalInterest,
                interestSaved = 0.0,
                newTenureMonths = tenureMonths,
                monthsSaved = 0,
                newEmi = baseCalc.monthlyEmi,
                totalExtraPaid = 0.0
            )
        }

        // 2. Baseline remaining interest without any extra payment
        var baselineRemainingInterest = 0.0
        var tempBal = balance
        for (m in (validPaid + 1)..tenureMonths) {
            val iMonth = tempBal * monthlyRate
            baselineRemainingInterest += iMonth
            val pPart = (baseEmi - iMonth).coerceAtMost(tempBal)
            tempBal = (tempBal - pPart).coerceAtLeast(0.0)
            if (tempBal <= 0.0) break
        }

        // 3. Simulation with extra payments
        val remainingTotal = tenureMonths - validPaid
        var emi = baseEmi
        var newRemainingInterest = 0.0
        var totalExtra = 0.0
        var k = 0

        if (prepaymentType == PrepaymentType.ONE_TIME) {
            val lump = extraAmount.coerceAtMost(balance)
            balance -= lump
            totalExtra += lump
            if (mode == PrepaymentMode.REDUCE_EMI && balance > 1.0) emi = pmt(balance, monthlyRate, remainingTotal)
        }

        val customByMonth = customPayments.groupBy({ it.month }, { it.amount }).mapValues { it.value.sum() }

        while (balance > 1.0 && k < remainingTotal + 60) {
            k++
            val interestMonth = balance * monthlyRate
            newRemainingInterest += interestMonth
            val principalPayment = (emi - interestMonth).coerceIn(0.0, balance)
            balance -= principalPayment

            val extra = when (prepaymentType) {
                PrepaymentType.MONTHLY_EXTRA -> extraAmount
                PrepaymentType.QUARTERLY -> if (k % 3 == 0) extraAmount else 0.0
                PrepaymentType.HALF_YEARLY -> if (k % 6 == 0) extraAmount else 0.0
                PrepaymentType.YEARLY -> if (k % 12 == 0) extraAmount else 0.0
                PrepaymentType.CUSTOM -> customByMonth[k] ?: 0.0
                PrepaymentType.ONE_TIME -> 0.0
            }.coerceAtMost(balance)

            if (extra > 0.0) {
                balance -= extra
                totalExtra += extra
                if (mode == PrepaymentMode.REDUCE_EMI && balance > 1.0) {
                    val left = remainingTotal - k
                    if (left > 0) emi = pmt(balance, monthlyRate, left)
                }
            }
        }

        val totalMonths = validPaid + k
        val interestSaved = (baselineRemainingInterest - newRemainingInterest).coerceAtLeast(0.0)
        val monthsSaved = (tenureMonths - totalMonths).coerceAtLeast(0)

        return PrepaymentImpact(
            originalEmi = baseCalc.monthlyEmi,
            originalTotalInterest = baseCalc.totalInterest,
            originalTenureMonths = tenureMonths,
            emisAlreadyPaid = validPaid,
            remainingBalanceBeforePrepayment = remainingBalanceBefore,
            newTotalInterest = interestPaidSoFar + newRemainingInterest,
            interestSaved = interestSaved,
            newTenureMonths = totalMonths,
            monthsSaved = monthsSaved,
            newEmi = emi,
            totalExtraPaid = totalExtra
        )
    }

    /**
     * Month-by-month schedule including the extra prepayments, used by the prepayment screen
     * and the PDF report. It mirrors [calculatePrepayment] so the numbers always match.
     */
    fun generatePrepaymentSchedule(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
        prepaymentType: PrepaymentType,
        extraAmount: Double,
        emisAlreadyPaid: Int = 0,
        mode: PrepaymentMode = PrepaymentMode.REDUCE_TENURE,
        customPayments: List<CustomPrepayment> = emptyList()
    ): List<PrepaymentMonthRow> {
        if (principal <= 0.0 || tenureMonths <= 0) return emptyList()
        val monthlyRate = annualRatePercent / 1200.0
        val validPaid = emisAlreadyPaid.coerceIn(0, (tenureMonths - 1).coerceAtLeast(0))
        val customByMonth = customPayments.groupBy({ it.month }, { it.amount }).mapValues { it.value.sum() }

        var balance = principal
        var emi = pmt(principal, monthlyRate, tenureMonths)
        val rows = mutableListOf<PrepaymentMonthRow>()
        var m = 0
        val limit = tenureMonths + 120

        while (balance > 1.0 && m < limit) {
            m++
            val opening = balance
            val interest = opening * monthlyRate
            var principalPart = (emi - interest).coerceIn(0.0, opening)
            balance = (opening - principalPart).coerceAtLeast(0.0)

            var extra = 0.0
            if (m > validPaid && balance > 0.0) {
                extra = when (prepaymentType) {
                    PrepaymentType.ONE_TIME -> if (m == validPaid + 1) extraAmount else 0.0
                    PrepaymentType.MONTHLY_EXTRA -> extraAmount
                    PrepaymentType.QUARTERLY -> if ((m - validPaid) % 3 == 0) extraAmount else 0.0
                    PrepaymentType.HALF_YEARLY -> if ((m - validPaid) % 6 == 0) extraAmount else 0.0
                    PrepaymentType.YEARLY -> if ((m - validPaid) % 12 == 0) extraAmount else 0.0
                    PrepaymentType.CUSTOM -> customByMonth[m - validPaid] ?: 0.0
                }.coerceAtMost(balance)
            }
            if (extra > 0.0) {
                balance = (balance - extra).coerceAtLeast(0.0)
                if (mode == PrepaymentMode.REDUCE_EMI && balance > 1.0) {
                    val left = tenureMonths - m
                    if (left > 0) emi = pmt(balance, monthlyRate, left)
                }
            }

            rows.add(
                PrepaymentMonthRow(
                    monthNumber = m,
                    openingBalance = opening,
                    principalPaid = principalPart + extra,
                    interestPaid = interest,
                    emi = emi,
                    extraPaid = extra,
                    closingBalance = balance
                )
            )

            // A tiny rounding correction so the last row really closes at zero.
            if (balance <= 1.0) break
        }

        return rows.mapIndexed { index, row ->
            if (index == rows.lastIndex) row.copy(closingBalance = 0.0) else row
        }
    }

    fun generateAmortizationSchedule(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int
    ): Pair<List<AmortizationYear>, List<AmortizationMonth>> {
        val baseCalc = calculateEmi(principal, annualRatePercent, tenureMonths)
        val monthlyRate = (annualRatePercent / 12.0) / 100.0
        val emi = baseCalc.monthlyEmi

        var currentBalance = principal
        val monthsList = mutableListOf<AmortizationMonth>()
        val yearsList = mutableListOf<AmortizationYear>()

        var yearOpening = principal
        var yearPrincipal = 0.0
        var yearInterest = 0.0

        for (m in 1..tenureMonths) {
            val opening = currentBalance
            val interestPart = opening * monthlyRate
            var principalPart = emi - interestPart

            if (principalPart > opening || m == tenureMonths) {
                principalPart = opening
            }

            currentBalance = (opening - principalPart).coerceAtLeast(0.0)

            val currentYearNum = ((m - 1) / 12) + 1
            monthsList.add(
                AmortizationMonth(
                    monthNumber = m,
                    yearNumber = currentYearNum,
                    openingBalance = opening,
                    emi = emi,
                    principalComponent = principalPart,
                    interestComponent = interestPart,
                    closingBalance = currentBalance
                )
            )

            yearPrincipal += principalPart
            yearInterest += interestPart

            if (m % 12 == 0 || m == tenureMonths) {
                yearsList.add(
                    AmortizationYear(
                        yearNumber = currentYearNum,
                        openingBalance = yearOpening,
                        principalPaid = yearPrincipal,
                        interestPaid = yearInterest,
                        totalPaymentPaid = yearPrincipal + yearInterest,
                        closingBalance = currentBalance
                    )
                )
                yearOpening = currentBalance
                yearPrincipal = 0.0
                yearInterest = 0.0
            }
        }

        return Pair(yearsList, monthsList)
    }

    /**
     * Formats currency based on the selected currency's locale
     */
    @Volatile
    var activeCurrency: Currency = AvailableCurrencies[1]

    /** Locale used only for digit grouping (1,23,456 vs 123,456) - the symbol always comes from Currency.symbol. */
    private fun numberLocale(currency: Currency): java.util.Locale = when (currency.code) {
        "INR", "PKR", "BDT", "LKR", "NPR", "NONE" -> java.util.Locale("en", "IN")   // lakh / crore style
        "EUR" -> java.util.Locale("en", "IE")
        "CHF" -> java.util.Locale.US
        else -> currency.locale
    }

    fun formatCurrency(amount: Double, currency: Currency = activeCurrency): String {
        if (amount < 0.0) return "-" + formatCurrency(-amount, currency)
        val digits = if (currency.code == "NONE") 2 else try {
            java.util.Currency.getInstance(currency.code).defaultFractionDigits.coerceAtLeast(0)
        } catch (e: Exception) { 2 }
        val nf = java.text.NumberFormat.getNumberInstance(numberLocale(currency))
        nf.minimumFractionDigits = digits
        nf.maximumFractionDigits = digits
        val number = nf.format(amount)
        val symbol = currency.symbol
        if (symbol.isEmpty()) return number
        // letters (Rp, RM, CHF, Arabic) get a space, pure signs ($, ₹, €) do not
        return if (symbol.lastOrNull()?.isLetter() == true) "$symbol $number" else "$symbol$number"
    }
}

val AvailableCurrencies = listOf(
    Currency("USD", "$", "US Dollar", java.util.Locale.US),
    Currency("INR", "₹", "Indian Rupee", java.util.Locale("en", "IN")),
    Currency("EUR", "€", "Euro", java.util.Locale("en", "IE")),
    Currency("GBP", "£", "British Pound", java.util.Locale.UK),
    Currency("AED", "د.إ", "UAE Dirham", java.util.Locale("en", "AE")),
    Currency("CAD", "CA$", "Canadian Dollar", java.util.Locale.CANADA),
    Currency("AUD", "A$", "Australian Dollar", java.util.Locale("en", "AU")),
    Currency("SAR", "ر.س", "Saudi Riyal", java.util.Locale("en", "SA")),
    Currency("QAR", "ر.ق", "Qatari Riyal", java.util.Locale("en", "QA")),
    Currency("KWD", "د.ك", "Kuwaiti Dinar", java.util.Locale("en", "KW")),
    Currency("OMR", "ر.ع.", "Omani Rial", java.util.Locale("en", "OM")),
    Currency("BHD", "د.ب", "Bahraini Dinar", java.util.Locale("en", "BH")),
    Currency("PKR", "₨", "Pakistani Rupee", java.util.Locale("en", "PK")),
    Currency("BDT", "৳", "Bangladeshi Taka", java.util.Locale("en", "BD")),
    Currency("LKR", "රු", "Sri Lankan Rupee", java.util.Locale("en", "LK")),
    Currency("NPR", "रू", "Nepalese Rupee", java.util.Locale("en", "NP")),
    Currency("SGD", "S$", "Singapore Dollar", java.util.Locale("en", "SG")),
    Currency("MYR", "RM", "Malaysian Ringgit", java.util.Locale("en", "MY")),
    Currency("IDR", "Rp", "Indonesian Rupiah", java.util.Locale("en", "ID")),
    Currency("THB", "฿", "Thai Baht", java.util.Locale("en", "TH")),
    Currency("PHP", "₱", "Philippine Peso", java.util.Locale("en", "PH")),
    Currency("JPY", "¥", "Japanese Yen", java.util.Locale.JAPAN),
    Currency("CNY", "CN¥", "Chinese Yuan", java.util.Locale.CHINA),
    Currency("NZD", "NZ$", "New Zealand Dollar", java.util.Locale("en", "NZ")),
    Currency("CHF", "CHF", "Swiss Franc", java.util.Locale.US),
    Currency("ZAR", "R", "South African Rand", java.util.Locale("en", "ZA")),
    Currency("NGN", "₦", "Nigerian Naira", java.util.Locale("en", "NG")),
    Currency("KES", "KSh", "Kenyan Shilling", java.util.Locale("en", "KE")),
    Currency("EGP", "ج.م", "Egyptian Pound", java.util.Locale("en", "EG")),
    Currency("TRY", "₺", "Turkish Lira", java.util.Locale("en", "TR")),
    // No currency symbol at all (just numbers)
    Currency("NONE", "", "No currency", java.util.Locale("en", "IN"))
)
