package com.example.padelconnect

import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchTest {

    @Test
    fun defaultValues_areCorrect() {
        val match = Match()

        assertEquals(4, match.maxPlayers)
        assertEquals(0, match.currentPlayersCount)
        assertFalse(match.isFull)
        assertEquals(0.0, match.latitude, 0.0001)
        assertEquals(0.0, match.longitude, 0.0001)
        assertTrue(match.participants.isEmpty())
    }

    @Test
    fun currentPlayersCount_returnsCorrectSize() {
        val participants = listOf(
            Participant(id = "1"),
            Participant(id = "2")
        )
        val match = Match(participants = participants)

        assertEquals(2, match.currentPlayersCount)
    }

    @Test
    fun isFull_returnsTrue_whenParticipantsReachMax() {
        val participants = listOf(
            Participant(id = "1"),
            Participant(id = "2"),
            Participant(id = "3"),
            Participant(id = "4")
        )
        val match = Match(participants = participants, maxPlayers = 4)

        assertTrue(match.isFull)
    }

    @Test
    fun isFull_returnsFalse_whenParticipantsAreBelowMax() {
        val participants = listOf(
            Participant(id = "1"),
            Participant(id = "2")
        )
        val match = Match(participants = participants, maxPlayers = 4)

        assertFalse(match.isFull)
    }

    @Test
    fun isUserParticipant_returnsTrue_whenUserIsInList() {
        val participants = listOf(
            Participant(id = "user_123"),
            Participant(id = "user_456")
        )
        val match = Match(participants = participants)

        assertTrue(match.isUserParticipant("user_123"))
    }

    @Test
    fun isUserParticipant_returnsFalse_whenUserIsNotInList() {
        val participants = listOf(
            Participant(id = "user_123")
        )
        val match = Match(participants = participants)

        assertFalse(match.isUserParticipant("unknown_user"))
    }

    @Test
    fun isUserHost_returnsTrue_whenIdMatchesCreatorId() {
        val match = Match(creatorId = "host_999")

        assertTrue(match.isUserHost("host_999"))
    }

    @Test
    fun isUserHost_returnsFalse_whenIdDoesNotMatchCreatorId() {
        val match = Match(creatorId = "host_999")

        assertFalse(match.isUserHost("other_user"))
    }
}