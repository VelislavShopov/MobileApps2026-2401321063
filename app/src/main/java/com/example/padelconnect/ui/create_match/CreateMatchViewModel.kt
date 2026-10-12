package com.example.padelconnect.ui.create_match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.local.MatchEntity
import com.example.padelconnect.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    private val _isMatchCreated = MutableStateFlow(false)
    val isMatchCreated: StateFlow<Boolean> = _isMatchCreated.asStateFlow()

    fun onLocationSelected(lat: Double, lng: Double) {
        _uiState.update { it.copy(latitude = lat, longitude = lng) }
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
        val currentState = _uiState.value
        if (!currentState.hasValidLocation) return

        viewModelScope.launch {
            val matchEntity = MatchEntity(
                latitude = currentState.latitude!!,
                longitude = currentState.longitude!!,
                dateText = currentState.dateText,
                timeText = currentState.timeText,
                maxPlayers = currentState.maxPlayers
            )
            matchRepository.insertMatch(matchEntity)
            _isMatchCreated.value = true
        }
    }
}