package com.example.padelconnect.ui.create_match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateMatchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showTimeDialog by remember {mutableStateOf(false)}
    var showDateDialog by remember {mutableStateOf(false)}

    // Default camera centered on Plovdiv, Bulgaria
    val defaultLocation = LatLng(42.1354, 24.7453)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val datePickerState = rememberDatePickerState()
    val dateFormatter = remember { DatePickerDefaults.dateFormatter() }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }

            val formattedDate = formatter.format(Date(millis))
            viewModel.onDateChanged(formattedDate)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Padel Match") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "1. Select Location on Map",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        viewModel.onLocationSelected(latLng.latitude, latLng.longitude)
                    }
                ) {
                    if (uiState.hasValidLocation) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    uiState.latitude!!,
                                    uiState.longitude!!
                                )
                            ),
                            title = "Match Location"
                        )
                    }
                }
            }

            Text(
                text = if (uiState.hasValidLocation) {
                    "Selected: %.4f, %.4f".format(uiState.latitude, uiState.longitude)
                } else {
                    "Tap on the map to place a court marker"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (uiState.hasValidLocation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            HorizontalDivider()

            Text(
                text = "2. Match Details",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = if (uiState.dateText.isNotBlank()) "Selected date: ${uiState.dateText}"
                           else "No date selected!",
                    color = if (uiState.dateText.isNotBlank()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                )

                Button(onClick = {showDateDialog = true}){
                    Text("Select Date")
                }
            }

            if (showDateDialog){
                DatePickerDialog(
                    onDismissRequest = {showDateDialog = false},
                    confirmButton = {
                        Button(onClick = {showDateDialog = false}) {
                            Text("Confirm")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState,
                        dateFormatter = dateFormatter
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = if (uiState.timeText.isNotBlank()) "Selected time: ${uiState.timeText}"
                           else "No time selected!",
                    color = if (uiState.timeText.isNotBlank()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )

                Button(onClick = {showTimeDialog = true}){
                    Text(text = "Select Time")
                }
            }

            if (showTimeDialog){
                val timePickerState = rememberTimePickerState(
                    initialHour = 10,
                    initialMinute = 0,
                    is24Hour = true
                )

                TimePickerDialog(
                    title = {},
                    onDismissRequest = {showTimeDialog = false},
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.onTimeChanged(timePickerState.hour, timePickerState.minute)
                                showTimeDialog = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                ) {
                    TimePicker(
                        state = timePickerState
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Max Players: ${uiState.maxPlayers}")
                Row {
                    IconButton(
                        onClick = { if (uiState.maxPlayers == 4) viewModel.onMaxPlayersChanged(2) }
                    ) {
                        Text("-", style = MaterialTheme.typography.titleLarge)
                    }
                    IconButton(
                        onClick = { if (uiState.maxPlayers == 2) viewModel.onMaxPlayersChanged(4) }
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.createMatch()
                    onNavigateBack()
                },
                enabled = uiState.hasValidLocation && uiState.dateText.isNotBlank() && uiState.timeText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Create Match")
            }
        }
    }
}