package com.aksh.emicalculator.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.aksh.emicalculator.billing.ProManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * "Watch ad, get something back" ad.
 *
 * Yeh ad KABHI apne aap nahi khulta — user khud ek button dabakar shuru karta hai (fully opt-in,
 * Play Store/AdMob reward-ad policy ke mutabik). Poora video dekhne ke BAAD hi reward milta hai:
 * agle [AD_FREE_MINUTES] minute ke liye sirf full-screen (interstitial) ads band hote hain; banner chalta rehta hai,
 * jaise user ne thodi der ke liye Pro le liya ho. Beech me ad band kar di ya skip kar di to
 * kuch nahi milta — yeh AdMob policy ki zaroorat hai.
 */
object RewardedAdManager {

    private const val PREFS = "reward_ad_state"
    private const val KEY_AD_FREE_UNTIL = "ad_free_until"

    /** Reward dekhne ke baad kitni der ke liye full-screen ads band rahenge. */
    const val AD_FREE_MINUTES = 10L

    private val main = Handler(Looper.getMainLooper())
    private var ad: RewardedAd? = null
    private var loading = false
    private var prefs: SharedPreferences? = null

    private val _adFreeUntil = MutableStateFlow(0L)
    /** UI isko collect karke "X minute ads-free baaki hai" jaisa dikha sakti hai. */
    val adFreeUntil: StateFlow<Long> = _adFreeUntil.asStateFlow()

    private val _ready = MutableStateFlow(false)
    /** True jab ek reward ad dekhne ke liye taiyaar hai (button enable karne ke liye). */
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    /** Abhi reward se mila "ads band" window chal raha hai kya. */
    val isAdFreeActive: Boolean
        get() = System.currentTimeMillis() < _adFreeUntil.value

    /** Call once from MainActivity.onCreate — purana ad-free window (agar hai) load karta hai. */
    fun init(context: Context) {
        val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs = p
        _adFreeUntil.value = p.getLong(KEY_AD_FREE_UNTIL, 0L)
    }

    fun preload(context: Context) {
        val appContext = context.applicationContext
        main.post {
            try {
                if (ad != null || loading) return@post
                // Pro users ko reward ad dikhane ki zaroorat nahi — unke paas already no-ads hai.
                if (ProManager.isPro.value) return@post
                val unitId = AdConfig.REWARDED_AD_UNIT_ID
                if (unitId.isBlank()) return@post
                loading = true
                RewardedAd.load(
                    appContext, unitId, AdRequest.Builder().build(),
                    object : RewardedAdLoadCallback() {
                        override fun onAdLoaded(loaded: RewardedAd) {
                            ad = loaded
                            loading = false
                            _ready.value = true
                        }
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            ad = null
                            loading = false
                            _ready.value = false
                        }
                    }
                )
            } catch (t: Throwable) {
                loading = false
                _ready.value = false
                android.util.Log.w("RewardedAd", "preload failed", t)
            }
        }
    }

    /**
     * Reward ad dikhata hai. Poora dekhne ke baad hi [onEarned] milta hai aur ads band ho jaati
     * hain; beech me chhod diya ya ad available nahi to [onUnavailable] milta hai, kuch nahi badalta.
     */
    fun show(context: Context, onEarned: () -> Unit, onUnavailable: () -> Unit) {
        try {
            val activity = context.findActivity()
            val current = ad
            if (activity == null || current == null || ProManager.isPro.value) {
                onUnavailable()
                if (!ProManager.isPro.value) preload(context)
                return
            }
            ad = null
            _ready.value = false
            var earned = false
            current.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    preload(context)
                    if (earned) grantAdFree(onEarned) else onUnavailable()
                }
                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    preload(context)
                    onUnavailable()
                }
            }
            current.show(activity) { earned = true }
        } catch (t: Throwable) {
            android.util.Log.w("RewardedAd", "show failed", t)
            onUnavailable()
        }
    }

    private fun grantAdFree(onEarned: () -> Unit) {
        val until = System.currentTimeMillis() + AD_FREE_MINUTES * 60_000L
        _adFreeUntil.value = until
        prefs?.edit()?.putLong(KEY_AD_FREE_UNTIL, until)?.apply()
        main.post { onEarned() }
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
