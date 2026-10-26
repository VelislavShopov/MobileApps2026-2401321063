package com.example.padelconnect

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchRepository
import com.example.padelconnect.ui.edit_match.EditMatchViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class EditMatchViewModelTest {

    private lateinit var viewModel: EditMatchViewModel
    private val matchRepository: MatchRepository = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    private val sampleMatch = Match(
        id = "match_123",
        creatorId = "user_1",
        creatorName = "John Doe",
        latitude = 42.0,
        longitude = 23.0,
        streetAddress = "Test Street",
        dateText = "2026-10-25",
        startTimeText = "10:00",
        endTimeText = "11:00",
        maxPlayers = 4
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        val preferences = UserPreferences(context)

        coEvery { matchRepository.getMatch("match_123") } returns sampleMatch

        val savedStateHandle = SavedStateHandle(mapOf("matchId" to "match_123"))

        viewModel = EditMatchViewModel(
            matchRepository = matchRepository,
            context = context,
            userPreferences = preferences,
            savedStateHandle = savedStateHandle
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadMatchData_populatesUiStateCorrectly() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("John Doe", state.creatorName)
        assertEquals("2026-10-25", state.dateText)
        assertEquals(4, state.maxPlayers)
        assertEquals(10, state.startHour)
        assertEquals(0, state.startMinutes)
    }

    @Test
    fun onCreatorNameChanged_updatesCreatorName() {
        viewModel.onCreatorNameChanged("Alex")

        assertEquals(
            "Alex",
            viewModel.uiState.value.creatorName
        )
    }

    @Test
    fun onDateChanged_updatesDateText() {
        viewModel.onDateChanged("2026-10-26")

        assertEquals(
            "2026-10-26",
            viewModel.uiState.value.dateText
        )
    }

    @Test
    fun onMaxPlayersChanged_updatesMaxPlayers() {
        viewModel.onMaxPlayersChanged(4)

        assertEquals(
            4,
            viewModel.uiState.value.maxPlayers
        )
    }

    @Test
    fun onEndTimeChanged_validDuration_setsTimeValid() {
        viewModel.onStartTimeChanged(10, 0)
        viewModel.onEndTimeChanged(10, 30)

        assertEquals(true, viewModel.isTimeValid.value)
    }

    @Test
    fun onEndTimeChanged_invalidDuration_setsTimeInvalid() {
        viewModel.onStartTimeChanged(10, 30)
        viewModel.onEndTimeChanged(10, 0)

        assertEquals(false, viewModel.isTimeValid.value)
    }

    @Test
    fun editMatch_timeInvalid_doesNotUpdate() = runTest {
       viewModel.onStartTimeChanged(11, 0)
        viewModel.onEndTimeChanged(10, 0)

        viewModel.editMatch()
        advanceUntilIdle()

        assertEquals(false, viewModel.isMatchUpdated.value)
    }
}