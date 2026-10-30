package com.aksh.emicalculator

import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Star
import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.ui.text.style.TextOverflow
import com.aksh.emicalculator.ui.theme.resolveDarkTheme
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.R
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.data.PreferencesManager
import com.aksh.emicalculator.ui.components.AdBannerSlot
import com.aksh.emicalculator.ui.components.AdConfig
import com.aksh.emicalculator.ui.components.InterstitialAdManager
import com.aksh.emicalculator.ui.components.RewardedAdManager
import com.aksh.emicalculator.ui.components.RewardAdButton
import com.aksh.emicalculator.ui.components.LanguageDropdownMenu
import com.aksh.emicalculator.ui.components.LanguageSelectionDialog
import com.aksh.emicalculator.ui.components.ThemeSelectionDialog
import com.aksh.emicalculator.ui.screens.AmortizationScreen
import com.aksh.emicalculator.ui.screens.CompareLoansScreen
import com.aksh.emicalculator.ui.screens.EligibilityScreen
import com.aksh.emicalculator.ui.screens.HistoryScreen
import com.aksh.emicalculator.ui.screens.PaywallScreen
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.ui.components.LoadingScreen
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.ui.screens.HomeScreen
import com.aksh.emicalculator.ui.screens.PrepaymentScreen
import com.aksh.emicalculator.ui.theme.EmiCalculatorTheme
import com.aksh.emicalculator.ui.theme.EmeraldLight
import com.aksh.emicalculator.viewmodel.EmiViewModel

private const val SPLASH_MS = 900L

class MainActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()
        // Jab bhi user app me wapas aaye, Google Play se check karo ki subscription active hai ya nahi
        try { ProManager.refreshPurchases() } catch (t: Throwable) {
            android.util.Log.w("MainActivity", "refreshPurchases failed", t)
        }
    }

    private val viewModel: EmiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Billing / Ads kabhi bhi app start crash na karein
        try { ProManager.init(this) } catch (t: Throwable) { android.util.Log.w("MainActivity", "ProManager.init failed", t) }
        try { RewardedAdManager.init(this) } catch (t: Throwable) { android.util.Log.w("MainActivity", "RewardedAdManager.init failed", t) }
        try { AdConfig.initialize(this) } catch (t: Throwable) { android.util.Log.w("MainActivity", "AdConfig.initialize failed", t) }

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val accentColor by viewModel.accentColor.collectAsState()
            val language by viewModel.currentLanguage.collectAsState()
            val currency by viewModel.selectedCurrency.collectAsState()
            val selectedTab by viewModel.selectedTab.collectAsState()
            val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
            val showThemeDialog by viewModel.showThemeDialog.collectAsState()
            val showHistory by viewModel.showHistory.collectAsState()
            val showPaywall by ProManager.showPaywall.collectAsState()
            val isPro by ProManager.isPro.collectAsState()
            val prefsManager = androidx.compose.runtime.remember { PreferencesManager(this@MainActivity) }

            LaunchedEffect(Unit) {
                // 1. Din me ek baar Paywall dikhane ka check
                if (!isPro && prefsManager.shouldShowDailyPaywall()) {
                    kotlinx.coroutines.delay(1000) // Splash hatne ke baad
                    ProManager.showPaywall.value = true
                }

                // 2. Google Play se subscription status check karo
                kotlinx.coroutines.delay(1500)
                ProManager.connect()

                // 3. Bada ad background me ready rakho (4th calculator open par kaam aayega)
                kotlinx.coroutines.delay(3000)
                if (!isPro) InterstitialAdManager.preload(this@MainActivity)
            }

            var showSplash by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(SPLASH_MS)
                showSplash = false
            }

            val isDark = false
            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT) { isDark },
                    navigationBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT) { isDark }
                )
                onDispose {}
            }

            BackHandler(enabled = selectedTab != 0 && !showHistory) { viewModel.setSelectedTab(0) }

            EmiCalculatorTheme(
                themeMode = themeMode,
                accentColor = accentColor
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val imeVisible = WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0
                    Scaffold(
                        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
                        topBar = {
                            EmiTopAppBar(
                                title = TranslationRepository.getString(language, Strings.APP_TITLE),
                                currentLanguage = language,
                                currentCurrency = currency,
                                onLanguageSelected = { viewModel.setLanguage(it) },
                                onCurrencySelected = { viewModel.setSelectedCurrency(it) },
                                onThemeClick = { viewModel.setShowThemeDialog(true) },
                                onHistoryClick = { viewModel.setShowHistory(true) }
                            )
                        },
                        bottomBar = {
                            // Hide ad + tabs while the keyboard is open, so no input field is covered or cut off
                            if (!imeVisible) Column(modifier = Modifier.fillMaxWidth().testTag("bottom_nav_area")) {
                                if (!isPro) {
                                    // Opt-in: user can watch a short ad to turn ads off for a while.
                                    RewardAdButton(language = language)
                                }
                                if (!isPro) {
                                    AdBannerSlot(language = language)
                                }
                                EmiBottomNavigationBar(
                                    selectedTab = selectedTab,
                                    language = language,
                                    // Tab change ab kabhi ad se nahi rukta. Bada ad sirf tab aata hai
                                    // jab user 3 calculators use kar chuka ho (4th open par).
                                    onTabSelected = { idx -> viewModel.setSelectedTab(idx) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                                .imePadding()
                        ) {
                            key(currency.code) {
                                Crossfade(targetState = selectedTab, label = "tab_crossfade") { tab ->
                                    when (tab) {
                                        0 -> HomeScreen(viewModel = viewModel)
                                        1 -> Column {
                                            com.aksh.emicalculator.ui.components.CalcHeader(
                                                title = com.aksh.emicalculator.data.TranslationRepository.getString(language, com.aksh.emicalculator.data.Strings.COMPARE_TITLE),
                                                subtitle = null, onBack = null)
                                            Box(Modifier.weight(1f)) { CompareLoansScreen(viewModel = viewModel) }
                                        }
                                        2 -> Column {
                                            com.aksh.emicalculator.ui.components.CalcHeader(
                                                title = com.aksh.emicalculator.data.TranslationRepository.getString(language, com.aksh.emicalculator.data.Strings.PREPAYMENT_TITLE),
                                                subtitle = null, onBack = null)
                                            Box(Modifier.weight(1f)) { PrepaymentScreen(viewModel = viewModel) }
                                        }
                                        3 -> Column {
                                            com.aksh.emicalculator.ui.components.CalcHeader(
                                                title = com.aksh.emicalculator.data.TranslationRepository.getString(language, com.aksh.emicalculator.data.Strings.SCHEDULE_TITLE),
                                                subtitle = null, onBack = null)
                                            Box(Modifier.weight(1f)) { AmortizationScreen(viewModel = viewModel) }
                                        }
                                    }
                                }
                            }
                        }

                        if (showLanguageDialog) {
                            LanguageSelectionDialog(
                                currentLanguage = language,
                                onLanguageSelected = { viewModel.setLanguage(it) },
                                onDismissRequest = { viewModel.setShowLanguageDialog(false) }
                            )
                        }

                        if (showThemeDialog) {
                            ThemeSelectionDialog(
                                currentTheme = themeMode,
                                currentAccent = accentColor,
                                language = language,
                                onThemeSelected = { mode, accent -> viewModel.setTheme(mode, accent) },
                                onDismissRequest = { viewModel.setShowThemeDialog(false) }
                            )
                        }
                    }

                    if (showHistory) HistoryScreen(viewModel = viewModel)
                    if (showPaywall) PaywallScreen(viewModel = viewModel)

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showSplash,
                        exit = androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(250))
                    ) {
                        LoadingScreen(language = language)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmiTopAppBar(
    title: String,
    currentLanguage: AppLanguage,
    currentCurrency: com.aksh.emicalculator.data.Currency,
    onLanguageSelected: (AppLanguage) -> Unit,
    onCurrencySelected: (com.aksh.emicalculator.data.Currency) -> Unit,
    onThemeClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).clip(RoundedCornerShape(7.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                val isProUser by ProManager.isPro.collectAsState()
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isProUser) EmeraldLight else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { ProManager.showPaywall.value = true }
                        .testTag("go_pro_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = TranslationRepository.getString(
                                currentLanguage,
                                if (isProUser) ExtraStrings.PRO_BADGE else ExtraStrings.GO_PRO
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        },
        actions = {
            com.aksh.emicalculator.ui.components.CurrencySelector(
                selectedCurrency = currentCurrency,
                onCurrencySelected = onCurrencySelected
            )
            LanguageDropdownMenu(
                selectedLanguage = currentLanguage,
                onLanguageSelected = onLanguageSelected,
                compact = true
            )
            IconButton(onClick = onHistoryClick, modifier = Modifier.testTag("action_history_btn")) {
                Icon(Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onThemeClick, modifier = Modifier.testTag("action_theme_btn")) {
                Icon(Icons.Default.Brightness4, contentDescription = "Change Theme", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.testTag("top_app_bar")
    )
}

@Composable
private fun EmiBottomNavigationBar(
    selectedTab: Int,
    language: com.aksh.emicalculator.data.AppLanguage,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        Triple(0, ToolStrings.HOME, Icons.Default.Home),
        Triple(1, Strings.TAB_COMPARE, Icons.Default.CompareArrows),
        Triple(2, Strings.TAB_PREPAYMENT, Icons.Default.Payments),
        Triple(3, Strings.TAB_SCHEDULE, Icons.Default.CalendarMonth)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().testTag("bottom_nav_bar")
    ) {
        items.forEach { (index, labelKey, icon) ->
            val label = TranslationRepository.getString(language, labelKey)
            val isSelected = selectedTab == index

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("tab_item_$index")
            )
        }
    }
}