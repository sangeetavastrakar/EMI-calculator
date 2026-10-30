package com.aksh.emicalculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.google.android.gms.ads.AdSize

/** Separate, labelled ad area: never overlays content or clips an ad creative. */
@Composable
fun AdBannerSlot(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    language: AppLanguage = AppLanguage.ENGLISH,
    inline: Boolean = false
) {
    val pro by ProManager.isPro.collectAsState()
    val sdkReady by AdConfig.ready.collectAsState()
    if (!enabled || pro || !sdkReady) return
    val context = LocalContext.current
    BoxWithConstraints(modifier.fillMaxWidth().padding(horizontal = 8.dp), contentAlignment = Alignment.Center) {
        val widthDp = maxWidth.value.toInt().coerceAtMost(600)
        if (widthDp >= 320) {
            val orientation = androidx.compose.ui.platform.LocalConfiguration.current.orientation
            val size = remember(widthDp, orientation, inline) {
                if (inline) AdSize.BANNER
                else AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)
            }
            var failed by remember(size) { mutableStateOf(false) }
            Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()
                .testTag(if (inline) "inline_ad_banner" else "bottom_ad_banner")) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = if (failed) 0.dp else 8.dp)) {
                    if (!failed) {
                        Text(if (language == AppLanguage.HINDI) "विज्ञापन" else "Advertisement",
                            fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                    }
                    // Keep the failed view composed at zero height; do not create a retry loop.
                    AdMobBanner(AdConfig.BANNER_AD_UNIT_ID, size,
                        modifier = Modifier.width(size.width.dp).height(if (failed) 0.dp else size.height.dp),
                        onLoadState = { loaded -> failed = !loaded })
                }
            }
        }
    }
}
