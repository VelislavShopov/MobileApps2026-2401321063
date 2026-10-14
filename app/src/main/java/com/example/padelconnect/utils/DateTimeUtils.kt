package com.example.padelconnect.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class DateTimeUtils {
    companion object {
        fun parseToMillis(dateText: String, timeText: String): Long {
            return try {
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }
                dateTimeFormat.parse("$dateText $timeText")?.time ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    }
}