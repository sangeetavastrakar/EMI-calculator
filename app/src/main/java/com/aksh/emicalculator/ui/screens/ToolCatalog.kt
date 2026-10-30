package com.aksh.emicalculator.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.aksh.emicalculator.data.ToolStrings

/** Every calculator that can be opened from the dashboard. */
enum class ToolId {
    EMI,
    HOME_CAR_LOAN,
    SCHEDULE,
    PREPAYMENT,
    ADVANCE_PREPAYMENT,
    YEARLY_BREAKUP,
    ELIGIBILITY,
    SIP,
    LUMPSUM,
    PPF,
    FD,
    RD,
    SSY,
    SIMPLE_INTEREST,
    COMPOUND_INTEREST,
    GST,
    PROFIT_MARGIN,
    CAGR,
    VAT,
    BROKERAGE,
    INFLATION,
    SALARY,
    BREAKEVEN,
    DISCOUNT_MARKUP,
    DISCOUNT,
    PERCENTAGE,
    FUEL,
    GRATUITY,
    AGE,
    SWP,
    EPF,
    BMI,
    CURRENCY,
    TARGET_GOAL,
    TIP,
    UNIT,
    GOLD_LOAN,
    CAR_LOAN,
    HEALTH_INSURANCE,
    SKIP_EMI
}

data class ToolItem(
    val id: ToolId,
    val titleKey: String,
    val icon: ImageVector,
    val tint: Color
)

data class ToolCategory(
    val titleKey: String,
    val accent: Color,
    val tools: List<ToolItem>
)

private val BLUE = Color(0xFF2563EB)
private val ORANGE = Color(0xFFF59E0B)
private val PURPLE = Color(0xFF8B5CF6)
private val GREEN = Color(0xFF16A34A)
private val PINK = Color(0xFFEC4899)
private val TEAL = Color(0xFF0D9488)
private val INDIGO = Color(0xFF6366F1)
private val RED = Color(0xFFEF4444)
private val CYAN = Color(0xFF0891B2)
private val LIME = Color(0xFF65A30D)
private val ROSE = Color(0xFFE11D48)
private val SLATE = Color(0xFF475569)

/** The home dashboard sections, in the exact order users see them. */
val ToolCategories: List<ToolCategory> = listOf(
    ToolCategory(
        titleKey = ToolStrings.CAT_LOAN,
        accent = GREEN,
        tools = listOf(
            ToolItem(ToolId.EMI, ToolStrings.TOOL_EMI, Icons.Default.Calculate, BLUE),
            ToolItem(ToolId.HOME_CAR_LOAN, ToolStrings.TOOL_HOME_CAR, Icons.Default.Home, BLUE),
            ToolItem(ToolId.SCHEDULE, ToolStrings.TOOL_SCHEDULE, Icons.Default.CalendarMonth, ORANGE),
            ToolItem(ToolId.PREPAYMENT, ToolStrings.TOOL_PREPAY, Icons.Default.Savings, PURPLE),
            ToolItem(ToolId.ADVANCE_PREPAYMENT, ToolStrings.TOOL_ADVANCE, Icons.Default.Payments, CYAN),
            ToolItem(ToolId.YEARLY_BREAKUP, ToolStrings.TOOL_YEARLY, Icons.Default.Timeline, PURPLE),
            ToolItem(ToolId.ELIGIBILITY, ToolStrings.TOOL_ELIG, Icons.Default.AccountBalance, GREEN),
            ToolItem(ToolId.CAR_LOAN, ToolStrings.TOOL_CAR, Icons.Default.DirectionsCar, BLUE),
            ToolItem(ToolId.GOLD_LOAN, ToolStrings.TOOL_GOLD, Icons.Default.MonetizationOn, ORANGE)
        )
    ),
    ToolCategory(
        titleKey = ToolStrings.CAT_BANKING,
        accent = PURPLE,
        tools = listOf(
            ToolItem(ToolId.SIP, ToolStrings.TOOL_SIP, Icons.Default.TrendingUp, GREEN),
            ToolItem(ToolId.LUMPSUM, ToolStrings.TOOL_LUMPSUM, Icons.Default.ShowChart, TEAL),
            ToolItem(ToolId.PPF, ToolStrings.TOOL_PPF, Icons.Default.AccountBalanceWallet, PURPLE),
            ToolItem(ToolId.FD, ToolStrings.TOOL_FD, Icons.Default.Savings, PURPLE),
            ToolItem(ToolId.RD, ToolStrings.TOOL_RD, Icons.Default.Repeat, CYAN),
            ToolItem(ToolId.SSY, ToolStrings.TOOL_SSY, Icons.Default.ChildCare, PINK),
            ToolItem(ToolId.SIMPLE_INTEREST, ToolStrings.TOOL_SIMPLE, Icons.Default.Percent, INDIGO),
            ToolItem(ToolId.COMPOUND_INTEREST, ToolStrings.TOOL_COMPOUND, Icons.Default.Insights, BLUE)
        )
    ),
    ToolCategory(
        titleKey = ToolStrings.CAT_BUSINESS,
        accent = ORANGE,
        tools = listOf(
            ToolItem(ToolId.GST, ToolStrings.TOOL_GST, Icons.Default.ReceiptLong, INDIGO),
            ToolItem(ToolId.PROFIT_MARGIN, ToolStrings.TOOL_MARGIN, Icons.Default.PieChart, ORANGE),
            ToolItem(ToolId.CAGR, ToolStrings.TOOL_CAGR, Icons.Default.QueryStats, GREEN),
            ToolItem(ToolId.VAT, ToolStrings.TOOL_VAT, Icons.Default.Storefront, PURPLE),
            ToolItem(ToolId.BROKERAGE, ToolStrings.TOOL_BROKERAGE, Icons.Default.ShowChart, CYAN),
            ToolItem(ToolId.INFLATION, ToolStrings.TOOL_INFLATION, Icons.Default.TrendingUp, RED),
            ToolItem(ToolId.SALARY, ToolStrings.TOOL_SALARY, Icons.Default.Work, BLUE),
            ToolItem(ToolId.BREAKEVEN, ToolStrings.TOOL_BREAKEVEN, Icons.Default.Flag, TEAL),
            ToolItem(ToolId.DISCOUNT_MARKUP, ToolStrings.TOOL_DISCOUNT_MARKUP, Icons.Default.LocalOffer, ROSE)
        )
    ),
    ToolCategory(
        titleKey = ToolStrings.CAT_DAILY,
        accent = ROSE,
        tools = listOf(
            ToolItem(ToolId.DISCOUNT, ToolStrings.TOOL_DISCOUNT, Icons.Default.LocalOffer, ROSE),
            ToolItem(ToolId.PERCENTAGE, ToolStrings.TOOL_PERCENT, Icons.Default.Functions, INDIGO),
            ToolItem(ToolId.FUEL, ToolStrings.TOOL_FUEL, Icons.Default.LocalGasStation, LIME),
            ToolItem(ToolId.GRATUITY, ToolStrings.TOOL_GRATUITY, Icons.Default.CardGiftcard, PINK),
            ToolItem(ToolId.AGE, ToolStrings.TOOL_AGE, Icons.Default.Cake, ORANGE),
            ToolItem(ToolId.TIP, ToolStrings.TOOL_TIP, Icons.Default.Restaurant, TEAL),
            ToolItem(ToolId.UNIT, ToolStrings.TOOL_UNIT, Icons.Default.Straighten, CYAN)
        )
    ),
    ToolCategory(
        titleKey = ToolStrings.CAT_EXTRA,
        accent = TEAL,
        tools = listOf(
            ToolItem(ToolId.SWP, ToolStrings.TOOL_SWP, Icons.Default.Timer, PURPLE),
            ToolItem(ToolId.EPF, ToolStrings.TOOL_EPF, Icons.Default.Savings, GREEN),
            ToolItem(ToolId.BMI, ToolStrings.TOOL_BMI, Icons.Default.HealthAndSafety, TEAL),
            ToolItem(ToolId.CURRENCY, ToolStrings.TOOL_CURRENCY, Icons.Default.CurrencyExchange, BLUE),
            ToolItem(ToolId.TARGET_GOAL, ToolStrings.TOOL_TARGET, Icons.Default.Flag, ORANGE),
            ToolItem(ToolId.HEALTH_INSURANCE, ToolStrings.TOOL_HEALTH, Icons.Default.HealthAndSafety, RED),
            ToolItem(ToolId.SKIP_EMI, ToolStrings.TOOL_SKIP, Icons.Default.SkipNext, SLATE)
        )
    )
)

fun allToolItems(): List<ToolItem> = ToolCategories.flatMap { it.tools }

fun toolItem(id: ToolId): ToolItem? = allToolItems().firstOrNull { it.id == id }
