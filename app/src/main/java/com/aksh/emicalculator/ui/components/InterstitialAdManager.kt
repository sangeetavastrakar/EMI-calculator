package com.aksh.emicalculator.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import com.aksh.emicalculator.billing.ProManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Bada (full-screen) ad.
 *
 * Placement: ab yeh PDF/Share jaise zaroori kaam par kabhi NAHI aata (wo dono hamesha instant
 * hain) — iski jagah yeh sirf tab change jaisi "natural pause" par, kabhi kabhar dikhta hai.
 * Isse Play Store policy ke hisab se bhi safe rehta hai (koi bhi action "ad ke piche band"
 * nahi hai) aur user ko bhi nahi lagta ki har click par ad aa raha hai.
 *
 *  - Pro ya active "reward ad-free" window me KABHI nahi dikhta (RewardedAdManager dekho).
 *  - App khulte hi turant nahi dikhta — ek shuruaati "chup" gap (LAUNCH_GRACE_MS) hai.
 *  - Do ads ke beech kam se kam MIN_GAP_MS ka gap hai, taaki baar baar na aaye.
 *  - Ad ready na ho / fail ho jaye to user ka kaam turant ho jata hai (user kabhi atakta nahi).
 */
object InterstitialAdManager {

    private val main = Handler(Looper.getMainLooper())
    private var ad: InterstitialAd? = null
    private var loading = false
    private val APP_START_AT = System.currentTimeMillis()

    // App start hote hi ek chhota cooldown "pehle se bhara hua" maan lo, taaki launch ke turant
    // baad (jaise pehla hi tab switch) ad na aa jaye — Play Store spammy-ad guidelines ke against hai.
    private var lastShownAt = APP_START_AT

    /** App khulne ke kitni der baad pehla bada ad dikh sakta hai (ms). */
    private const val LAUNCH_GRACE_MS = 45_000L

    /** Do bade ads ke beech minimum gap (ms), taaki user ko spam na lage. */
    private const val MIN_GAP_MS = 120_000L

    /** Kitni calculators use ke baad ek bada ad dikhe (4th use par). */
    private const val CALC_USES_BETWEEN_ADS = 3

    private const val CALC_PREFS = "interstitial_calc_prefs"
    private const val KEY_CALC_USES = "calc_uses"

    private fun adsSuppressed(): Boolean =
        ProManager.isPro.value || RewardedAdManager.isAdFreeActive

    fun preload(context: Context) {
        val appContext = context.applicationContext
        main.post {
            try {
                if (ad != null || loading) return@post
                if (adsSuppressed()) return@post
                val unitId = AdConfig.INTERSTITIAL_AD_UNIT_ID
                if (unitId.isBlank()) return@post
                loading = true
                InterstitialAd.load(
                    appContext, unitId, AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(loaded: InterstitialAd) {
                            ad = loaded
                            loading = false
                        }
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            ad = null
                            loading = false
                        }
                    }
                )
            } catch (t: Throwable) {
                loading = false
                android.util.Log.w("InterstitialAd", "preload failed", t)
            }
        }
    }

    /**
     * [action] (jaise tab change) hamesha chalta hai — Pro/ad-free/cooldown/launch-grace me turant,
     * warna ad khatam hone ke baad. Ad available na ho to bhi [action] turant chalta hai.
     * [action] hamesha sirf ek hi baar chalta hai.
     */
    fun showThen(context: Context, action: () -> Unit) {
        var done = false
        fun runOnce() {
            if (done) return
            done = true
            try { action() } catch (t: Throwable) {
                android.util.Log.w("InterstitialAd", "action failed", t)
            }
        }
        try {
            val activity = context.findActivity()
            val current = ad
            val now = System.currentTimeMillis()
            val withinGrace = now - APP_START_AT < LAUNCH_GRACE_MS
            val tooSoon = now - lastShownAt < MIN_GAP_MS
            if (adsSuppressed() || activity == null || current == null || withinGrace || tooSoon) {
                runOnce()
                if (!adsSuppressed()) preload(context)
                return
            }
            ad = null
            current.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    lastShownAt = System.currentTimeMillis()
                    preload(context)
                    runOnce()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    preload(context)
                    runOnce()
                }
            }
            current.show(activity)
        } catch (t: Throwable) {
            android.util.Log.w("InterstitialAd", "show failed", t)
            runOnce()
        }
    }

    /**
     * Ginti karta hai ki user ne kitni baar calculator khola. Har 4th open par (3 use ke baad)
     * ek bada ad dikhta hai — aur uske baad ginti phir se 0 se shuru.
     *
     * [action] hamesha chalta hai, chahe ad ready ho ya na ho. Isliye calculator kabhi atakta nahi.
     * Ad sirf "navigate" ke waqt aata hai — kabhi bhi input, PDF ya Share ke beech nahi.
     */
    fun onCalculatorOpened(context: Context, action: () -> Unit) {
        val prefs = context.applicationContext.getSharedPreferences(CALC_PREFS, Context.MODE_PRIVATE)
        val uses = prefs.getInt(KEY_CALC_USES, 0) + 1
        if (uses > CALC_USES_BETWEEN_ADS) {
            prefs.edit().putInt(KEY_CALC_USES, 0).apply()
            showThen(context, action)
        } else {
            prefs.edit().putInt(KEY_CALC_USES, uses).apply()
            action()
        }
    }

    private fun Context.findActivity(): Activity? {
        var c: Context? = this
        while (c is ContextWrapper) {
            if (c is Activity) return c
            c = c.baseContext
        }
        return null
    }
}
