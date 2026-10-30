package com.aksh.emicalculator

import com.aksh.emicalculator.ui.screens.ToolCategories
import com.aksh.emicalculator.ui.screens.ToolId
import com.aksh.emicalculator.ui.screens.allToolItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolCatalogTest {

    @Test
    fun dashboardHasFiveCategoriesInTheRequestedOrder() {
        assertEquals(5, ToolCategories.size)
        assertTrue(ToolCategories.all { it.tools.isNotEmpty() })
        assertEquals(ToolId.EMI, ToolCategories[0].tools.first().id)
    }

    @Test
    fun everyCalculatorAppearsOnExactlyOneTile() {
        val ids = allToolItems().map { it.id }
        assertEquals("No duplicate tiles allowed", ids.size, ids.toSet().size)

        ToolId.values().forEach { id ->
            assertTrue("Missing dashboard tile for $id", ids.contains(id))
        }
    }

    @Test
    fun eachCategoryContainsLoanAndInvestmentEssentials() {
        val loanIds = ToolCategories[0].tools.map { it.id }
        assertTrue(loanIds.containsAll(listOf(ToolId.EMI, ToolId.SCHEDULE, ToolId.PREPAYMENT)))

        val bankingIds = ToolCategories[1].tools.map { it.id }
        assertTrue(bankingIds.containsAll(listOf(ToolId.SIP, ToolId.FD, ToolId.RD, ToolId.PPF)))

        val businessIds = ToolCategories[2].tools.map { it.id }
        assertTrue(businessIds.containsAll(listOf(ToolId.GST, ToolId.CAGR, ToolId.BREAKEVEN)))

        val dailyIds = ToolCategories[3].tools.map { it.id }
        assertTrue(dailyIds.containsAll(listOf(ToolId.PERCENTAGE, ToolId.FUEL, ToolId.UNIT)))

        val extraIds = ToolCategories[4].tools.map { it.id }
        assertTrue(extraIds.containsAll(listOf(ToolId.SWP, ToolId.EPF, ToolId.BMI, ToolId.CURRENCY)))
    }
}
