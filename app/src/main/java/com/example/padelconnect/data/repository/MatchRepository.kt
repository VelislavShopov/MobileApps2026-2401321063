package com.example.padelconnect.data.repository

import com.example.padelconnect.data.local.MatchDao
import com.example.padelconnect.data.local.toDomain
import com.example.padelconnect.data.local.toEntity
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao
) {
    val allMatches: Flow<List<Match>> = matchDao.getAllMatches().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match.toEntity())
    }

    suspend fun joinMatch(matchId: Long, userId: String, userName: String): Boolean {
        val entity = matchDao.getMatchById(matchId) ?: return false
        val match = entity.toDomain()

        if (match.isUserParticipant(userId) || match.isFull) {
            return false
        }

        val updatedParticipants = match.participants + Participant(id = userId, name = userName)
        val updatedMatch = match.copy(participants = updatedParticipants)

        matchDao.insertMatch(updatedMatch.toEntity())
        return true
    }
}