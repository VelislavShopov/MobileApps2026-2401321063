package com.example.padelconnect.data.repository

import com.example.padelconnect.data.local.MatchDao
import com.example.padelconnect.data.local.MatchEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val matchDao: MatchDao
) {
    val allMatches: Flow<List<MatchEntity>> = matchDao.getAllMatches()
    suspend fun insertMatch(matchEntity: MatchEntity) {
        matchDao.insertMatch(matchEntity)
    }
}