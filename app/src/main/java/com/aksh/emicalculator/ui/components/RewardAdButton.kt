package com.aksh.emicalculator.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.TranslationRepository

/**
 * Opt-in reward ad entry point, shown just above the banner ad.
 *  - Free, not-yet-ad-free user -> "Watch Ad: Remove Ads" row (user taps to start, never auto-plays).
 *  - Reward currently active     -> small green "ads off" chip with minutes left.
 *  - Pro user                   -> nothing (they already have no ads).
 * This is the only ad in the whole app that the user chooses to watch, which is what makes it
 * feel like a reward instead of a punishment.
 */
@Composable
fun RewardAdButton(language: AppLanguage, modifier: Modifier = Modifier) {
    val isPro by ProManager.isPro.collectAsState()
    if (isPro) return

    val context = LocalContext.current
    val ready by RewardedAdManager.ready.collectAsState()
    val adFreeUntil by RewardedAdManager.adFreeUntil.collectAsState()

    // Live countdown: update every second and switch back to the watch-ad action at zero.
    var now by androidx.compose.runtime.remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(adFreeUntil) {
        while (adFreeUntil > System.currentTimeMillis()) {
            now = System.currentTimeMillis()
            kotlinx.coroutines.delay(1_000L)
        }
        now = System.currentTimeMillis()
    }

    fun t(key: String) = TranslationRepository.getString(language, key)
    val secondsLeft = ((adFreeUntil - now + 999L) / 1_000L).coerceAtLeast(0L)
    val countdown = String.format(java.util.Locale.US, "%d:%02d", secondsLeft / 60L, secondsLeft % 60L)

    if (now < adFreeUntil) {
        Surface(
            modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier)
                Text(
                    t(ExtraStrings.REWARD_AD_ACTIVE) + " · " + countdown,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        return
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("reward_ad_button")
            .clickable {
                RewardedAdManager.show(
                    context = context,
                    onEarned = {
                        Toast.makeText(context, t(ExtraStrings.REWARD_AD_THANKS), Toast.LENGTH_LONG).show()
                    },
                    onUnavailable = {
                        Toast.makeText(context, t(ExtraStrings.REWARD_AD_UNAVAILABLE), Toast.LENGTH_SHORT).show()
                    }
                )
            },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.PlayCircle, null, tint = MaterialTheme.colorScheme.secondary)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    t(ExtraStrings.REWARD_AD_BTN), fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer, maxLines = 1
                )
                Text(
                    t(ExtraStrings.REWARD_AD_SUB), fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f), maxLines = 1
                )
            }
        }
    }

    // Keep a reward ad ready in the background so the button works the first time it's tapped.
    LaunchedEffect(ready) {
        if (!ready) RewardedAdManager.preload(context)
    }
}
