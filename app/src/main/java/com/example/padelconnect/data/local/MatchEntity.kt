package com.example.padelconnect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val dateText: String,
    val timeText: String,
    val maxPlayers: Int
)