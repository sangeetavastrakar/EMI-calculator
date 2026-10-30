package com.aksh.emicalculator.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiCalculation
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.Strings
import com.aksh.emicalculator.data.TranslationRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtils {

    // Apne App ka Play Store link yahan daalein
    private const val PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.aksh.emicalculator&pcampaignid=web_share"

    // ============================================================
    // EXISTING: EMI loan PDF (used by EmiCalculatorScreen + Amortization)
    // ============================================================
    fun generateLoanPdf(
        context: Context,
        principal: Double,
        rate: Double,
        tenureMonths: Int,
        isTenureInYears: Boolean,
        emiResult: EmiCalculation,
        language: AppLanguage,
        assetPrice: Double = 0.0,
        downPayment: Double = 0.0
    ): File? {
        val rows = EmiMathUtils.generatePrepaymentSchedule(
            principal, rate, tenureMonths,
            com.aksh.emicalculator.data.PrepaymentType.MONTHLY_EXTRA, 0.0
        )
        return SchedulePdf.generate(context, SchedulePdf.Data(
            principal, rate, tenureMonths, emiResult.monthlyEmi,
            emiResult.totalInterest, emiResult.totalPayment, 0.0,
            tenureMonths, 0, 0.0, rows, assetPrice, downPayment
        ), language)
    }

    fun shareLoanDetails(
        context: Context,
        principal: Double,
        rate: Double,
        tenureMonths: Int,
        isTenureInYears: Boolean,
        emiResult: EmiCalculation,
        language: AppLanguage,
        assetPrice: Double = 0.0,
        downPayment: Double = 0.0
    ) {
        val file = generateLoanPdf(context, principal, rate, tenureMonths, isTenureInYears,
            emiResult, language, assetPrice, downPayment)
        if (file != null) shareGeneric(context, "EMI Calculator", "Loan report", file)
        else Toast.makeText(context, "Could not generate PDF. Please try again.", Toast.LENGTH_SHORT).show()
    }

    fun downloadAndOpenPdf(
        context: Context,
        principal: Double,
        rate: Double,
        tenureMonths: Int,
        isTenureInYears: Boolean,
        emiResult: EmiCalculation,
        language: AppLanguage,
        assetPrice: Double = 0.0,
        downPayment: Double = 0.0
    ) {
        try {
            val pdfFile = generateLoanPdf(context, principal, rate, tenureMonths, isTenureInYears, emiResult, language, assetPrice, downPayment)

            if (pdfFile != null && pdfFile.exists()) {
                Toast.makeText(context, "PDF Report Generated Successfully!", Toast.LENGTH_SHORT).show()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                try {
                    context.startActivity(viewIntent)
                } catch (e: Exception) {
                    val chooserIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(chooserIntent, "Open / Share EMI PDF Report"))
                }
            } else {
                Toast.makeText(context, "Failed to generate PDF. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.util.Log.w("PdfExportUtils", "downloadAndOpenPdf failed", e)
            Toast.makeText(context, "Could not complete this action. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    // ============================================================
    // NEW: GENERIC PDF / SHARE (used by Compare, Prepayment, Tools)
    // ============================================================
    fun generateGenericPdf(
        context: Context,
        title: String,
        subtitle: String,
        rows: List<Pair<String, String>>,
        footerNote: String? = null
    ): File? {
        return SchedulePdf.generateSummary(context, title, subtitle, rows, footerNote)
    }

    fun shareGeneric(
        context: Context,
        title: String,
        shareText: String,
        pdfFile: File? = null
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (pdfFile != null && pdfFile.exists()) {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                // Har share message ke end me Play store link
                putExtra(Intent.EXTRA_TEXT, "$shareText\n\nDownload App: $PLAY_STORE_LINK")
                putExtra(Intent.EXTRA_SUBJECT, title)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (e: Exception) {
            android.util.Log.w("PdfExportUtils", "shareGeneric failed", e)
            Toast.makeText(context, "Could not complete this action. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    fun downloadAndOpenGenericPdf(
        context: Context,
        title: String,
        subtitle: String,
        rows: List<Pair<String, String>>,
        footerNote: String? = null
    ) {
        try {
            val pdfFile = generateGenericPdf(context, title, subtitle, rows, footerNote)
            if (pdfFile != null && pdfFile.exists()) {
                Toast.makeText(context, "PDF Report Generated Successfully!", Toast.LENGTH_SHORT).show()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    context.startActivity(viewIntent)
                } catch (e: Exception) {
                    shareGeneric(context, title, subtitle, pdfFile)
                }
            } else {
                Toast.makeText(context, "Failed to generate PDF. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.util.Log.w("PdfExportUtils", "downloadAndOpenGenericPdf failed", e)
            Toast.makeText(context, "Could not complete this action. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}