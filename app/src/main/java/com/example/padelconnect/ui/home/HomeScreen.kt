package com.example.padelconnect.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.padelconnect.data.model.Match
import com.example.padelconnect.ui.components.ActionCard
import com.example.padelconnect.ui.components.HeaderSection
import com.example.padelconnect.ui.components.HeroBannerCard
import com.example.padelconnect.ui.components.MatchCard

@Composable
fun HomeScreen(
    onCreateMatchClick: () -> Unit,
    onViewMatchesClick: () -> Unit,
    onViewMyMatchesClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val myMatches by viewModel.myMatches.collectAsStateWithLifecycle()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeaderSection()

            HeroBannerCard()

            MyMatchesSection(
                matches = myMatches,
                currentUserId = currentUserId,
                onViewAllClick = onViewMyMatchesClick,
                viewModel = viewModel
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                ActionCard(
                    title = "Create a Match",
                    subtitle = "Organize a game and invite players",
                    icon = Icons.Rounded.Add,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = onCreateMatchClick
                )

                ActionCard(
                    title = "View Matches",
                    subtitle = "Browse & join open padel games",
                    icon = Icons.AutoMirrored.Rounded.FormatListBulleted,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onViewMatchesClick
                )
            }
        }
    }
}


@Composable
private fun MyMatchesSection(
    matches: List<Match>,
    currentUserId: String,
    onViewAllClick: () -> Unit,
    viewModel: HomeViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Scheduled Matches",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(onClick = onViewAllClick) {
                Text("See All")
            }
        }

        if (matches.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EventAvailable,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You haven't joined any matches yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 12.dp)
            ) {
                items(matches, key = { it.id }) { match ->
                    Box(modifier = Modifier.width(350.dp)) {
                        MatchCard(
                            match = match,
                            currentUserId = currentUserId,
                            onJoinClick = {},
                            onDeleteClick = {viewModel.deleteMatch(match.id)}
                        )
                    }
                }
            }
        }
    }
}