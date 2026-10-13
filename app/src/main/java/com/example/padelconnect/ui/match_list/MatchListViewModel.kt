package com.example.padelconnect.ui.match_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchListViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val matches: StateFlow<List<Match>> = matchRepository.allMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentUserId: String = userPreferences.getUserId()
    private val currentUserName: String = userPreferences.getCreatorName() ?: "Player"

    fun joinMatch(matchId: String) {
        viewModelScope.launch {
            matchRepository.joinMatch(
                matchId = matchId,
                userId = currentUserId,
                userName = currentUserName
            )
        }
    }
}