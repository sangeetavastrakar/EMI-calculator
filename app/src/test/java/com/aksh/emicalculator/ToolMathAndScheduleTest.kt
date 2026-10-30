package com.aksh.emicalculator

import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.PrepaymentMode
import com.aksh.emicalculator.data.PrepaymentType
import com.aksh.emicalculator.data.ToolMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolMathAndScheduleTest {

    @Test
    fun simpleInterestMatchesFormula() {
        val (interest, total) = ToolMath.simpleInterest(100000.0, 10.0, 2.0)
        assertEquals(20000.0, interest, 0.01)
        assertEquals(120000.0, total, 0.01)
    }

    @Test
    fun cagrMatchesCompoundGrowth() {
        assertEquals(41.42, ToolMath.cagr(100.0, 200.0, 2.0), 0.02)
    }

    @Test
    fun inflationFutureCostGrows() {
        val future = ToolMath.futureCost(100000.0, 6.0, 10.0)
        assertTrue(future > 179000.0 && future < 180000.0)
    }

    @Test
    fun monthlyExtraScheduleClosesAtZeroAndSavesInterest() {
        val original = EmiMathUtils.calculateEmi(2000000.0, 9.0, 240)
        val rows = EmiMathUtils.generatePrepaymentSchedule(
            principal = 2000000.0,
            annualRatePercent = 9.0,
            tenureMonths = 240,
            prepaymentType = PrepaymentType.MONTHLY_EXTRA,
            extraAmount = 5000.0
        )

        assertTrue(rows.isNotEmpty())
        assertTrue("Prepayment should finish before the original tenure", rows.size < 240)
        assertEquals(0.0, rows.last().closingBalance, 0.01)

        val principalRecovered = rows.sumOf { it.principalPaid }
        assertEquals(2000000.0, principalRecovered, 1.0)

        val newInterest = rows.sumOf { it.interestPaid }
        assertTrue("Prepayment should reduce interest", newInterest < original.totalInterest)
    }

    @Test
    fun oneTimePrepaymentShowsExtraOnlyOnce() {
        val rows = EmiMathUtils.generatePrepaymentSchedule(
            principal = 1000000.0,
            annualRatePercent = 10.0,
            tenureMonths = 120,
            prepaymentType = PrepaymentType.ONE_TIME,
            extraAmount = 100000.0
        )

        assertEquals(1, rows.count { it.extraPaid > 0.0 })
        assertEquals(100000.0, rows.first().extraPaid, 0.01)
        assertEquals(0.0, rows.last().closingBalance, 0.01)
    }

    @Test
    fun reduceEmiKeepsTenureButLowersEmi() {
        val rows = EmiMathUtils.generatePrepaymentSchedule(
            principal = 1000000.0,
            annualRatePercent = 10.0,
            tenureMonths = 120,
            prepaymentType = PrepaymentType.ONE_TIME,
            extraAmount = 200000.0,
            mode = PrepaymentMode.REDUCE_EMI
        )

        val originalEmi = EmiMathUtils.calculateEmi(1000000.0, 10.0, 120).monthlyEmi
        assertTrue("EMI should come down after a lump sum", rows.first().emi < originalEmi)
        assertEquals(120, rows.size)
    }
}
