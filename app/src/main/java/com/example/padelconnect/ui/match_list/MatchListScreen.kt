package com.example.padelconnect.ui.match_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.ui.components.MatchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchListScreen(
    initialFilter: MatchFilter = MatchFilter.OPEN_ONLY,
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToEditMatchClick: (Match) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MatchListViewModel = hiltViewModel()
) {
    LaunchedEffect(initialFilter) {
        viewModel.setFilter(initialFilter)
    }

    val matches by viewModel.matches.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            errorMessage = message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Padel Matches",
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreateMatch) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Match")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == MatchFilter.ALL,
                    onClick = { viewModel.setFilter(MatchFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == MatchFilter.OPEN_ONLY,
                    onClick = { viewModel.setFilter(MatchFilter.OPEN_ONLY) },
                    label = { Text("Open Spots") }
                )
                FilterChip(
                    selected = selectedFilter == MatchFilter.JOINED_MATCHES,
                    onClick = { viewModel.setFilter(MatchFilter.JOINED_MATCHES) },
                    label = { Text("Joined") }
                )
                FilterChip(
                    selected = selectedFilter == MatchFilter.MY_MATCHES,
                    onClick = { viewModel.setFilter(MatchFilter.MY_MATCHES) },
                    label = { Text("My Matches") }
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                matches.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matches found for this filter.\nTry a different filter or create one!",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matches, key = { it.id }) { match ->
                            MatchCard(
                                match = match,
                                currentUserId = currentUserId,
                                onEditClick = onNavigateToEditMatchClick,
                                onJoinClick = { viewModel.joinMatch(match) },
                                onDeleteClick = {viewModel.deleteMatch(match.id)}
                            )
                        }
                    }
                }
            }

            errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Cannot Join Match",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

