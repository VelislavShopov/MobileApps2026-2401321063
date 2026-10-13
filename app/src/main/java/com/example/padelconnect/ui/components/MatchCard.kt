package com.example.padelconnect.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.padelconnect.data.model.Match

@Composable
fun MatchCard(
    match: Match,
    currentUserId: String,
    onJoinClick: () -> Unit
) {
    val isHost = match.isUserHost(currentUserId)
    val hasJoined = match.isUserParticipant(currentUserId)
    val isFull = match.isFull

    val canJoin = !isHost && !hasJoined && !isFull

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${match.dateText} at ${match.timeText}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = if (isFull) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${match.currentPlayersCount}/${match.maxPlayers} Players",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (isFull) MaterialTheme.colorScheme.onErrorContainer
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Hosted by: ${match.creatorName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (match.participants.isNotEmpty()) {
                Text(
                    text = "Players: ${match.participants.joinToString(", ") { it.name }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Location: ${match.streetAddress}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onJoinClick,
                enabled = canJoin,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when {
                        isHost -> "Your Match (Host)"
                        hasJoined -> "Already Joined ✓"
                        isFull -> "Match Full"
                        else -> "Join Match"
                    }
                )
            }
        }
    }
}