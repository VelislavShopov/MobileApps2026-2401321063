package com.example.padelconnect

import com.example.padelconnect.ui.match_state.MatchFormUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchFormUiStateTest {

    @Test
    fun defaultValues_areCorrect() {
        val state = MatchFormUiState()

        assertEquals("", state.creatorName)
        assertFalse(state.isNameSaved)
        assertEquals(null, state.latitude)
        assertEquals(null, state.longitude)
        assertEquals(null, state.startHour)
        assertEquals(null, state.startMinutes)
        assertEquals(4, state.maxPlayers)
        assertFalse(state.hasValidLocation)
        assertEquals("", state.startTimeText)
        assertEquals("", state.endTimeText)
    }

    @Test
    fun hasValidLocation_returnsTrue_whenBothCoordinatesArePresent() {
        val state = MatchFormUiState(latitude = 42.0, longitude = 23.0)

        assertTrue(state.hasValidLocation)
    }

    @Test
    fun hasValidLocation_returnsFalse_whenCoordinateIsMissing() {
        // Само латитуд
        val stateWithOnlyLat = MatchFormUiState(latitude = 42.0, longitude = null)
        assertFalse(stateWithOnlyLat.hasValidLocation)

        // И двата липсват
        val stateEmpty = MatchFormUiState(latitude = null, longitude = null)
        assertFalse(stateEmpty.hasValidLocation)
    }

    @Test
    fun startTimeText_formatsCorrectly_withSingleDigitNumbers() {
        // Час 9, минути 5 трябва да стане "09:05"
        val state = MatchFormUiState(startHour = 9, startMinutes = 5)

        assertEquals("09:05", state.startTimeText)
    }

    @Test
    fun startTimeText_formatsCorrectly_withDoubleDigitNumbers() {
        // Час 14, минути 30 трябва да стане "14:30"
        val state = MatchFormUiState(startHour = 14, startMinutes = 30)

        assertEquals("14:30", state.startTimeText)
    }

    @Test
    fun startTimeText_returnsEmpty_whenTimeIsNull() {
        val state = MatchFormUiState(startHour = null, startMinutes = 30)

        assertEquals("", state.startTimeText)
    }

    @Test
    fun endTimeText_formatsCorrectly() {
        val state = MatchFormUiState(endHour = 18, endMinutes = 0)

        assertEquals("18:00", state.endTimeText)
    }

    @Test
    fun endTimeText_returnsEmpty_whenTimeIsNull() {
        val state = MatchFormUiState(endHour = 18, endMinutes = null)

        assertEquals("", state.endTimeText)
    }
}