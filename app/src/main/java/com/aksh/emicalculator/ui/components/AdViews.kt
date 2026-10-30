package com.aksh.emicalculator.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/** One request per slot/size. AdMob manages refresh; release views with the screen. */
@Composable
fun AdMobBanner(unitId: String, size: AdSize, modifier: Modifier = Modifier, onLoadState: (Boolean) -> Unit = {}) {
    val ready by AdConfig.ready.collectAsState()
    if (!ready) return
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val callback by rememberUpdatedState(onLoadState)
    val adView = remember(context, unitId, size) {
        runCatching { AdView(context).apply { setAdSize(size); adUnitId = unitId } }.getOrNull()
    }
    if (adView == null) {
        LaunchedEffect(Unit) { callback(false) }
        return
    }
    key(adView) { AndroidView(modifier = modifier, factory = { adView }) }
    DisposableEffect(adView, owner) {
        var disposed = false
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() { if (!disposed) callback(true) }
            override fun onAdFailedToLoad(error: LoadAdError) { if (!disposed) callback(false) }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adView.resume()
                Lifecycle.Event.ON_PAUSE -> adView.pause()
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        runCatching {
            adView.loadAd(AdRequest.Builder().build())
            if (!owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) adView.pause()
        }.onFailure { callback(false) }
        onDispose {
            disposed = true
            owner.lifecycle.removeObserver(observer)
            adView.adListener = object : AdListener() {}
            adView.destroy()
        }
    }
}
