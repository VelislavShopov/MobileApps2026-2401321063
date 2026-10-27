package com.example.padelconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.padelconnect.ui.match_state.MatchFormUiState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchFormContent(
    uiState: MatchFormUiState,
    onMapClick: (Double, Double) -> Unit,
    onDateChanged: (String) -> Unit,
    onStartTimeChanged: (Int, Int) -> Unit,
    onEndTimeChanged: (Int, Int) -> Unit,
    onMaxPlayersChanged: (Int) -> Unit,
    onCreatorNameChanged: (String) -> Unit,
    timeError: Boolean
) {
    var showStartTimeDialog by remember { mutableStateOf(false) }
    var showEndTimeDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }

    var isMapLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150.milliseconds)
        isMapLoaded = true
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val dateFormatter = remember { DatePickerDefaults.dateFormatter() }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val formattedDate = formatter.format(Date(millis))
            onDateChanged(formattedDate)
        }
    }

    val defaultLocation = LatLng(42.1354, 24.7453)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionCard(
            stepNumber = "1",
            title = "Select Location"
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isMapLoaded) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize().testTag("map"),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { latLng ->
                                onMapClick(latLng.latitude, latLng.longitude)
                            }
                        ) {
                            if (uiState.hasValidLocation) {
                                val markerState = rememberMarkerState(
                                    position = LatLng(uiState.latitude!!, uiState.longitude!!)
                                )
                                Marker(
                                    state = markerState,
                                    title = "Match Location"
                                )
                            }
                        }
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Location Status Badge
            Surface(
                color = if (uiState.hasValidLocation) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.hasValidLocation) Icons.Rounded.CheckCircle else Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = if (uiState.hasValidLocation) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.hasValidLocation && uiState.streetAddress != null) {
                            "Location set: ${uiState.streetAddress}"
                        } else {
                            "Tap anywhere on the map to pin a court"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (uiState.hasValidLocation) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        SectionCard(
            stepNumber = "2",
            title = "Match Details"
        ) {
            DetailPickerRow(
                modifier = Modifier.fillMaxSize().testTag("date_select"),
                label = uiState.dateText.ifBlank { "Select Date" },
                icon = Icons.Rounded.CalendarToday,
                isSelected = uiState.dateText.isNotBlank(),
                onClick = { showDateDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DetailPickerRow(
                modifier = Modifier.fillMaxSize(),
                label = uiState.startTimeText.ifBlank { "Select Start Time" },
                icon = Icons.Rounded.AccessTime,
                isSelected = uiState.startTimeText.isNotBlank(),
                isError = timeError,
                onClick = { showStartTimeDialog = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            DetailPickerRow(
                modifier = Modifier.fillMaxSize(),
                label = uiState.endTimeText.ifBlank { "Select End Time" },
                icon = Icons.Rounded.AccessTime,
                isSelected = uiState.endTimeText.isNotBlank(),
                isError = timeError,
                onClick = { showEndTimeDialog = true }
            )

            if (timeError) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "End time must be at least 30 minutes after start time",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Max Players",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { onMaxPlayersChanged(2) },
                            enabled = uiState.maxPlayers == 4,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = if (uiState.maxPlayers == 4) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Remove,
                                contentDescription = "Decrease players",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "${uiState.maxPlayers}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { onMaxPlayersChanged(4) },
                            enabled = uiState.maxPlayers == 2,
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = if (uiState.maxPlayers == 2) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Increase players",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        SectionCard(
            stepNumber = "3",
            title = "Organizer Info"
        ) {
            OutlinedTextField(
                value = uiState.creatorName,
                onValueChange = { onCreatorNameChanged(it) },
                label = { Text("Your Name / Host Name") },
                placeholder = { Text("e.g. Alex") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showDateDialog = false },
            confirmButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                dateFormatter = dateFormatter
            )
        }
    }

    if (showStartTimeDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = 10,
            initialMinute = 0,
            is24Hour = true
        )

        TimePickerDialog(
            title = {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            },
            onDismissRequest = { showStartTimeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onStartTimeChanged(timePickerState.hour, timePickerState.minute)
                        showStartTimeDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimeDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    if (showEndTimeDialog) {
        val endTimePickerState = rememberTimePickerState(
            initialHour = 11,
            initialMinute = 0,
            is24Hour = true
        )

        TimePickerDialog(
            title = {
                Text(
                    text = "Select End Time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            },
            onDismissRequest = { showEndTimeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEndTimeChanged(endTimePickerState.hour, endTimePickerState.minute)
                        showEndTimeDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimeDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }
}