package com.example.padelconnect.data.local

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant

@Entity(tableName = "matches")
data class MatchEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val creatorId: String,
    val creatorName: String,
    val latitude: Double,
    val longitude: Double,
    val streetAddress: String,
    val dateText: String,
    val timeText: String,
    val maxPlayers: Int,
    val participants: List<Participant>
)

fun MatchEntity.toDomain(): Match {
    return Match(
        id = id,
        creatorId = creatorId,
        creatorName = creatorName,
        latitude = latitude,
        longitude = longitude,
        streetAddress = streetAddress,
        dateText = dateText,
        timeText = timeText,
        maxPlayers = maxPlayers,
        participants = participants
    )
}

fun Match.toEntity(): MatchEntity {
    return MatchEntity(
        id = id,
        creatorId = creatorId,
        creatorName = creatorName,
        latitude = latitude,
        longitude = longitude,
        streetAddress = streetAddress,
        dateText = dateText,
        timeText = timeText,
        maxPlayers = maxPlayers,
        participants = participants
    )
}