package com.example.padelconnect.ui.edit_match

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
import com.example.padelconnect.ui.match_state.MatchFormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditMatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    @param:ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val matchId: String = savedStateHandle.get<String>("matchId") ?: ""
    private val _uiState = MutableStateFlow(MatchFormUiState())
    val uiState: StateFlow<MatchFormUiState> = _uiState.asStateFlow()

    private val _isMatchUpdated = MutableStateFlow(false)
    val isMatchUpdated: StateFlow<Boolean> = _isMatchUpdated.asStateFlow()
    private val _isTimeValid = MutableStateFlow(false)
    val isTimeValid: StateFlow<Boolean> = _isTimeValid.asStateFlow()
    private var originalMatch: Match? = null

    init {
        loadSavedCreatorName()
        loadMatchData()
    }

    private fun loadMatchData() {
        if (matchId.isBlank()) return
        viewModelScope.launch {
            val match = matchRepository.getMatch(matchId)
            if (match != null) {
                originalMatch = match
                val startParts = match.startTimeText.split(":")
                val endParts = match.endTimeText.split(":")

                val startHour = startParts.getOrNull(0)?.toIntOrNull()
                val startMin = startParts.getOrNull(1)?.toIntOrNull()
                val endHour = endParts.getOrNull(0)?.toIntOrNull()
                val endMin = endParts.getOrNull(1)?.toIntOrNull()

                _uiState.update {
                    it.copy(
                        creatorName = match.creatorName,
                        latitude = match.latitude,
                        longitude = match.longitude,
                        streetAddress = match.streetAddress,
                        dateText = match.dateText,
                        startHour = startHour,
                        startMinutes = startMin,
                        endHour = endHour,
                        endMinutes = endMin,
                        maxPlayers = match.maxPlayers,
                        isNameSaved = true
                    )
                }
                _isTimeValid.value = isTimeValid()
            }
        }
    }
    private fun loadSavedCreatorName() {
        val savedName = userPreferences.getCreatorName()
        if (!savedName.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    creatorName = savedName,
                    isNameSaved = true
                )
            }
        }
    }

    fun onCreatorNameChanged(newName: String) {
        _uiState.update { it.copy(creatorName = newName) }
    }

    fun onLocationSelected(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng) }

        fetchStreetAddress(lat,lng)
    }

    private fun fetchStreetAddress(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val fullAddress = addresses[0].getAddressLine(0)

                    _uiState.update { it.copy(streetAddress = fullAddress) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onDateChanged(newDate: String) {
        _uiState.update { it.copy(dateText = newDate) }
    }

    fun onStartTimeChanged(hour: Int, minutes: Int) {
        _uiState.update { it.copy(startHour = hour, startMinutes = minutes) }
        _isTimeValid.value = isTimeValid()
    }

    fun onEndTimeChanged(hour: Int, minutes: Int){
        _uiState.update { it.copy(endHour = hour, endMinutes = minutes) }
        _isTimeValid.value = isTimeValid()
    }

    private fun isTimeValid(): Boolean {
        val state = _uiState.value

        if (state.startHour == null || state.startMinutes == null || state.endHour == null || state.endMinutes == null){
            return false
        }

        val startTotalMinutes = state.startHour * 60 + state.startMinutes
        val endTotalMinutes = state.endHour * 60 + state.endMinutes - 29
        return endTotalMinutes > startTotalMinutes
    }

    fun onMaxPlayersChanged(count: Int) {
        _uiState.update { it.copy(maxPlayers = count) }
    }

    fun editMatch() {
        val currentMatch = originalMatch ?: return

        viewModelScope.launch {
            val currentState = _uiState.value
            val hostName = currentState.creatorName.trim()

            if (!isTimeValid()) {
                return@launch
            }

            userPreferences.saveCreatorName(hostName)
            val userId = userPreferences.getUserId()

            val updatedParticipants = currentMatch.participants.map { participant ->
                if (participant.id == userId) participant.copy(name = hostName) else participant
            }

            val updatedMatch = currentMatch.copy(
                creatorId = userId,
                creatorName = hostName,
                latitude = currentState.latitude ?: currentMatch.latitude,
                longitude = currentState.longitude ?: currentMatch.longitude,
                streetAddress = currentState.streetAddress ?: currentMatch.streetAddress,
                dateText = currentState.dateText,
                startTimeText = currentState.startTimeText,
                endTimeText = currentState.endTimeText,
                maxPlayers = currentState.maxPlayers,
                participants = updatedParticipants
            )

            try {
                matchRepository.editMatch(updatedMatch)
                _isMatchUpdated.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}