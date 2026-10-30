package com.aksh.emicalculator.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.AdBannerSlot
import com.aksh.emicalculator.ui.components.InterstitialAdManager
import com.aksh.emicalculator.ui.components.RewardedAdManager
import com.aksh.emicalculator.viewmodel.EmiViewModel

private fun t(language: AppLanguage, key: String) = TranslationRepository.getString(language, key)

/**
 * The new main screen: a colourful, categorized dashboard of every calculator.
 *
 * Ads are deliberately calm:
 *  - a banner sits between every two categories (never over content or inputs);
 *  - a full-screen ad appears at most once every 4th calculator open;
 *  - opening a calculator, PDF and Share are never blocked (see [InterstitialAdManager]).
 */
@Composable
fun HomeScreen(
    viewModel: EmiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    var selectedTool by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedCategory by rememberSaveable { mutableIntStateOf(-1) }

    val isPro by ProManager.isPro.collectAsState()
    val showAds = !isPro

    fun openTool(id: ToolId) {
        // Ginti + optional bada ad sirf navigation par. Input/PDF/Share kabhi block nahi hote.
        InterstitialAdManager.onCalculatorOpened(context) {
            selectedTool = id.name
        }
    }

    BackHandler(enabled = selectedTool != null || selectedCategory >= 0) {
        if (selectedTool != null) selectedTool = null else selectedCategory = -1
    }

    val openToolId = selectedTool?.let { name -> ToolId.values().firstOrNull { it.name == name } }

    when {
        openToolId != null -> {
            ToolDetailHost(
                toolId = openToolId,
                viewModel = viewModel,
                onBack = { selectedTool = null }
            )
        }

        selectedCategory in ToolCategories.indices -> {
            CategoryToolsScreen(
                category = ToolCategories[selectedCategory],
                language = language,
                onBack = { selectedCategory = -1 },
                onOpenTool = { openTool(it) }
            )
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("home_screen")
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                HomeHero(language = language, onOpenEmi = { openTool(ToolId.EMI) })

                Spacer(Modifier.height(16.dp))

                ToolCategories.forEachIndexed { index, category ->
                    CategorySection(
                        category = category,
                        language = language,
                        onViewAll = { selectedCategory = index },
                        onOpenTool = { openTool(it) }
                    )

                    if (showAds && (index + 1) % 2 == 0 && index < ToolCategories.lastIndex) {
                        Spacer(Modifier.height(4.dp))
                        AdBannerSlot(language = language, inline = true, modifier = Modifier.testTag("home_ad_banner_$index"))
                        Spacer(Modifier.height(12.dp))
                    } else {
                        Spacer(Modifier.height(14.dp))
                    }
                }

                Text(
                    text = t(language, ToolStrings.HOME_NOTE),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// --------------------------------------------------------------------------- hero

@Composable
private fun HomeHero(language: AppLanguage, onOpenEmi: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_hero"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(primary, primary.copy(alpha = 0.82f), tertiary.copy(alpha = 0.9f))
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = t(language, Strings.APP_TITLE),
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        Text(
                            text = t(language, ToolStrings.HOME_TAGLINE),
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onOpenEmi)
                        .testTag("home_open_emi")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = t(language, ToolStrings.TOOL_EMI),
                            color = primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------------- category section

@Composable
internal fun CategorySection(
    category: ToolCategory,
    language: AppLanguage,
    onViewAll: (() -> Unit)? = null,
    onOpenTool: (ToolId) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("home_category_${category.titleKey}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(category.accent)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = t(language, category.titleKey),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )
                if (onViewAll != null) {
                    Text(
                        text = t(language, ToolStrings.VIEW_ALL),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onViewAll)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            ToolGrid(tools = category.tools, language = language, onOpenTool = onOpenTool)
        }
    }
}

@Composable
internal fun ToolGrid(
    tools: List<ToolItem>,
    language: AppLanguage,
    onOpenTool: (ToolId) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        tools.chunked(4).forEach { rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowTools.forEach { item ->
                    ToolGridTile(
                        item = item,
                        title = t(language, item.titleKey),
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenTool(item.id) }
                    )
                }
                repeat(4 - rowTools.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ToolGridTile(
    item: ToolItem,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 6.dp)
            .testTag("home_tool_${item.id.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(item.tint.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = title, tint = item.tint, modifier = Modifier.size(23.dp))
        }
        Spacer(Modifier.height(5.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            lineHeight = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// --------------------------------------------------------------------------- category + tool detail screens

@Composable
private fun CategoryToolsScreen(
    category: ToolCategory,
    language: AppLanguage,
    onBack: () -> Unit,
    onOpenTool: (ToolId) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("category_screen")
    ) {
        ToolScreenHeader(
            title = t(language, category.titleKey),
            subtitle = null,
            accent = category.accent,
            onBack = onBack
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
                    )
                ) {
                    Column(Modifier.fillMaxWidth().padding(14.dp)) {
                        ToolGrid(category.tools, language, onOpenTool)
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
internal fun ToolScreenHeader(
    title: String,
    subtitle: String?,
    accent: Color,
    onBack: (() -> Unit)? = null
) {
    com.aksh.emicalculator.ui.components.CalcHeader(
        title = title,
        subtitle = subtitle ?: "Smart Calculation  •  Better Decisions",
        onBack = onBack,
        showPro = true
    )
}

/**
 * One place that maps every dashboard tile to its calculator. Both the Home tab and the
 * Tools tab use this, so a new tool only has to be added in one place.
 */
@Composable
internal fun ToolDetailHost(
    toolId: ToolId,
    viewModel: EmiViewModel,
    onBack: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val item = toolItem(toolId)

    Column(modifier = Modifier.fillMaxSize().testTag("tool_detail_host")) {
        // Every calculator gets the same blue header (logo + title + back + Pro), like the reference design.
        ToolScreenHeader(
            title = item?.let { t(language, it.titleKey) } ?: "",
            subtitle = null,
            accent = item?.tint ?: MaterialTheme.colorScheme.primary,
            onBack = onBack
        )
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (toolId) {
                ToolId.EMI, ToolId.HOME_CAR_LOAN -> EmiCalculatorScreen(viewModel = viewModel)
                ToolId.SCHEDULE, ToolId.YEARLY_BREAKUP -> AmortizationScreen(viewModel = viewModel)
                ToolId.PREPAYMENT, ToolId.ADVANCE_PREPAYMENT -> PrepaymentScreen(viewModel = viewModel)
                ToolId.ELIGIBILITY -> EligibilityScreen(viewModel = viewModel)

                ToolId.SIP -> SipScreen(language)
                ToolId.LUMPSUM -> SipScreen(language, initialLumpsum = true)
                ToolId.PPF -> PpfScreen(language)
                ToolId.FD -> FdScreen(language)
                ToolId.RD -> RdScreen(language)
                ToolId.SSY -> SsyScreen(language)
                ToolId.SIMPLE_INTEREST -> SimpleInterestScreen(language)
                ToolId.COMPOUND_INTEREST -> CompoundInterestScreen(language)

                ToolId.GST -> GstScreen(language)
                ToolId.PROFIT_MARGIN -> MarginScreen(language)
                ToolId.CAGR -> CagrScreen(language)
                ToolId.VAT -> VatScreen(language)
                ToolId.BROKERAGE -> BrokerageScreen(language)
                ToolId.INFLATION -> InflationScreen(language)
                ToolId.SALARY -> SalaryScreen(language)
                ToolId.BREAKEVEN -> BreakEvenScreen(language)
                ToolId.DISCOUNT_MARKUP -> DiscountMarkupScreen(language)

                ToolId.DISCOUNT -> DiscountScreen(language)
                ToolId.PERCENTAGE -> PercentageScreen(language)
                ToolId.FUEL -> FuelScreen(language)
                ToolId.GRATUITY -> GratuityScreen(language)
                ToolId.AGE -> AgeScreen(language)
                ToolId.TIP -> TipScreen(language)
                ToolId.UNIT -> UnitScreen(language)

                ToolId.SWP -> SwpScreen(language)
                ToolId.EPF -> EpfScreen(language)
                ToolId.BMI -> BmiScreen(language)
                ToolId.CURRENCY -> CurrencyScreen(language)
                ToolId.TARGET_GOAL -> TargetGoalScreen(language)
                ToolId.GOLD_LOAN -> GoldLoanScreen(language)
                ToolId.CAR_LOAN -> CarLoanToolScreen(language)
                ToolId.HEALTH_INSURANCE -> HealthInsuranceScreen(language)
                ToolId.SKIP_EMI -> SkipEmiScreen(language)
            }
        }
    }
}
