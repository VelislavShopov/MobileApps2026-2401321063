package com.example.padelconnect.data.local

import androidx.room.TypeConverter
import com.example.padelconnect.data.model.Participant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromParticipantList(participants: List<Participant>?): String {
        return json.encodeToString(participants ?: emptyList())
    }

    @TypeConverter
    fun toParticipantList(data: String?): List<Participant> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString<List<Participant>>(data)
        } catch (e: Exception) {
            emptyList()
        }
    }
}