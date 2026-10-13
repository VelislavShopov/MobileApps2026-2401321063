package com.example.padelconnect.data.model

data class Match(
    val id: Long = 0L,
    val creatorId: String = "",
    val creatorName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val streetAddress: String = "",
    val dateText: String = "",
    val timeText: String = "",
    val maxPlayers: Int = 4,
    val participants: List<Participant> = emptyList()
) {
    val currentPlayersCount: Int get() = participants.size
    val isFull: Boolean get() = participants.size >= maxPlayers
    val availableSpots: Int get() = (maxPlayers - participants.size).coerceAtLeast(0)

    fun isUserParticipant(userId: String): Boolean {
        return participants.any { it.id == userId }
    }

    fun isUserHost(userId: String): Boolean {
        return creatorId == userId
    }
}