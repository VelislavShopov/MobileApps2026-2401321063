package com.example.padelconnect.ui.create_match

data class CreateMatchUiState(
    val creatorName: String = "",
    val isNameSaved: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val startHour: Int? = null,
    val startMinutes: Int? = null,
    val endHour: Int? = null,
    val endMinutes: Int? = null,
    val streetAddress: String? = null,
    val dateText: String = "",
    val maxPlayers: Int = 4,
) {
    val hasValidLocation: Boolean
        get() = latitude != null && longitude != null
    val startTimeText: String
        get() = if (startHour != null && startMinutes != null) "%02d:%02d".format(startHour, startMinutes) else ""
    val endTimeText: String
        get() = if (endHour != null && endMinutes != null) "%02d:%02d".format(endHour, endMinutes) else ""

}