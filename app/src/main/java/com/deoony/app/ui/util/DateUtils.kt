package com.deoony.app.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.US),
        SimpleDateFormat("dd-MM-yyyy", Locale.US)
    )

    fun parseStringToMillis(dateStr: String): Long? {
        val trimmed = dateStr.trim()
        if (trimmed.isEmpty()) return null
        for (format in formats) {
            try {
                format.isLenient = false
                val date = format.parse(trimmed)
                if (date != null) return date.time
            } catch (e: Exception) {
                // Try next format
            }
        }
        return null
    }

    fun formatMillisToString(millis: Long): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.format(Date(millis))
        } catch (e: Exception) {
            ""
        }
    }
}
