package com.example.padelconnect

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.data.preferences.UserPreferences
import com.example.padelconnect.data.repository.MatchCreator
import com.example.padelconnect.ui.create_match.CreateMatchViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateMatchViewModelTest {

    private lateinit var viewModel: CreateMatchViewModel

    private val fakeRepository = object : MatchCreator {
        override suspend fun createMatch(match: Match) {
            // No Firebase calls in unit tests.
        }
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val preferences = UserPreferences(context)

        viewModel = CreateMatchViewModel(
            matchRepository = fakeRepository,
            context = context,
            userPreferences = preferences
        )
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
        viewModel.onDateChanged("2026-10-25")

        assertEquals(
            "2026-10-25",
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
    fun onEndTimeChanged_exactly30Minutes_setsTimeInvalid() {
        viewModel.onStartTimeChanged(10, 0)
        viewModel.onEndTimeChanged(10, 29)

        assertEquals(false, viewModel.isTimeValid.value)
    }

    @Test
    fun createMatch_timeInvalid(){
        viewModel.createMatch()

        assertEquals(false, viewModel.isMatchCreated.value)
    }

    @Test
    fun createMatch_Valid(){
        viewModel.onStartTimeChanged(10, 0)
        viewModel.onEndTimeChanged(10, 30)
        viewModel.onDateChanged("2026-10-25")
        viewModel.onLocationSelected(50.10, 45.10)

        viewModel.createMatch()

        assertEquals(true, viewModel.isMatchCreated.value)
    }
}