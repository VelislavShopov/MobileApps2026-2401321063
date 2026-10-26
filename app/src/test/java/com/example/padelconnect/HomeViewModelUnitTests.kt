package com.example.padelconnect

import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.model.Participant
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
import com.example.padelconnect.ui.home.HomeViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val matchRepository: MatchRepository = mockk(relaxed = true)
    private val userPreferences: UserPreferences = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val matchesFlow = MutableStateFlow<List<Match>>(emptyList())

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { userPreferences.getUserId() } returns "user_123"

        every { matchRepository.allMatches } returns matchesFlow

        viewModel = HomeViewModel(matchRepository, userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsCurrentUserIdCorrectly() {
        assertEquals("user_123", viewModel.currentUserId)
    }

    @Test
    fun myMatches_filtersOnlyMatchesContainingCurrentUser() = runTest {
        val matchWithUser = Match(
            id = "1",
            participants = listOf(Participant(id = "user_123"), Participant(id = "other_user"))
        )
        val matchWithoutUser = Match(
            id = "2",
            participants = listOf(Participant(id = "other_user"))
        )
        val anotherMatchWithUser = Match(
            id = "3",
            participants = listOf(Participant(id = "user_123"))
        )

        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.myMatches.collect {}
        }

        matchesFlow.value = listOf(matchWithUser, matchWithoutUser, anotherMatchWithUser)

        testScheduler.advanceUntilIdle()

        val result = viewModel.myMatches.value
        assertEquals(2, result.size)
        assertEquals(listOf("1", "3"), result.map { it.id })

        collectJob.cancel()
    }

    @Test
    fun deleteMatch_callsRepositoryDelete() = runTest {
        val matchIdToDelete = "match_abc"

        viewModel.deleteMatch(matchIdToDelete)

        testScheduler.advanceUntilIdle()

        coVerify { matchRepository.deleteMatch(matchIdToDelete) }
    }
}