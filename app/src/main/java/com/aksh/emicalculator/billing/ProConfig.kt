package com.aksh.emicalculator.billing

import com.aksh.emicalculator.BuildConfig

/**
 * ===== PRO / SUBSCRIPTION SETTINGS =====
 */
object ProConfig {

    /**
     * TEST SWITCH.
     *  true  -> Pro is unlocked for free, so you can test every Pro feature on your phone (APK builds).
     *  false -> normal behaviour (Pro only after a real purchase).
     * When you build an AAB (Android App Bundle for Play Store) this switch is IGNORED and forced
     * OFF automatically (see isBundleBuild in app/build.gradle.kts), even if you forget to set it to false.
     */
    const val TEST_UNLOCK_PRO = false

    /**
     * True only if the test switch is on AND this is a debug build (never installed from
     * Play Store) AND it's not a Play Store bundle build. BuildConfig.DEBUG is the main
     * safety net here: even if someone ships a signed release APK instead of an AAB
     * (IS_BUNDLE_BUILD would then be false), Pro still stays locked because DEBUG is
     * always false in a release build. This stops Pro from ever leaking for free.
     */
    val testUnlockActive: Boolean
        get() = TEST_UNLOCK_PRO && BuildConfig.DEBUG && !BuildConfig.IS_BUNDLE_BUILD

    // ---- Google Play Console: Monetize -> Subscriptions ----
    // Create ONE subscription with this Product ID and two base plans with these IDs.
    const val PRODUCT_ID = "premium"
    const val BASE_PLAN_MONTHLY = "monthly"
    const val BASE_PLAN_YEARLY = "yearly"

    // Shown only if Play prices are not loaded yet (real price always comes from Google Play)
    const val FALLBACK_MONTHLY_PRICE = "₹21"
    const val FALLBACK_YEARLY_PRICE = "₹151"

    // VIP support + legal links (fill these in)
    const val SUPPORT_EMAIL = "your-email@example.com"
    const val TERMS_URL = ""
    const val PRIVACY_URL = ""
}
