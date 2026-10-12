package com.example.padelconnect.ui.create_match

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.local.AppDatabase
import com.example.padelconnect.data.local.MatchEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateMatchViewModel(application: Application) : AndroidViewModel(application) {

    private val matchDao = AppDatabase.getDatabase(application).matchDao()

    private val _uiState = MutableStateFlow(CreateMatchUiState())
    val uiState: StateFlow<CreateMatchUiState> = _uiState.asStateFlow()

    private val _isMatchCreated = MutableStateFlow(false)
    val isMatchCreated: StateFlow<Boolean> = _isMatchCreated.asStateFlow()


    fun onLocationSelected(lat: Double, lng: Double) {
        _uiState.update { currentState ->
            currentState.copy(
                latitude = lat,
                longitude = lng
            )
        }
    }

    fun onDateChanged(newDate: String) {
        _uiState.update { currentState ->
            currentState.copy(dateText = newDate)
        }
    }

    fun onTimeChanged(hour: Int, minutes: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                hour = hour,
                minutes = minutes
            )
        }
    }

    fun onMaxPlayersChanged(count: Int) {
        _uiState.update { currentState ->
            currentState.copy(maxPlayers = count)
        }
    }

    fun createMatch() {
        val currentState = _uiState.value
        if (!currentState.hasValidLocation) return

        viewModelScope.launch(Dispatchers.IO) {
            val matchEntity = MatchEntity(
                latitude = currentState.latitude!!,
                longitude = currentState.longitude!!,
                dateText = currentState.dateText,
                timeText = currentState.timeText,
                maxPlayers = currentState.maxPlayers
            )

            matchDao.insertMatch(matchEntity)
            _isMatchCreated.value = true
        }
    }
}