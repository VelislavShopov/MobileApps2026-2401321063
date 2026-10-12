package com.example.padelconnect.ui.match_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.local.MatchEntity
import com.example.padelconnect.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MatchListViewModel @Inject constructor(
    matchRepository: MatchRepository
) : ViewModel() {

    // Converts the Flow from Room into a StateFlow for Compose to observe cleanly
    val matches: StateFlow<List<MatchEntity>> = matchRepository.allMatches
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}