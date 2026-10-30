package com.aksh.emicalculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.theme.EmeraldLight

/**
 * PDF + Share action bar. Free for everyone, and both buttons run instantly —
 * no ad ever sits in front of PDF export or Share (that is what earlier used to
 * annoy users / risk a Play Store "spammy ads" flag). The big ad is shown
 * elsewhere, at natural pauses only — see InterstitialAdManager.
 */
@Composable
fun ProActionBar(
    language: AppLanguage,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProActionButton(
            icon = Icons.Default.PictureAsPdf,
            label = "PDF",
            tint = EmeraldLight,
            locked = false,
            tag = "pro_pdf_btn",
            onClick = onPdf
        )
        ProActionButton(
            icon = Icons.Default.Share,
            label = TranslationRepository.getString(language, Strings.SHARE_BTN),
            tint = MaterialTheme.colorScheme.primary,
            locked = false,
            tag = "pro_share_btn",
            onClick = onShare
        )
    }
}

@Composable
private fun ProActionButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    locked: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tint.copy(alpha = 0.15f),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tint, maxLines = 1)
            if (locked) {
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    Icons.Default.Lock, contentDescription = "Locked",
                    tint = tint.copy(alpha = 0.7f), modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}