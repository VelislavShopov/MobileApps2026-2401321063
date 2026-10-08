package com.example.padelconnect.ui.create_match

import android.app.AlertDialog
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CreateMatchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    fun onLocationSelected(lat: Double, lng: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                latitude = lat,
                longitude = lng
            )
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
        val currentState = _uiState.value
        if (!currentState.hasValidLocation) {
            return
        }
    }
}