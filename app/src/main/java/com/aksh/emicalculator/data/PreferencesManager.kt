package com.aksh.emicalculator.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("aksh_emi_calculator_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FIRST_LAUNCH = "key_first_launch"
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_SAVED_AMOUNT = "key_saved_amount"
        private const val KEY_SAVED_RATE = "key_saved_rate"
        private const val KEY_SAVED_TENURE = "key_saved_tenure"
        private const val KEY_ACCENT_COLOR = "key_accent_color"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_PRICE = "key_asset_price"
        private const val KEY_DOWN = "key_down_payment"
        private const val KEY_CATEGORY = "key_loan_category"
        private const val KEY_LAST_PAYWALL_DATE = "key_last_paywall_date"
    }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    var selectedLanguage: AppLanguage
        get() {
            val code = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
            return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.ENGLISH
        }
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value.code).apply()

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name) ?: ThemeMode.DARK.name
            return try {
                ThemeMode.valueOf(name)
            } catch (e: Exception) {
                if (name == "CUSTOM") ThemeMode.DARK else ThemeMode.SYSTEM
            }
        }
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value.name).apply()

    fun saveLastCalculation(amount: Double, rate: Double, tenureMonths: Int) {
        prefs.edit()
            .putFloat(KEY_SAVED_AMOUNT, amount.toFloat())
            .putFloat(KEY_SAVED_RATE, rate.toFloat())
            .putInt(KEY_SAVED_TENURE, tenureMonths)
            .apply()
    }

    fun getLastAmount(): Double =
        prefs.getFloat(KEY_SAVED_AMOUNT, 0f).toDouble().takeIf { it > 0.0 } ?: LoanCategory.HOME.defaultAmount
    fun getLastRate(): Double =
        prefs.getFloat(KEY_SAVED_RATE, 0f).toDouble().takeIf { it > 0.0 } ?: LoanCategory.HOME.defaultRate
    fun getLastTenure(): Int =
        prefs.getInt(KEY_SAVED_TENURE, 0).takeIf { it > 0 } ?: (LoanCategory.HOME.defaultTenureYears * 12)

    var currencyCode: String
        get() = prefs.getString(KEY_CURRENCY, "INR") ?: "INR"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    var accentColor: AccentColor
        get() {
            val name = prefs.getString(KEY_ACCENT_COLOR, AccentColor.ROYAL_BLUE.name) ?: AccentColor.ROYAL_BLUE.name
            return try {
                AccentColor.valueOf(name)
            } catch (e: Exception) {
                AccentColor.ROYAL_BLUE
            }
        }
        set(value) = prefs.edit().putString(KEY_ACCENT_COLOR, value.name).apply()

    fun saveDownPayment(price: Double, down: Double) {
        prefs.edit().putFloat(KEY_PRICE, price.toFloat()).putFloat(KEY_DOWN, down.toFloat()).apply()
    }
    fun getLastPrice(): Double = prefs.getFloat(KEY_PRICE, 0f).toDouble()
    fun getLastDown(): Double = prefs.getFloat(KEY_DOWN, 0f).toDouble()

    var loanCategory: LoanCategory
        get() = LoanCategory.values().firstOrNull { it.name == prefs.getString(KEY_CATEGORY, null) } ?: LoanCategory.HOME
        set(value) = prefs.edit().putString(KEY_CATEGORY, value.name).apply()

    // Din me ek baar Paywall dikhane ka logic
    fun shouldShowDailyPaywall(): Boolean {
        val lastDate = prefs.getString(KEY_LAST_PAYWALL_DATE, "") ?: ""
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return if (lastDate != todayDate) {
            // Aaj ki date save kar do taaki usi din dobara na khule
            prefs.edit().putString(KEY_LAST_PAYWALL_DATE, todayDate).apply()
            true
        } else {
            false
        }
    }
}