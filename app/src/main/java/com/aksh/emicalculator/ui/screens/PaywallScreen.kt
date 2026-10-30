package com.aksh.emicalculator.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.billing.ProConfig
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.viewmodel.EmiViewModel

private val ProBlue = Color(0xFF1565E0)
private val ProPurple = Color(0xFF5B3FD6)

private fun Context.findActivity(): Activity? {
    var c = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

private fun openUrl(context: Context, url: String) {
    if (url.isBlank()) return
    try { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (_: Exception) {}
}

private data class Feature(val icon: ImageVector, val tint: Color, val title: String, val sub: String, val basic: Boolean)

@Composable
fun PaywallScreen(viewModel: EmiViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val isPro by ProManager.isPro.collectAsState()
    val plans by ProManager.plans.collectAsState()
    val message by ProManager.message.collectAsState()
    val context = LocalContext.current
    fun t(key: String) = TranslationRepository.getString(language, key)

    BackHandler { ProManager.showPaywall.value = false }
    LaunchedEffect(message) {
        if (message != null) {
            Toast.makeText(context, t(ExtraStrings.PW_UNAVAILABLE), Toast.LENGTH_LONG).show()
            ProManager.clearMessage()
        }
    }

    val monthly = plans[ProConfig.BASE_PLAN_MONTHLY]
    val yearly = plans[ProConfig.BASE_PLAN_YEARLY]
    val monthlyPrice = monthly?.formattedPrice ?: ProConfig.FALLBACK_MONTHLY_PRICE
    val yearlyPrice = yearly?.formattedPrice ?: ProConfig.FALLBACK_YEARLY_PRICE
    val savePct = if (monthly != null && yearly != null && monthly.priceMicros > 0)
        (100 - yearly.priceMicros * 100 / (monthly.priceMicros * 12)).toInt().coerceIn(1, 99) else 40

    // Basic and Pro differ only by advertising. Other calculator features stay available on Basic.
    val features = listOf(
        Feature(Icons.Default.Block, ProPurple,
            if (language == AppLanguage.HINDI) "कोई विज्ञापन नहीं" else "No Ads",
            if (language == AppLanguage.HINDI) "बैनर और सभी बड़े विज्ञापन हटें" else "Removes banners and all full-screen ads", false),
        Feature(Icons.Default.Star, ProPurple, t(ExtraStrings.PW_F_VIP),
            if (language == AppLanguage.HINDI) "आपके सवालों के लिए प्राथमिकता सहायता" else "Priority help with your questions", false)
    )

    Surface(modifier = Modifier.fillMaxSize().testTag("paywall_screen"), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { ProManager.showPaywall.value = false },
                        modifier = Modifier.align(Alignment.CenterStart).testTag("paywall_close")
                    ) { Icon(Icons.Default.Close, contentDescription = "Close") }
                    Icon(
                        Icons.Default.Star, contentDescription = null, tint = ProBlue,
                        modifier = Modifier.align(Alignment.Center).size(36.dp)
                    )
                }
                Text(
                    t(ExtraStrings.PW_TITLE), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    t(ExtraStrings.PW_SUB), fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                // Basic vs Premium table
                Card(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.weight(1f))
                            Text(
                                t(ExtraStrings.PW_BASIC), fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                color = ProBlue, textAlign = TextAlign.Center,
                                modifier = Modifier.width(64.dp)
                            )
                            Surface(shape = RoundedCornerShape(14.dp), color = ProBlue, modifier = Modifier.width(84.dp)) {
                                Text(
                                    t(ExtraStrings.PW_PREMIUM), fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                    color = Color.White, textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp), maxLines = 1
                                )
                            }
                        }
                        features.forEach { f ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(f.tint.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) { Icon(f.icon, null, tint = f.tint, modifier = Modifier.size(20.dp)) }
                                Spacer(Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(f.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(f.sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, lineHeight = 13.sp)
                                }
                                Box(modifier = Modifier.width(64.dp), contentAlignment = Alignment.Center) {
                                    if (f.basic) Icon(Icons.Default.Check, null, tint = ProBlue, modifier = Modifier.size(20.dp))
                                    else Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                                }
                                Box(modifier = Modifier.width(84.dp), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, null, tint = ProBlue, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                // (Tools preview card removed — every tool is free for all users now;
                // Pro only removes ads and adds VIP support, shown in the table above.)

                if (isPro) {
                    Card(
                        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = ProBlue)
                    ) {
                        Text(
                            t(ExtraStrings.PW_ACTIVE), color = Color.White, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(20.dp).testTag("paywall_active")
                        )
                    }
                } else {
                    PlanCard(
                        title = t(ExtraStrings.PW_MONTHLY),
                        sub = t(ExtraStrings.PW_MONTHLY_SUB).replace("%s", monthlyPrice),
                        price = monthlyPrice, per = t(ExtraStrings.PW_PER_MONTH),
                        button = t(ExtraStrings.PW_MONTHLY_BTN), accent = ProBlue, badge = null, save = null,
                        tag = "choose_monthly"
                    ) { context.findActivity()?.let { ProManager.purchase(it, ProConfig.BASE_PLAN_MONTHLY) } }
                    Spacer(Modifier.height(8.dp))
                    PlanCard(
                        title = t(ExtraStrings.PW_YEARLY),
                        sub = t(ExtraStrings.PW_YEARLY_SUB).replace("%s", yearlyPrice),
                        price = yearlyPrice, per = t(ExtraStrings.PW_PER_YEAR),
                        button = t(ExtraStrings.PW_YEARLY_BTN), accent = ProPurple,
                        badge = t(ExtraStrings.PW_BEST), save = t(ExtraStrings.PW_SAVE).replace("%d", savePct.toString()),
                        tag = "choose_yearly"
                    ) { context.findActivity()?.let { ProManager.purchase(it, ProConfig.BASE_PLAN_YEARLY) } }
                }

                if (isPro) {
                    TextButton(onClick = {
                        if (ProConfig.SUPPORT_EMAIL.isBlank() || ProConfig.SUPPORT_EMAIL.endsWith("@example.com")) {
                            Toast.makeText(context, if (language == AppLanguage.HINDI)
                                "सपोर्ट ईमेल अभी उपलब्ध नहीं है।" else "Support email is not configured yet.", Toast.LENGTH_LONG).show()
                        } else {
                            try {
                                context.startActivity(android.content.Intent(android.content.Intent.ACTION_SENDTO,
                                    android.net.Uri.parse("mailto:" + ProConfig.SUPPORT_EMAIL)).apply {
                                    putExtra(android.content.Intent.EXTRA_SUBJECT, t(ExtraStrings.SUPPORT_SUBJECT))
                                })
                            } catch (e: android.content.ActivityNotFoundException) {
                                Toast.makeText(context, ProConfig.SUPPORT_EMAIL, Toast.LENGTH_LONG).show()
                            }
                        }
                    }, modifier = Modifier.testTag("vip_support")) { Text(t(ExtraStrings.PW_F_VIP)) }
                }

                TextButton(onClick = { ProManager.connect(); ProManager.refreshPurchases() }, modifier = Modifier.testTag("restore_purchase")) {
                    Text(t(ExtraStrings.PW_RESTORE), fontSize = 12.sp)
                }

                // Footer
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(t(ExtraStrings.PW_FOOTER1) + " ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        t(ExtraStrings.PW_TERMS), fontSize = 10.sp, color = ProBlue, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { openUrl(context, ProConfig.TERMS_URL) }
                    )
                    Text(" " + t(ExtraStrings.PW_AND) + " ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        t(ExtraStrings.PW_PRIVACY), fontSize = 10.sp, color = ProBlue, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { openUrl(context, ProConfig.PRIVACY_URL) }
                    )
                    Text(" " + t(ExtraStrings.PW_FOOTER2), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = t(ExtraStrings.PW_AUTORENEW),
                    fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun PlanCard(
    title: String, sub: String, price: String, per: String, button: String,
    accent: Color, badge: String?, save: String?, tag: String, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (badge != null) {
                Surface(shape = RoundedCornerShape(10.dp), color = accent, modifier = Modifier.align(Alignment.End)) {
                    Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(price, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                        Spacer(Modifier.width(4.dp))
                        Text(per, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                        if (save != null) {
                            Spacer(Modifier.width(8.dp))
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFF10B981), modifier = Modifier.padding(bottom = 4.dp)) {
                                Text(save, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                    modifier = Modifier.width(120.dp).testTag(tag)
                ) {
                    Text(button, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 14.sp)
                }
            }
        }
    }
}