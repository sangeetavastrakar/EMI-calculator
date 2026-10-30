package com.aksh.emicalculator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.R
import com.aksh.emicalculator.billing.ProManager
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

/** Shared responsive design tokens for every calculator detail screen. */
object CalcColors {
    val Blue = Color(0xFF0B63CE)
    val BlueDark: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFFC1DCFF) else Color(0xFF083D8F)
    val BlueMid = Color(0xFF1E7AE0)
    val Sky = Color(0xFF3FA9F5)
    val Pale: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF111923) else Color(0xFFF3F8FF)
    val Pale2: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF142235) else Color(0xFFEAF3FF)
    val Card: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF182433) else Color(0xFFFFFFFF)
    val Field: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF213147) else Color(0xFFF1F6FE)
    val FieldBorder: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF3B506B) else Color(0xFFDCE8F8)
    val Text: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFFEDF4FF) else Color(0xFF0F172A)
    val Muted: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFFB5C4D8) else Color(0xFF64748B)
    val Green = Color(0xFF12A150)
    val GreenDark: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF9AE6C4) else Color(0xFF065F46)
    val GreenLight: Color
        @Composable get() = if (com.aksh.emicalculator.ui.theme.LocalCalcDark.current) Color(0xFF17392D) else Color(0xFFECFDF5)
    val Amber = Color(0xFFF59E0B)
    val Red = Color(0xFFE11D48)
    val Purple = Color(0xFF8B5CF6)
}

val CalcGradient = Brush.linearGradient(
    listOf(CalcColors.Blue, CalcColors.BlueMid, CalcColors.Sky)
)

val CalcPageGradient: Brush
    @Composable get() = Brush.verticalGradient(listOf(CalcColors.Pale2, CalcColors.Pale, CalcColors.Card))

@Composable
fun CalcHeader(
    title: String,
    subtitle: String?,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    showPro: Boolean = true
) {
    val isPro by ProManager.isPro.collectAsState()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 94.dp, max = 124.dp)
            .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
            .background(CalcGradient)
            .testTag("calc_header")
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.42f,
                center = Offset(size.width * 0.92f, size.height * 0.10f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.width * 0.30f,
                center = Offset(size.width * 0.02f, size.height * 1.05f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onBack)
                        .testTag("tool_back_btn")
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = TranslationRepository.getString(
                            AppLanguage.ENGLISH,
                            ToolStrings.BACK
                        ),
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
            }
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(15.dp))
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.replace("\n", " "),
                    color = Color.White,
                    fontSize = 21.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 10.5.sp,
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (showPro) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.16f),
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .clickable { ProManager.showPaywall.value = true }
                        .testTag("calc_header_pro")
                ) {
                    Icon(
                        Icons.Default.WorkspacePremium,
                        contentDescription = "Pro",
                        tint = if (isPro) Color(0xFF86EFAC) else Color(0xFFFDE047),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CalcPageBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.fillMaxSize().background(CalcPageGradient)) { content() }
}

@Composable
fun CalcCard(
    modifier: Modifier = Modifier,
    padding: Int = 14,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = CalcColors.Card,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, CalcColors.FieldBorder.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(padding.dp),
            content = content
        )
    }
}

@Composable
fun CalcSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(CalcColors.Pale2),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CalcColors.Blue, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(9.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = CalcColors.BlueDark,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    subtitle,
                    color = CalcColors.Muted,
                    fontSize = 11.5.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CalcFieldSurface(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    suffix: String = "",
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color = CalcColors.Field,
        border = androidx.compose.foundation.BorderStroke(1.dp, CalcColors.FieldBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    color = CalcColors.Muted,
                    fontSize = 9.8.sp,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                content()
            }
            if (suffix.isNotBlank()) {
                Spacer(Modifier.width(6.dp))
                Text(
                    suffix,
                    color = CalcColors.Blue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            if (trailing != null) trailing()
        }
    }
}

@Composable
fun CalcBasicTextField(
    text: String,
    onTextChange: (String) -> Unit,
    isDecimal: Boolean,
    modifier: Modifier = Modifier,
    prefix: String = "",
    fontSize: Int = 17
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        singleLine = true,
        textStyle = TextStyle(
            color = CalcColors.BlueDark,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        ),
        cursorBrush = SolidColor(CalcColors.Blue),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (prefix.isNotBlank()) {
                    Text(
                        prefix,
                        color = CalcColors.BlueDark,
                        fontSize = fontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(
                            "0",
                            color = CalcColors.Muted.copy(alpha = 0.45f),
                            fontSize = fontSize.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    inner()
                }
            }
        }
    )
}

@Composable
fun CalcPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tag: String = "calc_primary_btn"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(13.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(13.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CalcGradient)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                }
                Text(
                    label,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CalcGhostButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tag: String = "calc_ghost_btn"
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(11.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(11.dp),
        color = CalcColors.Pale2,
        border = androidx.compose.foundation.BorderStroke(1.dp, CalcColors.FieldBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = CalcColors.Blue, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(
                label,
                color = CalcColors.Blue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CalcMetricTile(
    icon: ImageVector,
    tint: Color,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            color = CalcColors.Muted,
            fontSize = 8.5.sp,
            lineHeight = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            value,
            color = CalcColors.BlueDark,
            fontSize = 11.5.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CalcMetricRow(
    items: List<Pair<ImageVector, Pair<Color, Pair<String, String>>>>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Top
    ) {
        items.forEach { (icon, rest) ->
            val (tint, labelValue) = rest
            CalcMetricTile(
                icon = icon,
                tint = tint,
                label = labelValue.first,
                value = labelValue.second,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CalcActionsRow(
    language: AppLanguage,
    onClear: (() -> Unit)?,
    onPdf: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onClear != null) {
            CalcGhostButton(
                label = TranslationRepository.getString(language, com.aksh.emicalculator.data.ExtraStrings.CLEAR_BTN),
                onClick = onClear,
                icon = Icons.Default.Delete,
                modifier = Modifier.weight(0.9f),
                tag = "calc_clear_btn"
            )
        }
        CalcGhostButton(
            label = TranslationRepository.getString(language, Strings.DOWNLOAD_PDF),
            onClick = onPdf,
            icon = Icons.Default.PictureAsPdf,
            modifier = Modifier.weight(1.35f),
            tag = "calc_pdf_btn"
        )
        Surface(
            modifier = Modifier
                .weight(1.1f)
                .height(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .clickable(onClick = onShare)
                .testTag("calc_share_btn"),
            shape = RoundedCornerShape(11.dp),
            color = CalcColors.Blue
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    TranslationRepository.getString(language, Strings.SHARE_BTN),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CalcSavingsCard(
    title: String,
    subtitle: String,
    value: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().testTag("calc_savings_card"),
        shape = RoundedCornerShape(15.dp),
        color = CalcColors.GreenLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, CalcColors.Green.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CalcColors.Green.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Savings,
                    contentDescription = null,
                    tint = CalcColors.Green,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    color = CalcColors.GreenDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    subtitle,
                    color = CalcColors.GreenDark.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = 12.5.sp
                )
                if (!value.isNullOrBlank()) {
                    Text(
                        value,
                        color = CalcColors.Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Text("🌱", fontSize = 20.sp)
        }
    }
}
