package com.aksh.emicalculator.ui.components

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ===== ALL AD SETTINGS ARE HERE =====
 * Right now the app uses Google TEST ads.
 * To go live: (1) put your App ID in AndroidManifest.xml, (2) set USE_REAL_ADS = true below.
 */
object AdConfig {
    // Pro (paid) users do not see the banner. This is the whole point of the subscription now
    // (all tools are free for everyone — Pro only means "no ads" + VIP support), so keep this true.
    const val HIDE_ADS_FOR_PRO = true

    // false = Google TEST ads (safe)   |   true = your REAL ads (use for the Play Store release)
    const val USE_REAL_ADS = false

    // Google's official TEST banner ad unit id
    private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // YOUR real banner ad unit id (from AdMob)
    private const val REAL_BANNER_AD_UNIT_ID = "ca-app-pub-7304410543484743/6711098130"

    // Google's official TEST interstitial (full-screen) ad unit id
    private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // YOUR real INTERSTITIAL ad unit id: AdMob -> Apps -> Ad units -> Add ad unit -> Interstitial.
    // Jab tak yahan real id nahi daalte, real mode me bada ad nahi dikhega (PDF/Share seedha chalega, crash nahi).
    private const val REAL_INTERSTITIAL_AD_UNIT_ID = ""

    // Google's official TEST rewarded ad unit id
    private const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // YOUR real REWARDED ad unit id: AdMob -> Apps -> Ad units -> Add ad unit -> Rewarded.
    // Jab tak yahan real id nahi daalte, real mode me "watch ad" button bhi chup rahega.
    private const val REAL_REWARDED_AD_UNIT_ID = ""

    /** Ad unit id used for the big full-screen ad shown occasionally between tabs. Empty = no ad. */
    val INTERSTITIAL_AD_UNIT_ID: String
        get() = if (USE_REAL_ADS) REAL_INTERSTITIAL_AD_UNIT_ID else TEST_INTERSTITIAL_AD_UNIT_ID

    /** Ad unit id used by the bottom banner. */
    val BANNER_AD_UNIT_ID: String
        get() = if (USE_REAL_ADS) REAL_BANNER_AD_UNIT_ID else TEST_BANNER_AD_UNIT_ID

    /** Ad unit id used by the opt-in "watch ad, get ad-free time" reward button. Empty = button hidden. */
    val REWARDED_AD_UNIT_ID: String
        get() = if (USE_REAL_ADS) REAL_REWARDED_AD_UNIT_ID else TEST_REWARDED_AD_UNIT_ID

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    /** Starts the Mobile Ads SDK off the main thread so app start stays fast. */
    fun initialize(context: Context) {
        val appContext = context.applicationContext
        Thread {
            // Ads must never be able to crash the app
            try {
                MobileAds.initialize(appContext) {
                    _ready.value = true
                    // Bada ad aur reward ad dono pehle se load karke rakho taaki dikhne me der na ho
                    InterstitialAdManager.preload(appContext)
                    RewardedAdManager.preload(appContext)
                }
            } catch (t: Throwable) {
                android.util.Log.w("AdConfig", "Ads init failed", t)
            }
        }.start()
    }
}
