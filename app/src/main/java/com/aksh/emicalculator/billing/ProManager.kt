package com.aksh.emicalculator.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** One purchasable plan (monthly / yearly) as returned by Google Play. */
data class PlanInfo(
    val basePlanId: String,
    val details: ProductDetails,
    val offerToken: String,
    val formattedPrice: String,
    val priceMicros: Long
)

/**
 * Keeps the "is the user Pro?" state.
 * - Last known state is cached, so Pro users never see locks while the app starts.
 * - Google Play is asked in the background (restores Pro automatically after reinstall / new phone).
 */
object ProManager : PurchasesUpdatedListener {

    private const val PREFS = "pro_state"
    private const val KEY_PRO = "is_pro"

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private val _plans = MutableStateFlow<Map<String, PlanInfo>>(emptyMap())
    val plans: StateFlow<Map<String, PlanInfo>> = _plans.asStateFlow()

    /** Paywall screen visible? Set to true from anywhere to open it. */
    val showPaywall = MutableStateFlow(false)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    fun clearMessage() { _message.value = null }

    private var client: BillingClient? = null
    private var appContext: Context? = null

    /** Cheap: only reads a cached boolean. Call in onCreate. */
    fun init(context: Context) {
        appContext = context.applicationContext
        val cached = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PRO, false)
        _isPro.value = ProConfig.testUnlockActive || cached
    }

    /** Connects to Google Play Billing (call a moment after the app is visible). */
    fun connect() {
        val ctx = appContext ?: return
        if (client?.isReady == true) { refreshPurchases(); return }
        try {
            val c = BillingClient.newBuilder(ctx)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .build()
            client = c
            c.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    try {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            queryPlans()
                            refreshPurchases()
                        }
                    } catch (t: Throwable) {
                        android.util.Log.w("ProManager", "Billing setup callback failed", t)
                    }
                }
                override fun onBillingServiceDisconnected() { /* reconnect on next connect() */ }
            })
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "Billing unavailable", t)
        }
    }

    private fun queryPlans() {
        val c = client ?: return
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(ProConfig.PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        try {
            c.queryProductDetailsAsync(
                QueryProductDetailsParams.newBuilder().setProductList(listOf(product)).build()
            ) { result, detailsList ->
                try {
                    if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryProductDetailsAsync
                    val details = detailsList.firstOrNull() ?: return@queryProductDetailsAsync
                    val offers = details.subscriptionOfferDetails ?: return@queryProductDetailsAsync
                    val map = mutableMapOf<String, PlanInfo>()
                    offers.groupBy { it.basePlanId }.forEach { (planId, list) ->
                        // prefer a trial/intro offer (only returned if the user is eligible), else the plain base plan
                        val offer = list.firstOrNull { it.offerId != null } ?: list.firstOrNull() ?: return@forEach
                        val recurring = offer.pricingPhases.pricingPhaseList.lastOrNull() ?: return@forEach
                        map[planId] = PlanInfo(
                            basePlanId = planId,
                            details = details,
                            offerToken = offer.offerToken,
                            formattedPrice = recurring.formattedPrice,
                            priceMicros = recurring.priceAmountMicros
                        )
                    }
                    _plans.value = map
                } catch (t: Throwable) {
                    android.util.Log.w("ProManager", "Plan parsing failed", t)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "queryPlans failed", t)
        }
    }

    /** Asks Google Play which subscriptions the user owns (also used for "Restore purchase"). */
    fun refreshPurchases() {
        val c = client ?: return
        if (!c.isReady) return
        try {
            c.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
            ) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    applyPurchases(purchases, authoritative = true)
                }
            }
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "refreshPurchases failed", t)
        }
    }

    private fun applyPurchases(purchases: List<Purchase>, authoritative: Boolean) {
        try {
            val active = purchases.filter {
                it.purchaseState == Purchase.PurchaseState.PURCHASED && it.products.contains(ProConfig.PRODUCT_ID)
            }
            active.filterNot { it.isAcknowledged }.forEach { p ->
                try {
                    client?.acknowledgePurchase(
                        AcknowledgePurchaseParams.newBuilder().setPurchaseToken(p.purchaseToken).build()
                    ) { }
                } catch (t: Throwable) {
                    android.util.Log.w("ProManager", "acknowledge failed", t)
                }
            }
            if (active.isNotEmpty() || authoritative) setPro(active.isNotEmpty())
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "applyPurchases failed", t)
        }
    }

    private fun setPro(value: Boolean) {
        appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)?.edit()?.putBoolean(KEY_PRO, value)?.apply()
        _isPro.value = ProConfig.testUnlockActive || value
    }

    /** Starts the Google Play purchase screen for "monthly" or "yearly". */
    fun purchase(activity: Activity, basePlanId: String) {
        val c = client
        val plan = _plans.value[basePlanId]
        if (c == null || !c.isReady || plan == null) {
            connect()
            _message.value = "unavailable"
            return
        }
        try {
            val params = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(plan.details)
                .setOfferToken(plan.offerToken)
                .build()
            c.launchBillingFlow(
                activity,
                BillingFlowParams.newBuilder().setProductDetailsParamsList(listOf(params)).build()
            )
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "launchBillingFlow failed", t)
            _message.value = "unavailable"
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        try {
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                applyPurchases(purchases, authoritative = false)
                if (_isPro.value) showPaywall.value = false
            }
        } catch (t: Throwable) {
            android.util.Log.w("ProManager", "onPurchasesUpdated failed", t)
        }
    }
}
