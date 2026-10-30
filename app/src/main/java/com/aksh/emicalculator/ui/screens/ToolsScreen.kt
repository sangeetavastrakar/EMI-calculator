package com.aksh.emicalculator.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.ExtraStrings
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import com.aksh.emicalculator.ui.components.InterstitialAdManager
import com.aksh.emicalculator.ui.components.ProActionBar
import com.aksh.emicalculator.ui.theme.AmberInterest
import com.aksh.emicalculator.ui.theme.EmeraldLight
import com.aksh.emicalculator.utils.PdfExportUtils
import com.aksh.emicalculator.viewmodel.EmiViewModel

/**
 * The "Tools" tab. It now reuses exactly the same catalog as the Home dashboard, so the two
 * screens can never drift apart or show different calculators.
 */
@Composable
fun ToolsScreen(viewModel: EmiViewModel, modifier: Modifier = Modifier) {
    val language by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current
    var selectedTool by rememberSaveable { mutableStateOf<String?>(null) }

    val openToolId = selectedTool?.let { name -> ToolId.values().firstOrNull { it.name == name } }

    BackHandler(enabled = openToolId != null) { selectedTool = null }

    if (openToolId != null) {
        ToolDetailHost(
            toolId = openToolId,
            viewModel = viewModel,
            onBack = { selectedTool = null }
        )
        return
    }

    Column(modifier = modifier.fillMaxSize().testTag("tools_screen")) {
        ToolScreenHeader(
            title = TranslationRepository.getString(language, ToolStrings.ALL_TOOLS),
            subtitle = TranslationRepository.getString(language, ToolStrings.HOME_TAGLINE),
            accent = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            ToolCategories.forEachIndexed { index, category ->
                CategorySection(
                    category = category,
                    language = language,
                    onOpenTool = { tool ->
                        InterstitialAdManager.onCalculatorOpened(context) {
                            selectedTool = tool.name
                        }
                    }
                )
                Spacer(Modifier.height(if (index == ToolCategories.lastIndex) 16.dp else 12.dp))
            }
        }
    }
}

// --------------------------------------------------------------------------- shared UI pieces

@Composable
internal fun ToolChip(label: String, selected: Boolean, tag: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF0878E8) else com.aksh.emicalculator.ui.components.CalcColors.Pale2,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = if (selected) Color.White else com.aksh.emicalculator.ui.components.CalcColors.BlueDark
        )
    }
}

@Composable
internal fun ToolColumn(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(com.aksh.emicalculator.ui.components.CalcColors.Pale2, com.aksh.emicalculator.ui.components.CalcColors.Pale, com.aksh.emicalculator.ui.components.CalcColors.Card)
            )
        ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            content()
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Big result card with headline value, rows, optional split bar, and PDF+Share buttons.
 */
@Composable
internal fun ToolResultCard(
    title: String,
    mainValue: String,
    rows: List<Pair<String, String>>,
    splitFirst: Double = 0.0,
    splitSecond: Double = 0.0,
    tag: String,
    language: AppLanguage,
    pdfTitle: String,
    pdfSubtitle: String,
    pdfRows: List<Pair<String, String>>,
    pdfFooter: String? = null,
    shareText: String,
    onClear: (() -> Unit)? = null,
    onPdfOverride: (() -> Unit)? = null,
    onShareOverride: (() -> Unit)? = null
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = com.aksh.emicalculator.ui.components.CalcColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, com.aksh.emicalculator.ui.components.CalcColors.FieldBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = mainValue,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = com.aksh.emicalculator.ui.components.CalcColors.BlueDark,
                maxLines = 1
            )
            if (splitFirst + splitSecond > 0.0) {
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))) {
                    if (splitFirst > 0.0) Box(Modifier.weight(splitFirst.toFloat()).fillMaxSize().background(EmeraldLight))
                    if (splitSecond > 0.0) Box(Modifier.weight(splitSecond.toFloat()).fillMaxSize().background(AmberInterest))
                }
            }
            Spacer(Modifier.height(12.dp))
            rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            com.aksh.emicalculator.ui.components.CalcActionsRow(
                language = language,
                onClear = onClear,
                onPdf = onPdfOverride ?: { PdfExportUtils.downloadAndOpenGenericPdf(context, pdfTitle, pdfSubtitle, pdfRows, pdfFooter) },
                onShare = onShareOverride ?: {
                    val file = PdfExportUtils.generateGenericPdf(context, pdfTitle, pdfSubtitle, pdfRows, pdfFooter)
                    if (file != null) PdfExportUtils.shareGeneric(context, pdfTitle, shareText, file)
                }
            )
        }
    }
}

@Composable
internal fun NoteText(text: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
