package com.example.padelconnect.ui.edit_match

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.ui.components.MatchFormContent
import com.example.padelconnect.ui.create_match.EditMatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMatchScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditMatchViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val isTimeValid by viewModel.isTimeValid.collectAsState()
    val timeError = !isTimeValid && uiState.startTimeText.isNotBlank() && uiState.endTimeText.isNotBlank()

    val isMatchUpdated by viewModel.isMatchUpdated.collectAsState()

    LaunchedEffect(isMatchUpdated) {
        if (isMatchUpdated) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Padel Match",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            MatchFormContent(
                uiState = uiState,
                onMapClick = { lat, long -> viewModel.onLocationSelected(lat, long) },
                onDateChanged = { date -> viewModel.onDateChanged(date) },
                onStartTimeChanged = { hour, minutes -> viewModel.onStartTimeChanged(hour, minutes) },
                onEndTimeChanged = { hour, minutes -> viewModel.onEndTimeChanged(hour, minutes) },
                onMaxPlayersChanged = { count -> viewModel.onMaxPlayersChanged(count) },
                onCreatorNameChanged = { name -> viewModel.onCreatorNameChanged(name) },
                timeError = timeError
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { viewModel.editMatch() },
                enabled = uiState.hasValidLocation &&
                        uiState.dateText.isNotBlank() &&
                        uiState.startTimeText.isNotBlank() &&
                        uiState.endTimeText.isNotBlank() &&
                        uiState.creatorName.isNotBlank() &&
                        !timeError,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Save Changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}