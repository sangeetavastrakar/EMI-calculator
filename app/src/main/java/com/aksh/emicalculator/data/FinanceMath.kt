package com.aksh.emicalculator.data

import kotlin.math.pow

/** Math for the SIP, FD and Gold-loan calculators. */
object FinanceMath {

    /** Future value of a monthly SIP (payment at the start of each month, monthly compounding). */
    fun sipFutureValue(monthlyAmount: Double, annualReturnPercent: Double, years: Int): Double {
        val n = years * 12
        if (monthlyAmount <= 0.0 || n <= 0) return 0.0
        val i = annualReturnPercent / 12.0 / 100.0
        if (i <= 0.0) return monthlyAmount * n
        return monthlyAmount * (((1.0 + i).pow(n.toDouble()) - 1.0) / i) * (1.0 + i)
    }

    /** One-time (lump sum) investment, compounded yearly. */
    fun lumpsumFutureValue(amount: Double, annualReturnPercent: Double, years: Int): Double {
        if (amount <= 0.0 || years <= 0) return 0.0
        return amount * (1.0 + annualReturnPercent / 100.0).pow(years.toDouble())
    }

    /**
     * Fixed deposit maturity value.
     * [compoundsPerYear] = 12 monthly, 4 quarterly, 2 half-yearly, 1 yearly, 0 = simple interest (paid at maturity).
     */
    fun fdMaturity(principal: Double, annualRatePercent: Double, months: Int, compoundsPerYear: Int): Double {
        if (principal <= 0.0 || months <= 0) return principal.coerceAtLeast(0.0)
        val t = months / 12.0
        val r = annualRatePercent / 100.0
        return if (compoundsPerYear <= 0) {
            principal * (1.0 + r * t)
        } else {
            principal * (1.0 + r / compoundsPerYear).pow(compoundsPerYear * t)
        }
    }

    /** Value of pure gold content: grams x (karat / 24) x rate per gram. */
    fun goldValue(grams: Double, karat: Int, ratePerGram: Double): Double {
        if (grams <= 0.0 || ratePerGram <= 0.0) return 0.0
        return grams * (karat.coerceIn(1, 24) / 24.0) * ratePerGram
    }

    /** Simple interest for a gold loan repaid in one go at the end. */
    fun bulletInterest(loan: Double, annualRatePercent: Double, months: Int): Double {
        if (loan <= 0.0 || months <= 0) return 0.0
        return loan * annualRatePercent / 100.0 * months / 12.0
    }

    /** Recurring deposit: monthly deposits, interest compounded quarterly (Indian bank method). */
    fun rdMaturity(monthlyDeposit: Double, annualRatePercent: Double, months: Int): Double {
        if (monthlyDeposit <= 0.0 || months <= 0) return 0.0
        val i = annualRatePercent / 400.0
        if (i <= 0.0) return monthlyDeposit * months
        val n = months / 3.0
        return monthlyDeposit * ((1.0 + i).pow(n) - 1.0) / (1.0 - (1.0 + i).pow(-1.0 / 3.0))
    }

    /** PPF: deposit at the start of every year, interest compounded yearly. */
    fun ppfMaturity(yearlyDeposit: Double, annualRatePercent: Double, years: Int): Double {
        var balance = 0.0
        repeat(years.coerceAtLeast(0)) { balance = (balance + yearlyDeposit) * (1.0 + annualRatePercent / 100.0) }
        return balance
    }

    data class SkipEmiResult(
        val emi: Double,
        val originalMonths: Int,
        val newMonths: Int,
        val originalInterest: Double,
        val newInterest: Double
    ) { val extraInterest: Double get() = (newInterest - originalInterest).coerceAtLeast(0.0) }

    /** What happens if you skip [skipMonths] EMIs after paying [paidEmis] EMIs (interest keeps adding up). */
    fun skipEmi(principal: Double, annualRatePercent: Double, tenureMonths: Int, paidEmis: Int, skipMonths: Int): SkipEmiResult {
        val base = EmiMathUtils.calculateEmi(principal, annualRatePercent, tenureMonths)
        val r = annualRatePercent / 12.0 / 100.0
        val paid = paidEmis.coerceIn(0, (tenureMonths - 1).coerceAtLeast(0))
        var balance = principal
        var interestTotal = 0.0
        var month = 0
        val limit = tenureMonths + skipMonths + 600
        while (balance > 1.0 && month < limit) {
            month++
            val interest = balance * r
            interestTotal += interest
            val skipped = month > paid && month <= paid + skipMonths
            balance += interest
            if (!skipped) balance -= base.monthlyEmi.coerceAtMost(balance)
        }
        return SkipEmiResult(base.monthlyEmi, tenureMonths, month, base.totalInterest, interestTotal)
    }

    /** Returns (amount without GST, GST amount, total). */
    fun gst(amount: Double, ratePercent: Double, amountIncludesGst: Boolean): Triple<Double, Double, Double> {
        val rate = ratePercent / 100.0
        return if (amountIncludesGst) {
            val net = amount / (1.0 + rate)
            Triple(net, amount - net, amount)
        } else {
            val tax = amount * rate
            Triple(amount, tax, amount + tax)
        }
    }

    /** Margin % = profit / selling price. Markup % = profit / cost. */
    fun marginPercent(cost: Double, sell: Double): Double = if (sell > 0.0) (sell - cost) / sell * 100.0 else 0.0
    fun markupPercent(cost: Double, sell: Double): Double = if (cost > 0.0) (sell - cost) / cost * 100.0 else 0.0
    fun sellingPriceForMargin(cost: Double, marginPercent: Double): Double {
        val m = marginPercent.coerceIn(0.0, 99.9) / 100.0
        return cost / (1.0 - m)
    }

    /** Final price after one or two successive discounts. */
    fun discountedPrice(price: Double, discount1: Double, discount2: Double): Double =
        price * (1.0 - discount1.coerceIn(0.0, 100.0) / 100.0) * (1.0 - discount2.coerceIn(0.0, 100.0) / 100.0)

    /** Section 80D deduction (old regime): self/family up to 25,000 (50,000 if senior), parents the same. */
    fun section80dDeduction(selfPremium: Double, selfSenior: Boolean, parentsPremium: Double, parentsSenior: Boolean): Double {
        val selfLimit = if (selfSenior) 50000.0 else 25000.0
        val parentLimit = if (parentsSenior) 50000.0 else 25000.0
        return selfPremium.coerceIn(0.0, selfLimit) + parentsPremium.coerceIn(0.0, parentLimit)
    }

    /** Tax saved = deduction x slab, plus 4% cess. */
    fun taxSaved(deduction: Double, slabPercent: Double): Double = deduction * slabPercent / 100.0 * 1.04
}
