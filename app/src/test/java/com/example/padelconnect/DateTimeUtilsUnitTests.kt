package com.example.padelconnect

import com.example.padelconnect.utils.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DateTimeUtilsTest {

    @Test
    fun parseToMillis_validDateTime_returnsCorrectMillis() {
        val dateText = "2026-10-26"
        val timeText = "14:30"

        val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
            set(2026, Calendar.OCTOBER, 26, 14, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val expectedMillis = calendar.timeInMillis

        val resultMillis = DateTimeUtils.parseToMillis(dateText, timeText)

        assertEquals(expectedMillis, resultMillis)
    }

    @Test
    fun parseToMillis_invalidDateFormat_returnsZero() {
        val dateText = "26.10.2026"
        val timeText = "14:30"

        val resultMillis = DateTimeUtils.parseToMillis(dateText, timeText)

        assertEquals(0L, resultMillis)
    }

    @Test
    fun parseToMillis_invalidTimeFormat_returnsZero() {
        val dateText = "2026-10-26"
        val timeText = "14"

        val resultMillis = DateTimeUtils.parseToMillis(dateText, timeText)

        assertEquals(0L, resultMillis)
    }

    @Test
    fun parseToMillis_emptyStrings_returnsZero() {
        val resultMillis = DateTimeUtils.parseToMillis("", "")

        assertEquals(0L, resultMillis)
    }

    @Test
    fun parseToMillis_malformedDateText_returnsZero() {
        val dateText = "not-a-date"
        val timeText = "12:00"

        val resultMillis = DateTimeUtils.parseToMillis(dateText, timeText)

        assertEquals(0L, resultMillis)
    }
}