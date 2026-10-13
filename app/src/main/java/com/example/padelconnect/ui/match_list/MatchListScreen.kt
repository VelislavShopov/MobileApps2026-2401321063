package com.example.padelconnect.ui.match_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.ui.components.MatchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListScreen(
    onNavigateToCreateMatch: () -> Unit,
    viewModel: MatchListViewModel = hiltViewModel()
) {
    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Padel Matches") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateMatch) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Match")
            }
        }
    ) { paddingValues ->
        if (matches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matches scheduled yet.\nTap + to create one!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(matches, key = { it.id }) { match ->
                    MatchCard(match = match, currentUserId = currentUserId, onJoinClick = { viewModel.joinMatch(match.id)} )
                }
            }
        }
    }
}

