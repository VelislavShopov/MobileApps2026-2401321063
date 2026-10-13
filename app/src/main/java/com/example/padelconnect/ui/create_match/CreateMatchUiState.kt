package com.example.padelconnect.ui.create_match

data class CreateMatchUiState(
    val creatorName: String = "",
    val isNameSaved: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val hour: Int? = null,
    val minutes: Int? = null,
    val streetAddress: String? = null,
    val dateText: String = "",
    val maxPlayers: Int = 4,
) {
    val hasValidLocation: Boolean
        get() = latitude != null && longitude != null
    val timeText: String
        get() = if (hour != null && minutes != null) "%02d:%02d".format(hour, minutes) else ""
}