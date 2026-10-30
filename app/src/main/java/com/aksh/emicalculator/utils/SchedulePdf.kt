package com.aksh.emicalculator.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import com.aksh.emicalculator.R
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.FileProvider
import com.aksh.emicalculator.data.AppLanguage
import com.aksh.emicalculator.data.EmiMathUtils
import com.aksh.emicalculator.data.PrepaymentMonthRow
import com.aksh.emicalculator.data.ToolStrings
import com.aksh.emicalculator.data.TranslationRepository
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * The designed "Schedule" PDF (the one with the big 1ST / 2ND / 3RD badge).
 *
 * The badge number is a per-install counter: the first report you generate is 1ST,
 * the next one 2ND, and so on. It is stored in SharedPreferences so it survives app restarts.
 */
object SchedulePdf {

    private const val PREFS = "schedule_pdf_prefs"
    private const val KEY_SERIAL = "schedule_serial"

    private const val PAGE_W = 595
    private const val PAGE_H = 842

    // Brand colours
    private val BLUE = Color.rgb(11, 99, 206)
    private val BLUE_DARK = Color.rgb(8, 61, 143)
    private val BLUE_LIGHT = Color.rgb(234, 242, 254)
    private val BORDER = Color.rgb(214, 228, 247)
    private val CARD = Color.rgb(248, 251, 255)
    private val ROW_ALT = Color.rgb(244, 248, 254)
    private val TEXT = Color.rgb(15, 23, 42)
    private val MUTED = Color.rgb(100, 116, 139)
    private val GREEN = Color.rgb(18, 161, 80)
    private val GREEN_DARK = Color.rgb(6, 95, 70)

    data class Data(
        val principal: Double,
        val annualRate: Double,
        val tenureMonths: Int,
        val monthlyEmi: Double,
        val totalInterest: Double,
        val totalPayment: Double,
        val extraPayment: Double,
        val finalTenureMonths: Int,
        val monthsSaved: Int,
        val interestSaved: Double,
        val rows: List<PrepaymentMonthRow>,
        val assetPrice: Double = 0.0,
        val downPayment: Double = 0.0
    )

    private fun paint(size: Float, color: Int, bold: Boolean = false, align: Paint.Align = Paint.Align.LEFT): Paint =
        Paint().apply {
            this.color = color
            textSize = size
            typeface = Typeface.create(Typeface.DEFAULT, if (bold) Typeface.BOLD else Typeface.NORMAL)
            textAlign = align
            isAntiAlias = true
        }

    private fun fill(color: Int): Paint = Paint().apply {
        this.color = color
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private fun stroke(color: Int, width: Float = 1f): Paint = Paint().apply {
        this.color = color
        style = Paint.Style.STROKE
        strokeWidth = width
        isAntiAlias = true
    }

    private fun nextSerial(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val next = prefs.getInt(KEY_SERIAL, 0) + 1
        return next
    }

    private fun ordinal(n: Int): String {
        val suffix = when {
            n % 100 in 11..13 -> "TH"
            n % 10 == 1 -> "ST"
            n % 10 == 2 -> "ND"
            n % 10 == 3 -> "RD"
            else -> "TH"
        }
        return "$n$suffix"
    }

    private fun compact(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(Locale("en", "IN"))
        nf.maximumFractionDigits = 0
        nf.minimumFractionDigits = 0
        return nf.format(amount)
    }

    private fun money(amount: Double): String = EmiMathUtils.formatCurrency(amount)

    private fun fit(p: Paint, text: String, maxWidth: Float): String {
        if (p.measureText(text) <= maxWidth) return text
        var t = text
        while (t.length > 1 && p.measureText("$t...") > maxWidth) t = t.dropLast(1)
        return "$t..."
    }

    @Synchronized
    fun generate(context: Context, data: Data, language: AppLanguage): File? {
        return try {
            val doc = PdfDocument()
            val page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create())
            val c = page.canvas
            val logo = requireNotNull(BitmapFactory.decodeResource(context.resources, R.drawable.app_logo))

            val t = { key: String -> TranslationRepository.getString(language, key) }
            val serialNumber = nextSerial(context)
            val serial = ordinal(serialNumber)
            val dateText = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

            drawHeader(c, dateText, t(ToolStrings.PDF_YOUR_PARTNER), logo)
            drawLoanDetails(c, data, t)
            drawScheduleBanner(c, serial, t(ToolStrings.PDF_SCHEDULE_TITLE), t(ToolStrings.PDF_SCHEDULE_SUB))
            drawTable(c, data, t)
            drawBottomCards(c, data, t)
            if (data.downPayment > 0.0) c.drawText(
                "Purchase price: ${money(data.assetPrice)} • Down payment: ${money(data.downPayment)}",
                24f, 770f, paint(9f, BLUE_DARK))
            drawFooter(c, t, logo)

            doc.finishPage(page)
            // The cover is a compact overview; subsequent pages contain EVERY month.
            if (data.rows.size > 16) drawFullSchedule(doc, data, serial, t, logo)
            val file = File(context.cacheDir, "EMI_Schedule_$serialNumber.pdf")
            FileOutputStream(file).use { doc.writeTo(it) }
            doc.close()
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(KEY_SERIAL, serialNumber).commit()
            file
        } catch (e: Exception) {
            android.util.Log.w("SchedulePdf", "generate failed", e)
            null
        }
    }

    /** All non-loan calculators use the same branding and shared report sequence. */
    @Synchronized
    fun generateSummary(context: Context, title: String, subtitle: String,
                        rows: List<Pair<String, String>>, note: String?): File? {
        val serialNumber = nextSerial(context)
        val doc = PdfDocument()
        return try {
            val logo = requireNotNull(BitmapFactory.decodeResource(context.resources, R.drawable.app_logo))
            val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val sections = (rows + listOfNotNull(note?.takeIf { it.isNotBlank() }?.let { "Note" to it }))
                .ifEmpty { listOf("Result" to "—") }
            var pageNumber = 0
            var page: PdfDocument.Page? = null
            var y = 0f
            fun finish() { page?.let { drawReportFooter(it.canvas, serialNumber, pageNumber, logo); doc.finishPage(it) }; page = null }
            fun start() {
                pageNumber++
                page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, pageNumber).create())
                val c = page!!.canvas
                c.drawColor(Color.rgb(235, 248, 255))
                drawHeader(c, date, "Your Financial\nPartner", logo)
                c.drawRoundRect(18f, 100f, 577f, 250f, 14f, 14f, fill(Color.WHITE))
                paragraph(c, title, 32f, 113f, 530, 21f, BLUE_DARK, true)
                paragraph(c, subtitle, 32f, 178f, 530, 11f, MUTED)
                drawScheduleBanner(c, ordinal(serialNumber), "Report", "Calculation details • " + pageNumber)
                y = 344f
            }
            start()
            sections.forEach { (label, value) ->
                val labelLayout = layout(label, 245, 11f, MUTED)
                val valueLayout = layout(value, 254, 12f, BLUE_DARK, true)
                // Split unusually long values over pages without clipping or dropping text.
                val maxLines = 22
                val valueLines = (0 until valueLayout.lineCount).map {
                    value.substring(valueLayout.getLineStart(it), valueLayout.getLineEnd(it)).trimEnd()
                }.chunked(maxLines)
                valueLines.forEachIndexed { index, lines ->
                    val text = lines.joinToString("\n")
                    val block = layout(text, 254, 12f, BLUE_DARK, true)
                    val h = maxOf(labelLayout.height, block.height).toFloat() + 22f
                    if (y + h > 760f) { finish(); start() }
                    val c = page!!.canvas
                    c.drawRoundRect(20f, y, 575f, y + h, 9f, 9f, fill(Color.WHITE))
                    c.drawRoundRect(20f, y, 575f, y + h, 9f, 9f, stroke(BORDER))
                    paragraph(c, if (index == 0) label else "…", 32f, y + 10f, 245, 11f, MUTED)
                    paragraph(c, text, 300f, y + 10f, 254, 12f, BLUE_DARK, true)
                    y += h + 8f
                }
            }
            finish()
            val file = File(context.cacheDir, "EMI_Report_$serialNumber.pdf")
            FileOutputStream(file).use { doc.writeTo(it) }
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(KEY_SERIAL, serialNumber).commit()
            file
        } catch (e: Exception) {
            android.util.Log.w("SchedulePdf", "Summary generation failed", e)
            null
        } finally { doc.close() }
    }

    private fun layout(text: String, width: Int, size: Float, color: Int, bold: Boolean = false): android.text.StaticLayout {
        val p = android.text.TextPaint(paint(size, color, bold))
        return android.text.StaticLayout.Builder.obtain(text, 0, text.length, p, width)
            .setIncludePad(false).build()
    }

    private fun paragraph(c: Canvas, text: String, x: Float, y: Float, width: Int,
                          size: Float, color: Int, bold: Boolean = false) {
        c.save()
        c.translate(x, y)
        layout(text, width, size, color, bold).draw(c)
        c.restore()
    }

    private fun drawReportFooter(c: Canvas, serial: Int, page: Int, logo: Bitmap) {
        c.drawRect(0f, 786f, 595f, 842f, fill(BLUE))
        c.drawBitmap(logo, null, RectF(20f, 795f, 58f, 833f), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        c.drawText("EMI Calculator • Plan Smart, Save More", 70f, 808f, paint(11f, Color.WHITE, true))
        c.drawText("Report #$serial  •  Page $page", 70f, 826f, paint(9f, Color.WHITE))
    }

    private fun drawFullSchedule(doc: PdfDocument, data: Data, serial: String, t: (String) -> String, logo: Bitmap) {
        data.rows.chunked(34).forEachIndexed { index, chunk ->
            val page = doc.startPage(PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, index + 2).create())
            val c = page.canvas
            c.drawColor(Color.WHITE)
            drawHeader(c, serial, "Complete Schedule", logo)
            c.drawText(t(ToolStrings.REPAYMENT_SCHEDULE), 24f, 117f, paint(16f, BLUE_DARK, true))
            val headers = listOf("No.", "Month", "Principal", "Interest", "EMI", "Extra", "Paid", "Balance")
            val xs = listOf(27f, 59f, 192f, 265f, 330f, 395f, 474f, 570f)
            c.drawRect(20f, 133f, 577f, 156f, fill(BLUE))
            headers.forEachIndexed { i, h -> c.drawText(h, xs[i], 148f,
                paint(8f, Color.WHITE, true, if (i < 2) Paint.Align.LEFT else Paint.Align.RIGHT)) }
            chunk.forEachIndexed { i, row ->
                val y = 174f + i * 17f
                if (i % 2 == 0) c.drawRect(20f, y - 12f, 577f, y + 5f, fill(BLUE_LIGHT))
                val cells = listOf(row.monthNumber.toString(), monthLabel(row.monthNumber),
                    compact(row.principalPaid), compact(row.interestPaid), compact(row.emi),
                    compact(row.extraPaid), compact(row.totalPaid), compact(row.closingBalance))
                cells.forEachIndexed { j, value ->
                    val p = paint(8f, BLUE_DARK, j == 7, if (j < 2) Paint.Align.LEFT else Paint.Align.RIGHT)
                    while (p.measureText(value) > (if (j == 1) 67f else 62f) && p.textSize > 5f) p.textSize -= 0.5f
                    c.drawText(value, xs[j], y, p)
                }
            }
            c.drawText("Currency: " + EmiMathUtils.activeCurrency.symbol, 24f, 772f, paint(9f, MUTED))
            c.drawRect(0f, 786f, 595f, 842f, fill(BLUE))
            c.drawText("EMI Calculator • $serial • Page ${index + 2}", 24f, 814f, paint(11f, Color.WHITE, true))
            doc.finishPage(page)
        }
    }

    // ------------------------------------------------------------------ header
    private fun drawHeader(c: Canvas, dateText: String, partner: String, logo: Bitmap) {
        c.drawRect(0f, 0f, PAGE_W.toFloat(), 86f, fill(BLUE))
        val wave = Path().apply {
            moveTo(300f, 0f)
            cubicTo(360f, 40f, 470f, 10f, 595f, 0f)
            lineTo(595f, 86f)
            lineTo(320f, 86f)
            close()
        }
        c.drawPath(wave, fill(Color.rgb(30, 122, 224)))
        c.drawOval(-40f, 52f, 140f, 190f, fill(Color.rgb(24, 110, 214)))

        c.drawBitmap(logo, null, RectF(18f, 17f, 70f, 69f), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))

        c.drawText("EMI Calculator", 74f, 42f, paint(21f, Color.WHITE, true))
        c.drawText("Smart Calculation  •  Better Decisions", 75f, 58f, paint(9f, Color.argb(225, 255, 255, 255)))

        val right = paint(10f, Color.WHITE, true, Paint.Align.RIGHT)
        partner.split("\n").forEachIndexed { index, line ->
            c.drawText(line, 574f, 30f + index * 13f, right)
        }
        c.drawLine(500f, 54f, 574f, 54f, stroke(Color.argb(180, 255, 255, 255), 1.2f))
        c.drawText(dateText, 574f, 68f, paint(8f, Color.argb(210, 255, 255, 255), false, Paint.Align.RIGHT))
    }

    // ------------------------------------------------------------------ loan details
    private fun drawLoanDetails(c: Canvas, data: Data, t: (String) -> String) {
        c.drawRoundRect(18f, 96f, 577f, 252f, 14f, 14f, fill(Color.WHITE))
        c.drawRoundRect(18f, 96f, 577f, 252f, 14f, 14f, stroke(BORDER))

        c.drawRoundRect(30f, 108f, 36f, 126f, 3f, 3f, fill(BLUE))
        c.drawText(t(ToolStrings.LOAN_DETAILS), 44f, 120f, paint(13f, BLUE, true))
        c.drawText(t(ToolStrings.ENTER_LOAN_INFO), 44f, 133f, paint(8f, MUTED))

        c.drawRoundRect(470f, 104f, 566f, 132f, 7f, 7f, fill(BLUE_LIGHT))
        c.drawText(t(ToolStrings.PDF_REPORT_DATE), 479f, 114f, paint(7f, MUTED))
        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        c.drawText(date, 479f, 126f, paint(9f, BLUE_DARK, true))

        val chips = listOf(
            Triple("₹", t(ToolStrings.LOAN_AMOUNT_LBL), money(data.principal)),
            Triple("%", t(ToolStrings.RATE_PER_YEAR), "${String.format(Locale.US, "%.2f", data.annualRate)}% p.a."),
            Triple("M", t(ToolStrings.LOAN_TENURE_LBL), "${data.tenureMonths} months"),
            Triple("+", t(ToolStrings.PDF_EXTRA_PAYMENT), money(data.extraPayment))
        )
        val chipW = 130f
        chips.forEachIndexed { index, chip ->
            val x = 28f + index * (chipW + 6f)
            c.drawRoundRect(x, 142f, x + chipW, 180f, 8f, 8f, fill(BLUE_LIGHT))
            c.drawCircle(x + 17f, 161f, 10f, fill(Color.rgb(219, 234, 254)))
            c.drawText(chip.first, x + 17f, 165f, paint(9f, BLUE, true, Paint.Align.CENTER))
            c.drawText(fit(paint(6.5f, MUTED), chip.second, chipW - 36f), x + 32f, 155f, paint(6.5f, MUTED))
            c.drawText(fit(paint(9.5f, TEXT, true), chip.third, chipW - 36f), x + 32f, 169f, paint(9.5f, TEXT, true))
        }

        val metrics = listOf(
            Triple("₹", t(ToolStrings.MONTHLY_EMI), money(data.monthlyEmi)),
            Triple("%", t(ToolStrings.TOTAL_INTEREST_LBL), money(data.totalInterest)),
            Triple("=", t(ToolStrings.TOTAL_PAYMENT_LBL), money(data.totalPayment)),
            Triple("M", t(ToolStrings.NEW_TENURE_LBL), "${data.finalTenureMonths} months")
        )
        metrics.forEachIndexed { index, metric ->
            val x = 28f + index * 136f
            c.drawRoundRect(x, 188f, x + 128f, 240f, 8f, 8f, fill(CARD))
            c.drawRoundRect(x, 188f, x + 128f, 240f, 8f, 8f, stroke(BORDER))
            c.drawCircle(x + 18f, 204f, 9f, fill(BLUE_LIGHT))
            c.drawText(metric.first, x + 18f, 207f, paint(8f, BLUE, true, Paint.Align.CENTER))
            c.drawText(fit(paint(7f, MUTED), metric.second, 106f), x + 32f, 200f, paint(7f, MUTED))
            c.drawText(fit(paint(10f, BLUE_DARK, true), metric.third, 106f), x + 32f, 216f, paint(10f, BLUE_DARK, true))
            if (index == 3 && data.monthsSaved > 0) {
                val savedLabel = t(ToolStrings.PDF_SAVED_MONTHS).replace("%d", data.monthsSaved.toString())
                c.drawText(fit(paint(6.5f, GREEN, true), "($savedLabel)", 106f), x + 32f, 231f, paint(6.5f, GREEN, true))
            }
        }
    }

    // ------------------------------------------------------------------ big serial banner
    private fun drawScheduleBanner(c: Canvas, serial: String, title: String, subtitle: String) {
        c.drawRoundRect(18f, 262f, 577f, 328f, 14f, 14f, fill(BLUE))
        c.drawRoundRect(18f, 262f, 577f, 328f, 14f, 14f, stroke(BLUE_DARK, 1.2f))

        val leftTri = Path().apply {
            moveTo(30f, 262f)
            lineTo(64f, 262f)
            lineTo(30f, 296f)
            close()
        }
        c.drawPath(leftTri, fill(Color.rgb(30, 122, 224)))

        c.drawCircle(72f, 295f, 27f, fill(Color.WHITE))
        c.drawCircle(72f, 295f, 22f, fill(BLUE_DARK))
        val serialPaint = paint(15f, Color.WHITE, true, Paint.Align.CENTER)
        while (serialPaint.measureText(serial) > 40f && serialPaint.textSize > 6f) serialPaint.textSize -= 0.5f
        c.drawText(serial, 72f, 302f, serialPaint)

        c.drawText(title, 110f, 302f, paint(26f, Color.WHITE, true))
        c.drawText(subtitle, 112f, 318f, paint(9f, Color.argb(225, 255, 255, 255)))

        c.drawRoundRect(516f, 276f, 548f, 308f, 5f, 5f, stroke(Color.WHITE, 1.6f))
        c.drawLine(516f, 285f, 548f, 285f, stroke(Color.WHITE, 1.2f))
        c.drawCircle(526f, 296f, 1.6f, fill(Color.WHITE))
        c.drawCircle(537f, 296f, 1.6f, fill(Color.WHITE))
    }

    // ------------------------------------------------------------------ table
    private fun drawTable(c: Canvas, data: Data, t: (String) -> String) {
        c.drawRoundRect(18f, 338f, 577f, 640f, 14f, 14f, fill(Color.WHITE))
        c.drawRoundRect(18f, 338f, 577f, 640f, 14f, 14f, stroke(BORDER))

        c.drawRoundRect(30f, 348f, 36f, 366f, 3f, 3f, fill(BLUE))
        c.drawText(t(ToolStrings.REPAYMENT_SCHEDULE), 44f, 360f, paint(12f, BLUE, true))
        c.drawRoundRect(452f, 346f, 566f, 368f, 11f, 11f, fill(BLUE))
        c.drawText(t(ToolStrings.PDF_MONTHLY_BREAKDOWN), 509f, 360f, paint(7.5f, Color.WHITE, true, Paint.Align.CENTER))

        val symbol = EmiMathUtils.activeCurrency.symbol
        val right = Paint.Align.RIGHT
        c.drawRect(24f, 374f, 571f, 388f, fill(BLUE))
        c.drawRoundRect(24f, 374f, 571f, 388f, 5f, 5f, fill(BLUE))
        c.drawText(t(ToolStrings.PDF_NO), 30f, 384f, paint(6.8f, Color.WHITE, true))
        c.drawText(t(ToolStrings.MONTH_SHORT), 50f, 384f, paint(6.8f, Color.WHITE, true))
        c.drawText(fit(paint(6.8f, Color.WHITE, true, right), "${t(ToolStrings.PRINCIPAL_LBL)} ($symbol)", 76f), 196f, 384f, paint(6.8f, Color.WHITE, true, right))
        c.drawText(fit(paint(6.8f, Color.WHITE, true, right), "${t(ToolStrings.TOTAL_INTEREST_LBL)} ($symbol)", 70f), 270f, 384f, paint(6.8f, Color.WHITE, true, right))
        c.drawText("${t(ToolStrings.EMI_LBL)}", 334f, 384f, paint(6.8f, Color.WHITE, true, right))
        c.drawText(fit(paint(6.8f, Color.WHITE, true, right), t(ToolStrings.PDF_EXTRA_PAYMENT), 66f), 400f, 384f, paint(6.8f, Color.WHITE, true, right))
        c.drawText(fit(paint(6.8f, Color.WHITE, true, right), t(ToolStrings.PDF_TOTAL_PAID), 76f), 482f, 384f, paint(6.8f, Color.WHITE, true, right))
        c.drawText(fit(paint(6.8f, Color.WHITE, true, right), "${t(ToolStrings.BALANCE_LBL)} ($symbol)", 84f), 568f, 384f, paint(6.8f, Color.WHITE, true, right))

        val rows = data.rows
        val first = rows.take(12)
        // 13-16 rows fit on the page in one go. For longer schedules show the first 12,
        // a "next N months" gap, then the final 4 rows so the closing balance is always visible.
        val last = when {
            rows.size <= 16 -> rows.drop(12)
            else -> rows.takeLast(4)
        }
        val skipped = rows.size - first.size - last.size

        val rowH = 12.8f
        var y = 398f

        fun drawRow(row: PrepaymentMonthRow, index: Int, zebra: Boolean) {
            if (zebra) c.drawRect(24f, y - 6f, 571f, y + 7f, fill(ROW_ALT))
            val cell = paint(7f, TEXT)
            val cellRight = paint(7f, TEXT, false, right)
            c.drawText("${index + 1}", 30f, y, cell)
            c.drawText(monthLabel(row.monthNumber), 50f, y, cell)
            c.drawText(compact(row.principalPaid), 196f, y, cellRight)
            c.drawText(compact(row.interestPaid), 270f, y, cellRight)
            c.drawText(compact(row.emi), 334f, y, cellRight)
            c.drawText(compact(row.extraPaid), 400f, y, cellRight)
            c.drawText(compact(row.totalPaid), 482f, y, paint(7f, BLUE_DARK, true, right))
            c.drawText(compact(row.closingBalance), 568f, y, paint(7f, TEXT, true, right))
            y += rowH
        }

        first.forEachIndexed { i, row -> drawRow(row, i, i % 2 == 1) }

        if (skipped > 0) {
            c.drawRect(24f, y - 6f, 571f, y + 7f, fill(BLUE_LIGHT))
            val note = "( ... ${t(ToolStrings.PDF_NEXT_MONTHS).replace("%d", skipped.toString())} ... )"
            c.drawText(note, 297f, y, paint(7f, BLUE_DARK, true, Paint.Align.CENTER))
            y += rowH
        }

        last.forEachIndexed { i, row -> drawRow(row, first.size + skipped + i, i % 2 == 1) }

        c.drawLine(24f, y + 1f, 571f, y + 1f, stroke(BORDER, 1f))
        c.drawText("${t(ToolStrings.PDF_TOTAL)}:", 430f, y + 12f, paint(7.5f, BLUE_DARK, true, right))
        c.drawText(money(data.totalPayment), 568f, y + 12f, paint(7.5f, BLUE_DARK, true, right))
    }

    private fun monthLabel(offsetMonths: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, offsetMonths - 1)
        return SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(cal.time)
    }

    // ------------------------------------------------------------------ bottom cards + footer
    private fun drawBottomCards(c: Canvas, data: Data, t: (String) -> String) {
        // Savings card
        c.drawRoundRect(18f, 650f, 305f, 726f, 10f, 10f, fill(Color.rgb(236, 253, 245)))
        c.drawRoundRect(18f, 650f, 305f, 726f, 10f, 10f, stroke(Color.rgb(209, 250, 229), 1f))
        c.drawCircle(40f, 668f, 10f, fill(Color.rgb(209, 250, 229)))
        c.drawText("✓", 40f, 672f, paint(10f, GREEN, true, Paint.Align.CENTER))
        c.drawText(t(ToolStrings.PDF_EXTRA_BENEFIT), 56f, 667f, paint(9f, GREEN_DARK, true))
        if (data.interestSaved > 0.0 || data.extraPayment > 0.0) {
            val saved = t(ToolStrings.PDF_SAVED)
                .replace("%s", money(data.interestSaved))
                .replace("%d", data.monthsSaved.toString())
            c.drawText(fit(paint(7f, GREEN_DARK), saved, 232f), 30f, 690f, paint(7f, GREEN_DARK))
        } else {
            c.drawText("Add an extra payment to see how much interest you can save.", 30f, 690f, paint(7f, GREEN_DARK))
        }
        c.drawText("Small Steps... Big Savings!", 161f, 712f, paint(9f, BLUE, true, Paint.Align.CENTER))

        // Includes card
        c.drawRoundRect(312f, 650f, 577f, 726f, 10f, 10f, fill(CARD))
        c.drawRoundRect(312f, 650f, 577f, 726f, 10f, 10f, stroke(BORDER))
        c.drawText(t(ToolStrings.PDF_INCLUDES), 328f, 666f, paint(8.5f, BLUE_DARK, true))
        listOf(
            ToolStrings.PDF_INC_1,
            ToolStrings.PDF_INC_2,
            ToolStrings.PDF_INC_3,
            ToolStrings.PDF_INC_4
        ).forEachIndexed { index, key ->
            c.drawText("✓", 328f, 680f + index * 11f, paint(7f, GREEN, true))
            c.drawText(
                fit(paint(7f, TEXT), t(key), 205f),
                338f,
                680f + index * 11f,
                paint(7f, TEXT)
            )
        }

        c.drawText(t(ToolStrings.PDF_TAGLINE), 297f, 746f, paint(9f, BLUE, true, Paint.Align.CENTER))
        c.drawLine(150f, 742f, 200f, 742f, stroke(BLUE, 1f))
        c.drawLine(394f, 742f, 444f, 742f, stroke(BLUE, 1f))
    }

    private fun drawFooter(c: Canvas, t: (String) -> String, logo: Bitmap) {
        c.drawRect(0f, PAGE_H - 58f, PAGE_W.toFloat(), PAGE_H.toFloat(), fill(BLUE))
        c.drawBitmap(logo, null, RectF(20f, PAGE_H - 48f, 58f, PAGE_H - 10f), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        c.drawText("EMI Calculator", 68f, PAGE_H - 32f, paint(9f, Color.WHITE, true))
        c.drawText("Make Your Dreams Affordable", 68f, PAGE_H - 20f, paint(6.5f, Color.argb(220, 255, 255, 255)))

        var x = 240f
        listOf(
            "● ${t(ToolStrings.PDF_EASY)}",
            "● ${t(ToolStrings.PDF_SECURE)}",
            "● ${t(ToolStrings.PDF_ALL_IN_ONE)}"
        ).forEach { label ->
            c.drawText(label, x, PAGE_H - 26f, paint(6.5f, Color.WHITE))
            x += 76f
        }
        c.drawText(t(ToolStrings.PDF_TAGLINE), 574f, PAGE_H - 20f, paint(7f, Color.WHITE, true, Paint.Align.RIGHT))
    }

    // ------------------------------------------------------------------ open / share
    fun downloadAndOpen(context: Context, data: Data, language: AppLanguage) {
        try {
            val file = generate(context, data, language)
            if (file != null && file.exists()) {
                Toast.makeText(context, "PDF Report Generated Successfully!", Toast.LENGTH_SHORT).show()
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    context.startActivity(viewIntent)
                } catch (e: Exception) {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(send, "Open / Share EMI PDF Report"))
                }
            } else {
                Toast.makeText(context, "Failed to generate PDF. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.util.Log.w("SchedulePdf", "downloadAndOpen failed", e)
            Toast.makeText(context, "Could not complete this action. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(context: Context, data: Data, language: AppLanguage, shareText: String) {
        try {
            val file = generate(context, data, language)
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (file != null && file.exists()) {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, "EMI Schedule Report")
            }
            context.startActivity(Intent.createChooser(intent, "Share Report"))
        } catch (e: Exception) {
            android.util.Log.w("SchedulePdf", "share failed", e)
            Toast.makeText(context, "Could not complete this action. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
