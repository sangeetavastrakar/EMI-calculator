package com.aksh.emicalculator

import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.LoanOfferInput
import com.aksh.emicalculator.data.PrepaymentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EmiMathUtilsTest {

    @Test
    fun testEmiCalculation_homeLoan() {
        // ₹10,00,000 for 10 years (120 months) at 10% annual interest
        val calc = EmiMathUtils.calculateEmi(1000000.0, 10.0, 120)

        // Standard EMI for 10L @ 10% for 10 yrs is approx ₹13,215
        assertTrue(calc.monthlyEmi > 13200.0 && calc.monthlyEmi < 13230.0)
        assertTrue(calc.totalPayment > 1000000.0)
        assertEquals(1000000.0, calc.principal, 0.1)
        assertTrue(calc.totalInterest > 580000.0 && calc.totalInterest < 590000.0)
    }

    @Test
    fun testLoanComparison_identifiesBestDeal() {
        val loanA = LoanOfferInput("Bank A", 2000000.0, 8.5, 240, 0.5)
        val loanB = LoanOfferInput("Bank B", 2000000.0, 9.5, 240, 0.5)

        val comparison = EmiMathUtils.compareLoans(loanA, loanB)

        // Bank A at 8.5% is significantly cheaper than Bank B at 9.5%
        assertEquals(0, comparison.betterOfferIndex)
        assertTrue(comparison.emiDifferenceMonthly > 1000.0)
        assertTrue(comparison.interestDifferenceTotal > 200000.0)
    }

    @Test
    fun testPrepayment_reducesTenureAndInterest() {
        // Prepaying ₹5,000 extra per month on 20 Lakhs loan
        val impact = EmiMathUtils.calculatePrepayment(
            principal = 2000000.0,
            annualRatePercent = 9.0,
            tenureMonths = 240,
            prepaymentType = PrepaymentType.MONTHLY_EXTRA,
            extraAmount = 5000.0
        )

        assertTrue(impact.interestSaved > 300000.0)
        assertTrue(impact.monthsSaved > 36) // More than 3 years saved
    }

    @Test
    fun testCurrencyFormatting() {
        // Digit grouping differs between the JVM and Android (lakh style vs western),
        // so assert the parts that must always be true on every runtime.
        val formatted = EmiMathUtils.formatCurrency(2500000.0)
        assertTrue(formatted.startsWith("₹2"))
        assertTrue(formatted.endsWith("000.00"))
    }

    @Test
    fun testZeroInterestEmi() {
        val calc = EmiMathUtils.calculateEmi(120000.0, 0.0, 12)
        assertEquals(10000.0, calc.monthlyEmi, 0.01)
        assertEquals(0.0, calc.totalInterest, 0.01)
    }

    @Test
    fun testQuarterlyPrepaymentSavesInterest() {
        val impact = EmiMathUtils.calculatePrepayment(
            1000000.0, 10.0, 120, com.aksh.emicalculator.data.PrepaymentType.QUARTERLY, 25000.0
        )
        assertTrue(impact.interestSaved > 0.0)
        assertTrue(impact.monthsSaved > 0)
    }

    @Test
    fun testReduceEmiLowersEmiKeepsTenure() {
        val impact = EmiMathUtils.calculatePrepayment(
            1000000.0, 10.0, 120, PrepaymentType.ONE_TIME, 200000.0,
            mode = com.aksh.emicalculator.data.PrepaymentMode.REDUCE_EMI
        )
        assertTrue(impact.newEmi < impact.originalEmi)
        assertEquals(0, impact.monthsSaved)
    }

    @Test
    fun testCustomMonthWisePayments() {
        val impact = EmiMathUtils.calculatePrepayment(
            1000000.0, 10.0, 120, PrepaymentType.CUSTOM, 0.0,
            customPayments = listOf(
                com.aksh.emicalculator.data.CustomPrepayment(1, 10000.0),
                com.aksh.emicalculator.data.CustomPrepayment(2, 5000.0),
                com.aksh.emicalculator.data.CustomPrepayment(3, 25000.0)
            )
        )
        assertEquals(40000.0, impact.totalExtraPaid, 0.01)
        assertTrue(impact.interestSaved > 0.0)
    }

    @Test
    fun testMaxLoanForEmiIsInverseOfEmi() {
        val emi = EmiMathUtils.calculateEmi(500000.0, 9.0, 60).monthlyEmi
        assertEquals(500000.0, EmiMathUtils.maxLoanForEmi(emi, 9.0, 60), 1.0)
    }

    @Test
    fun testSipFutureValue() {
        val fv = com.aksh.emicalculator.data.FinanceMath.sipFutureValue(5000.0, 12.0, 10)
        assertTrue(fv > 1150000.0 && fv < 1170000.0)
    }

    @Test
    fun testFdQuarterlyMaturity() {
        val fv = com.aksh.emicalculator.data.FinanceMath.fdMaturity(100000.0, 7.0, 12, 4)
        assertTrue(fv > 107100.0 && fv < 107250.0)
    }

    @Test
    fun testGoldValue() {
        val v = com.aksh.emicalculator.data.FinanceMath.goldValue(50.0, 22, 10000.0)
        assertEquals(458333.33, v, 0.5)
    }

    @Test
    fun testRdMaturity() {
        val v = com.aksh.emicalculator.data.FinanceMath.rdMaturity(5000.0, 6.5, 60)
        assertTrue(v > 350000.0 && v < 358000.0)
    }

    @Test
    fun testPpfMaturity() {
        val v = com.aksh.emicalculator.data.FinanceMath.ppfMaturity(150000.0, 7.1, 15)
        assertTrue(v > 4000000.0 && v < 4150000.0)
    }

    @Test
    fun testSkipEmiCostsMoreInterest() {
        val r = com.aksh.emicalculator.data.FinanceMath.skipEmi(1000000.0, 10.0, 120, 12, 3)
        assertTrue(r.extraInterest > 0.0)
        assertTrue(r.newMonths > r.originalMonths)
    }

    @Test
    fun testGstAddAndRemove() {
        val add = com.aksh.emicalculator.data.FinanceMath.gst(1000.0, 18.0, false)
        assertEquals(1180.0, add.third, 0.01)
        val rem = com.aksh.emicalculator.data.FinanceMath.gst(1180.0, 18.0, true)
        assertEquals(1000.0, rem.first, 0.01)
    }

    @Test
    fun testMarginAndDiscount() {
        assertEquals(20.0, com.aksh.emicalculator.data.FinanceMath.marginPercent(80.0, 100.0), 0.01)
        assertEquals(125.0, com.aksh.emicalculator.data.FinanceMath.sellingPriceForMargin(100.0, 20.0), 0.01)
        assertEquals(810.0, com.aksh.emicalculator.data.FinanceMath.discountedPrice(1000.0, 10.0, 10.0), 0.01)
    }

    @Test
    fun testSection80d() {
        val d = com.aksh.emicalculator.data.FinanceMath.section80dDeduction(40000.0, false, 60000.0, true)
        assertEquals(75000.0, d, 0.01)
    }
}
