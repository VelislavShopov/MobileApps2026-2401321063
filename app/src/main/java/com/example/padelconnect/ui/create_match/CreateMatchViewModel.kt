package com.example.padelconnect.ui.create_match

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.local.MatchEntity
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
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
class CreateMatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    private val _isMatchCreated = MutableStateFlow(false)
    val isMatchCreated: StateFlow<Boolean> = _isMatchCreated.asStateFlow()
    init {
        loadSavedCreatorName()
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

    fun onTimeChanged(hour: Int, minutes: Int) {
        _uiState.update { it.copy(hour = hour, minutes = minutes) }
    }

    fun onMaxPlayersChanged(count: Int) {
        _uiState.update { it.copy(maxPlayers = count) }
    }

    fun createMatch() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val hostName = currentState.creatorName.trim()

            userPreferences.saveCreatorName(hostName)

            val userId = userPreferences.getUserId()

            val hostParticipant = Participant(
                id = userId,
                name = hostName
            )

            val newMatch = Match(
                creatorId = userId,
                creatorName = hostName,
                latitude = currentState.latitude!!,
                longitude = currentState.longitude!!,
                dateText = currentState.dateText,
                timeText = currentState.timeText,
                maxPlayers = currentState.maxPlayers,
                participants = listOf(hostParticipant)
            )

            matchRepository.insertMatch(newMatch)

            _isMatchCreated.value = true
        }
    }
}