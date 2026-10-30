package com.aksh.emicalculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aksh.emicalculator.data.AmortizationMonth
import com.aksh.emicalculator.data.AmortizationYear
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.ComparisonResult
import com.aksh.emicalculator.data.EmiCalculation
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.HistoryEntry
import com.aksh.emicalculator.data.HistoryRepository
import com.aksh.emicalculator.data.LoanCategory
import com.aksh.emicalculator.data.LoanOfferInput
import com.aksh.emicalculator.data.PreferencesManager
import com.aksh.emicalculator.data.PrepaymentImpact
import com.aksh.emicalculator.data.PrepaymentType
import com.aksh.emicalculator.data.PrepaymentMode
import com.aksh.emicalculator.data.CustomPrepayment
import com.aksh.emicalculator.data.ThemeMode
import com.aksh.emicalculator.data.AccentColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmiViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = PreferencesManager(application)
    private val historyRepo = HistoryRepository(application)

    // ---------- History (auto-saved) ----------
    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()
    private var historyArmed = false
    private var historyJob: kotlinx.coroutines.Job? = null

    private val _showHistory = MutableStateFlow(false)
    val showHistory: StateFlow<Boolean> = _showHistory.asStateFlow()
    fun setShowHistory(show: Boolean) { _showHistory.value = show }

    private fun scheduleHistorySave() {
        if (!historyArmed) return
        historyJob?.cancel()
        historyJob = viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            val r = _emiResult.value
            if (r.principal <= 0.0 || r.monthlyEmi <= 0.0) return@launch
            val cat = _loanCategory.value
            val entry = HistoryEntry(
                id = System.currentTimeMillis(),
                category = cat.name,
                principal = _principal.value,
                annualRate = _annualRate.value,
                tenureMonths = _tenureMonths.value,
                emi = r.monthlyEmi,
                totalInterest = r.totalInterest,
                totalPayment = r.totalPayment,
                assetPrice = if (cat.hasDownPayment) _assetPrice.value else 0.0,
                downPayment = if (cat.hasDownPayment) _downPayment.value else 0.0,
                currencyCode = _selectedCurrency.value.code
            )
            val current = _history.value
            val previous = current.firstOrNull { it.sameLoanAs(entry) }
            val updated = (listOf(entry.copy(note = previous?.note ?: "")) + current.filterNot { it.sameLoanAs(entry) })
                .take(HistoryRepository.MAX_ENTRIES)
            _history.value = updated
            historyRepo.save(updated)
        }
    }

    fun updateHistoryNote(id: Long, note: String) {
        val updated = _history.value.map { if (it.id == id) it.copy(note = note.take(60)) else it }
        _history.value = updated
        historyRepo.save(updated)
    }

    fun deleteHistory(id: Long) {
        val updated = _history.value.filterNot { it.id == id }
        _history.value = updated
        historyRepo.save(updated)
    }

    fun clearHistory() {
        _history.value = emptyList()
        historyRepo.save(emptyList())
    }

    fun openHistoryInCalculator(e: HistoryEntry) {
        val cat = LoanCategory.values().firstOrNull { it.name == e.category } ?: LoanCategory.HOME
        _loanCategory.value = cat
        prefs.loanCategory = cat
        if (cat.hasDownPayment && e.assetPrice > 0.0) {
            _assetPrice.value = e.assetPrice
            _downPayment.value = e.downPayment
        }
        updatePrincipal(e.principal)
        updateAnnualRate(e.annualRate)
        updateTenure(if (e.tenureMonths % 12 == 0) e.tenureMonths / 12 else e.tenureMonths, e.tenureMonths % 12 == 0)
        _showHistory.value = false
        _selectedTab.value = 0
    }

    // Language & Theme State
    private val _currentLanguage = MutableStateFlow(prefs.selectedLanguage)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.themeMode)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow(prefs.accentColor)
    val accentColor: StateFlow<AccentColor> = _accentColor.asStateFlow()

    private val _selectedCurrency = MutableStateFlow(
        com.aksh.emicalculator.data.AvailableCurrencies.firstOrNull { it.code == prefs.currencyCode }
            ?: com.aksh.emicalculator.data.AvailableCurrencies[1]
    ).also { EmiMathUtils.activeCurrency = it.value }
    val selectedCurrency: StateFlow<com.aksh.emicalculator.data.Currency> = _selectedCurrency.asStateFlow()

    fun setSelectedCurrency(currency: com.aksh.emicalculator.data.Currency) {
        EmiMathUtils.activeCurrency = currency
        _selectedCurrency.value = currency
        prefs.currencyCode = currency.code
    }

    private val _showLanguageDialog = MutableStateFlow(prefs.isFirstLaunch)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    private val _showThemeDialog = MutableStateFlow(false)
    val showThemeDialog: StateFlow<Boolean> = _showThemeDialog.asStateFlow()

    // Navigation Tab (0: Calculator, 1: Compare, 2: Prepayment, 3: Schedule, 4: Tools)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Loan Inputs (Starting with 0)
    private val _loanCategory = MutableStateFlow(LoanCategory.HOME)
    val loanCategory: StateFlow<LoanCategory> = _loanCategory.asStateFlow()

    private val _principal = MutableStateFlow(0.0)
    val principal: StateFlow<Double> = _principal.asStateFlow()

    private val _annualRate = MutableStateFlow(0.0)
    val annualRate: StateFlow<Double> = _annualRate.asStateFlow()

    private val _tenureMonths = MutableStateFlow(0)
    val tenureMonths: StateFlow<Int> = _tenureMonths.asStateFlow()

    private val _assetPrice = MutableStateFlow(0.0)
    val assetPrice: StateFlow<Double> = _assetPrice.asStateFlow()

    private val _downPayment = MutableStateFlow(0.0)
    val downPayment: StateFlow<Double> = _downPayment.asStateFlow()

    private val _isTenureInYears = MutableStateFlow(true)
    val isTenureInYears: StateFlow<Boolean> = _isTenureInYears.asStateFlow()

    // EMI Calculation Result
    private val _emiResult = MutableStateFlow(
        EmiMathUtils.calculateEmi(0.0, 0.0, 0)
    )
    val emiResult: StateFlow<EmiCalculation> = _emiResult.asStateFlow()

    // Loan Comparison State (Starting with 0)
    private val _compareLoanA = MutableStateFlow(
        LoanOfferInput(name = "Bank A (SBI)", principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0, processingFeePercent = 0.5)
    )
    val compareLoanA: StateFlow<LoanOfferInput> = _compareLoanA.asStateFlow()

    private val _compareLoanB = MutableStateFlow(
        LoanOfferInput(name = "Bank B (HDFC)", principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0, processingFeePercent = 0.5)
    )
    val compareLoanB: StateFlow<LoanOfferInput> = _compareLoanB.asStateFlow()

    private val _comparisonResult = MutableStateFlow(
        EmiMathUtils.compareLoans(_compareLoanA.value, _compareLoanB.value)
    )
    val comparisonResult: StateFlow<ComparisonResult> = _comparisonResult.asStateFlow()

    // Prepayment State (Starting with 0)
    private val _prepaymentOriginalLoan = MutableStateFlow(0.0)
    val prepaymentOriginalLoan: StateFlow<Double> = _prepaymentOriginalLoan.asStateFlow()

    private val _prepaymentRate = MutableStateFlow(0.0)
    val prepaymentRate: StateFlow<Double> = _prepaymentRate.asStateFlow()

    private val _prepaymentTenureMonths = MutableStateFlow(0)
    val prepaymentTenureMonths: StateFlow<Int> = _prepaymentTenureMonths.asStateFlow()

    private val _prepaymentIsTenureInYears = MutableStateFlow(true)
    val prepaymentIsTenureInYears: StateFlow<Boolean> = _prepaymentIsTenureInYears.asStateFlow()

    private val _emisAlreadyPaid = MutableStateFlow(0)
    val emisAlreadyPaid: StateFlow<Int> = _emisAlreadyPaid.asStateFlow()

    private val _prepaymentType = MutableStateFlow(PrepaymentType.MONTHLY_EXTRA)
    val prepaymentType: StateFlow<PrepaymentType> = _prepaymentType.asStateFlow()

    private val _extraAmount = MutableStateFlow(0.0)
    val extraAmount: StateFlow<Double> = _extraAmount.asStateFlow()

    private val _prepaymentMode = MutableStateFlow(PrepaymentMode.REDUCE_TENURE)
    val prepaymentMode: StateFlow<PrepaymentMode> = _prepaymentMode.asStateFlow()

    private val _customPayments = MutableStateFlow<List<CustomPrepayment>>(emptyList())
    val customPayments: StateFlow<List<CustomPrepayment>> = _customPayments.asStateFlow()

    private val _prepaymentImpact = MutableStateFlow(
        EmiMathUtils.calculatePrepayment(
            principal = 0.0,
            annualRatePercent = 0.0,
            tenureMonths = 0,
            prepaymentType = _prepaymentType.value,
            extraAmount = 0.0,
            emisAlreadyPaid = 0,
            mode = PrepaymentMode.REDUCE_TENURE,
            customPayments = emptyList()
        )
    )
    val prepaymentImpact: StateFlow<PrepaymentImpact> = _prepaymentImpact.asStateFlow()

    // Amortization Schedule
    private val _amortizationSchedule = MutableStateFlow<Pair<List<AmortizationYear>, List<AmortizationMonth>>>(
        Pair(emptyList(), emptyList())
    )
    val amortizationSchedule: StateFlow<Pair<List<AmortizationYear>, List<AmortizationMonth>>> = _amortizationSchedule.asStateFlow()

    init {
        _history.value = historyRepo.load()
        recalculate()
        historyArmed = true
    }

    private fun updateComparisonInternal() {
        _comparisonResult.value = EmiMathUtils.compareLoans(_compareLoanA.value, _compareLoanB.value)
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        if (tab == 1 || tab == 2) {
            syncPrepaymentWithCalculator()
            _compareLoanA.value = _compareLoanA.value.copy(
                principal = _principal.value,
                annualRatePercent = _annualRate.value,
                tenureMonths = _tenureMonths.value
            )
            if (_compareLoanB.value.principal == 0.0 || _compareLoanB.value.tenureMonths == 0) {
                _compareLoanB.value = _compareLoanB.value.copy(
                    principal = _principal.value,
                    annualRatePercent = _annualRate.value,
                    tenureMonths = _tenureMonths.value
                )
            }
            updateComparisonInternal()
        }
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.selectedLanguage = language
        prefs.isFirstLaunch = false
        _showLanguageDialog.value = false
    }

    fun setTheme(mode: ThemeMode, accent: AccentColor) {
        _themeMode.value = mode
        prefs.themeMode = mode
        _accentColor.value = accent
        prefs.accentColor = accent
        _showThemeDialog.value = false
    }

    fun setShowLanguageDialog(show: Boolean) {
        _showLanguageDialog.value = show
    }

    fun setShowThemeDialog(show: Boolean) {
        _showThemeDialog.value = show
    }

    fun updateAssetPrice(price: Double) {
        _assetPrice.value = price.coerceAtLeast(0.0)
        if (_downPayment.value > _assetPrice.value) _downPayment.value = _assetPrice.value
        updatePrincipal(_assetPrice.value - _downPayment.value)
    }

    fun updateDownPayment(amount: Double) {
        _downPayment.value = amount.coerceIn(0.0, _assetPrice.value)
        updatePrincipal(_assetPrice.value - _downPayment.value)
    }

    fun setLoanCategory(category: LoanCategory) {
        _loanCategory.value = category
        prefs.loanCategory = category
        if (category.hasDownPayment) {
            _assetPrice.value = 0.0
            _downPayment.value = 0.0
        }
        _principal.value = 0.0
        _annualRate.value = 0.0
        _isTenureInYears.value = true
        _tenureMonths.value = 0

        syncPrepaymentWithCalculator()
        _compareLoanA.value = _compareLoanA.value.copy(
            principal = 0.0,
            annualRatePercent = 0.0,
            tenureMonths = 0
        )
        _compareLoanB.value = _compareLoanB.value.copy(
            principal = 0.0,
            annualRatePercent = 0.0,
            tenureMonths = 0
        )

        recalculate()
    }

    fun updatePrincipal(newPrincipal: Double) {
        _principal.value = newPrincipal.coerceAtLeast(0.0)
        _compareLoanA.value = _compareLoanA.value.copy(principal = _principal.value)
        _compareLoanB.value = _compareLoanB.value.copy(principal = _principal.value)
        syncPrepaymentWithCalculator()
        recalculate()
    }

    fun updateAnnualRate(newRate: Double) {
        _annualRate.value = newRate.coerceIn(0.0, 36.0)
        _compareLoanA.value = _compareLoanA.value.copy(annualRatePercent = _annualRate.value)
        syncPrepaymentWithCalculator()
        recalculate()
    }

    fun updateTenure(number: Int, isYears: Boolean) {
        _isTenureInYears.value = isYears
        val months = if (isYears) number * 12 else number
        _tenureMonths.value = months.coerceIn(0, 480)
        _compareLoanA.value = _compareLoanA.value.copy(tenureMonths = _tenureMonths.value)
        _compareLoanB.value = _compareLoanB.value.copy(tenureMonths = _tenureMonths.value)
        syncPrepaymentWithCalculator()
        recalculate()
    }

    fun toggleTenureUnit() {
        val newUnitIsYears = !_isTenureInYears.value
        _isTenureInYears.value = newUnitIsYears
        recalculate()
    }

    /**
     * Main EMI calculator ko clear karta hai (sab inputs 0 pe wapas), jisse
     * result chart (gol wala donut chart) bhi wapas gayab ho jaata hai
     * kyunki woh emiResult.monthlyEmi > 0 par hi dikhta hai.
     */
    fun clearEmiCalculator() {
        _principal.value = 0.0
        _annualRate.value = 0.0
        _tenureMonths.value = 0
        _isTenureInYears.value = true
        if (_loanCategory.value.hasDownPayment) {
            _assetPrice.value = 0.0
            _downPayment.value = 0.0
        }
        _compareLoanA.value = _compareLoanA.value.copy(principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0)
        _compareLoanB.value = _compareLoanB.value.copy(principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0)
        updateComparisonInternal()
        syncPrepaymentWithCalculator()
        recalculate()
    }

    private var recalcJob: kotlinx.coroutines.Job? = null

    private fun recalculate() {
        _emiResult.value = EmiMathUtils.calculateEmi(_principal.value, _annualRate.value, _tenureMonths.value)
        // Purane data ko save karna band kar diya gaya hai
        scheduleHistorySave()

        val principal = _principal.value
        val rate = _annualRate.value
        val tenure = _tenureMonths.value
        recalcJob?.cancel()
        recalcJob = viewModelScope.launch(kotlinx.coroutines.Dispatchers.Default) {
            kotlinx.coroutines.delay(60)
            val schedule = EmiMathUtils.generateAmortizationSchedule(principal, rate, tenure)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                _amortizationSchedule.value = schedule
            }
        }
    }

    private fun recalculatePrepaymentInternal() {
        _prepaymentImpact.value = EmiMathUtils.calculatePrepayment(
            principal = _prepaymentOriginalLoan.value,
            annualRatePercent = _prepaymentRate.value,
            tenureMonths = _prepaymentTenureMonths.value,
            prepaymentType = _prepaymentType.value,
            extraAmount = _extraAmount.value,
            emisAlreadyPaid = _emisAlreadyPaid.value,
            mode = _prepaymentMode.value,
            customPayments = _customPayments.value
        )
    }

    fun updateLoanA(principal: Double, rate: Double, tenureMonths: Int, feePercent: Double = 0.5) {
        _compareLoanA.value = _compareLoanA.value.copy(
            principal = principal,
            annualRatePercent = rate,
            tenureMonths = tenureMonths,
            processingFeePercent = feePercent
        )
        updateComparisonInternal()
    }

    fun updateLoanB(principal: Double, rate: Double, tenureMonths: Int, feePercent: Double = 0.5) {
        _compareLoanB.value = _compareLoanB.value.copy(
            principal = principal,
            annualRatePercent = rate,
            tenureMonths = tenureMonths,
            processingFeePercent = feePercent
        )
        updateComparisonInternal()
    }

    fun updateLoanAName(newName: String) {
        _compareLoanA.value = _compareLoanA.value.copy(name = newName)
        _comparisonResult.value = EmiMathUtils.compareLoans(_compareLoanA.value, _compareLoanB.value)
    }

    fun updateLoanBName(newName: String) {
        _compareLoanB.value = _compareLoanB.value.copy(name = newName)
        _comparisonResult.value = EmiMathUtils.compareLoans(_compareLoanA.value, _compareLoanB.value)
    }

    fun loadBankPresets(bankARate: Double, bankAName: String, bankBRate: Double, bankBName: String) {
        _compareLoanA.value = _compareLoanA.value.copy(name = bankAName, annualRatePercent = bankARate)
        _compareLoanB.value = _compareLoanB.value.copy(name = bankBName, annualRatePercent = bankBRate)
        _comparisonResult.value = EmiMathUtils.compareLoans(_compareLoanA.value, _compareLoanB.value)
    }

    fun updatePrepaymentOriginalLoan(amount: Double) {
        _prepaymentOriginalLoan.value = amount.coerceAtLeast(0.0)
        recalculatePrepaymentInternal()
    }

    fun updatePrepaymentRate(rate: Double) {
        _prepaymentRate.value = rate.coerceIn(0.0, 36.0)
        recalculatePrepaymentInternal()
    }

    fun updatePrepaymentTenure(number: Int, isYears: Boolean) {
        _prepaymentIsTenureInYears.value = isYears
        val months = if (isYears) number * 12 else number
        _prepaymentTenureMonths.value = months.coerceIn(0, 480)
        if (_emisAlreadyPaid.value >= _prepaymentTenureMonths.value) {
            _emisAlreadyPaid.value = (_prepaymentTenureMonths.value - 1).coerceAtLeast(0)
        }
        recalculatePrepaymentInternal()
    }

    fun togglePrepaymentTenureUnit() {
        val newUnitIsYears = !_prepaymentIsTenureInYears.value
        _prepaymentIsTenureInYears.value = newUnitIsYears
    }

    fun updateEmisAlreadyPaid(count: Int) {
        val maxEmis = (_prepaymentTenureMonths.value - 1).coerceAtLeast(0)
        _emisAlreadyPaid.value = count.coerceIn(0, maxEmis)
        recalculatePrepaymentInternal()
    }

    fun updatePrepaymentType(type: PrepaymentType) {
        _prepaymentType.value = type
        recalculatePrepaymentInternal()
    }

    fun updatePrepaymentMode(mode: PrepaymentMode) {
        _prepaymentMode.value = mode
        recalculatePrepaymentInternal()
    }

    fun addCustomPayment(month: Int, amount: Double) {
        if (month < 1 || amount <= 0.0) return
        val maxMonth = (_prepaymentTenureMonths.value - _emisAlreadyPaid.value).coerceAtLeast(1)
        val m = month.coerceIn(1, maxMonth)
        _customPayments.value = (_customPayments.value.filterNot { it.month == m } + CustomPrepayment(m, amount))
            .sortedBy { it.month }.take(120)
        recalculatePrepaymentInternal()
    }

    fun removeCustomPayment(month: Int) {
        _customPayments.value = _customPayments.value.filterNot { it.month == month }
        recalculatePrepaymentInternal()
    }

    fun updatePrepaymentExtraAmount(amount: Double) {
        _extraAmount.value = amount.coerceAtLeast(0.0)
        recalculatePrepaymentInternal()
    }

    fun updatePrepayment(type: PrepaymentType, amount: Double, emisPaid: Int = _emisAlreadyPaid.value) {
        _prepaymentType.value = type
        _extraAmount.value = amount.coerceAtLeast(0.0)
        _emisAlreadyPaid.value = emisPaid.coerceIn(0, (_prepaymentTenureMonths.value - 1).coerceAtLeast(0))
        recalculatePrepaymentInternal()
    }

    /** Prepayment screen ko clear karta hai (sab inputs 0 pe wapas). */
    fun clearPrepayment() {
        _prepaymentOriginalLoan.value = 0.0
        _prepaymentRate.value = 0.0
        _prepaymentTenureMonths.value = 0
        _prepaymentIsTenureInYears.value = true
        _emisAlreadyPaid.value = 0
        _prepaymentType.value = PrepaymentType.MONTHLY_EXTRA
        _extraAmount.value = 0.0
        _prepaymentMode.value = PrepaymentMode.REDUCE_TENURE
        _customPayments.value = emptyList()
        recalculatePrepaymentInternal()
    }

    /** Compare Loans screen ko clear karta hai (dono offers 0 pe wapas). */
    fun clearCompareLoans() {
        _compareLoanA.value = _compareLoanA.value.copy(
            principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0, processingFeePercent = 0.5
        )
        _compareLoanB.value = _compareLoanB.value.copy(
            principal = 0.0, annualRatePercent = 0.0, tenureMonths = 0, processingFeePercent = 0.5
        )
        updateComparisonInternal()
    }

    fun syncPrepaymentWithCalculator() {
        _prepaymentOriginalLoan.value = _principal.value
        _prepaymentRate.value = _annualRate.value
        _prepaymentTenureMonths.value = _tenureMonths.value
        _prepaymentIsTenureInYears.value = _isTenureInYears.value
        if (_emisAlreadyPaid.value >= _prepaymentTenureMonths.value) {
            _emisAlreadyPaid.value = 12.coerceAtMost((_prepaymentTenureMonths.value - 1).coerceAtLeast(0))
        }
        recalculatePrepaymentInternal()
    }
}