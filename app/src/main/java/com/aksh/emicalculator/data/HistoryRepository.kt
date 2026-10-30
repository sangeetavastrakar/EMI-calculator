package com.aksh.emicalculator.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/** One saved calculation. */
data class HistoryEntry(
    val id: Long,                 // also the timestamp (ms) when it was calculated
    val category: String,         // LoanCategory name
    val principal: Double,        // loan amount
    val annualRate: Double,
    val tenureMonths: Int,
    val emi: Double,
    val totalInterest: Double,
    val totalPayment: Double,
    val assetPrice: Double = 0.0, // > 0 only for Home / Car with a down payment
    val downPayment: Double = 0.0,
    val currencyCode: String = "INR",
    val note: String = ""         // loan name / account no. typed by the user
) {
    fun sameLoanAs(o: HistoryEntry) =
        category == o.category && principal == o.principal && annualRate == o.annualRate &&
            tenureMonths == o.tenureMonths && downPayment == o.downPayment
}

/** Stores history in SharedPreferences as JSON (no database library -> fast startup). */
class HistoryRepository(context: Context) {
    private val prefs = context.getSharedPreferences("emi_history", Context.MODE_PRIVATE)

    fun load(): List<HistoryEntry> {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                HistoryEntry(
                    id = o.getLong("id"),
                    category = o.getString("category"),
                    principal = o.getDouble("principal"),
                    annualRate = o.getDouble("rate"),
                    tenureMonths = o.getInt("months"),
                    emi = o.getDouble("emi"),
                    totalInterest = o.getDouble("interest"),
                    totalPayment = o.getDouble("total"),
                    assetPrice = o.optDouble("price", 0.0),
                    downPayment = o.optDouble("down", 0.0),
                    currencyCode = o.optString("cur", "INR"),
                    note = o.optString("note", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(list: List<HistoryEntry>) {
        val arr = JSONArray()
        list.forEach { e ->
            arr.put(JSONObject().apply {
                put("id", e.id); put("category", e.category); put("principal", e.principal)
                put("rate", e.annualRate); put("months", e.tenureMonths); put("emi", e.emi)
                put("interest", e.totalInterest); put("total", e.totalPayment)
                put("price", e.assetPrice); put("down", e.downPayment); put("cur", e.currencyCode); put("note", e.note)
            })
        }
        prefs.edit().putString(KEY, arr.toString()).apply()
    }

    companion object {
        private const val KEY = "entries"
        const val MAX_ENTRIES = 200
    }
}
