package com.example.padelconnect.ui.match_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MatchFilter{
    ALL,
    OPEN_ONLY,
    JOINED_MATCHES,
    MY_MATCHES
}

@HiltViewModel
class MatchListViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    val currentUserId: String = userPreferences.getUserId()
    private val currentUserName: String = userPreferences.getCreatorName() ?: "Player"

    private val _selectedFilter = MutableStateFlow(MatchFilter.OPEN_ONLY)
    val selectedFilter: StateFlow<MatchFilter> = _selectedFilter.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val matches: StateFlow<List<Match>> = combine(
            matchRepository.allMatches,
            _selectedFilter
        ) { matchList, filter ->
            _isLoading.value = false
            when (filter) {
                MatchFilter.ALL -> matchList
                MatchFilter.OPEN_ONLY -> matchList.filter { match ->
                    !match.isFull && match.participants.none { it.id == currentUserId }
                }
                MatchFilter.JOINED_MATCHES -> matchList.filter { match ->
                    match.participants.any { it.id == currentUserId }
                }
                MatchFilter.MY_MATCHES -> matchList.filter { match ->
                    match.creatorId == currentUserId
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: MatchFilter) {
        _selectedFilter.value = filter
    }

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

