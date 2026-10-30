package com.aksh.emicalculator

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.PrepaymentType
import com.aksh.emicalculator.ui.components.CalcHeader
import com.aksh.emicalculator.utils.SchedulePdf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Run on an emulator / device using connectedDebugAndroidTest. */
class ReportAndLayoutTest {
    @get:Rule val compose = createComposeRule()

    @Test fun headerLeavesCalculatorContentVisible() {
        compose.setContent {
            Column(Modifier.fillMaxSize()) {
                CalcHeader("Prepayment Calculator", "Pay Extra • Save More", null, showPro = false)
                Text("Loan inputs are visible")
            }
        }
        compose.onNodeWithText("Loan inputs are visible").assertIsDisplayed()
    }

    private fun pageCount(file: File): Int = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use {
        PdfRenderer(it).use { pdf -> pdf.pageCount }
    }

    @Test fun everyExportSharesSequenceAndLongSchedulesIncludeAllMonths() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("schedule_pdf_prefs", Context.MODE_PRIVATE)
        val before = preferences.getInt("schedule_serial", 0)
        val first = requireNotNull(SchedulePdf.generateSummary(context, "SIP", "Investment results",
            listOf("Invested" to "100000", "Returns" to "20000"), null))
        assertTrue(first.name.contains("_${before + 1}.pdf"))
        assertEquals(1, pageCount(first))
        val rows = EmiMathUtils.generatePrepaymentSchedule(1800000.0, 8.5, 180, PrepaymentType.MONTHLY_EXTRA, 5000.0)
        val data = SchedulePdf.Data(1800000.0, 8.5, 180, rows.first().emi,
            rows.sumOf { it.interestPaid }, rows.sumOf { it.totalPaid }, 5000.0,
            rows.size, 180 - rows.size, 0.0, rows)
        val second = requireNotNull(SchedulePdf.generate(context, data, AppLanguage.ENGLISH))
        assertTrue(second.name.contains("_${before + 2}.pdf"))
        assertEquals(1 + (rows.size + 33) / 34, pageCount(second))
        assertEquals(before + 2, preferences.getInt("schedule_serial", 0))
        assertTrue(first.exists()) // later export must not overwrite a previously shared PDF
    }

    @Test fun longToolReportsPaginate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = requireNotNull(SchedulePdf.generateSummary(context, "Tool Report", "Details",
            (1..45).map { "Input $it" to "Value $it" }, "End of report"))
        assertTrue(pageCount(file) > 1)
    }
}
