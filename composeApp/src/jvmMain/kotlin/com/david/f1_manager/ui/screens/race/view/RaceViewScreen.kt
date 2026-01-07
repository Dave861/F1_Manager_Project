package com.david.f1_manager.ui.screens.race.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.david.f1_manager.Driver
import com.david.f1_manager.Team
import com.david.f1_manager.Track
import com.david.f1_manager.data.DataStore
import com.david.f1_manager.domain.WeatherCondition
import com.david.f1_manager.domain.RaceEventIcons
import com.david.f1_manager.domain.RaceResult
import com.david.f1_manager.domain.TeamResult
import com.david.f1_manager.ui.screens.race.view.components.DriverSelectionDialog
import com.david.f1_manager.ui.screens.race.view.components.LiveLeaderboard

@Composable
fun RaceViewScreen(
    raceId: String,
    participants: List<Pair<Driver, Team>>,
    track: Track,
    initialWeather: WeatherCondition,
    onBack: () -> Unit
) {
    // Create live race simulator
    val simulator = remember(raceId) {
        com.david.f1_manager.domain.LiveRaceSimulator(participants, track, initialWeather)
    }

    // Dialog state for driver selection
    var showDriverDialog by remember { mutableStateOf(false) }
    var dialogEventType by remember { mutableStateOf<String?>(null) }

    // Auto-start race on screen load
    LaunchedEffect(raceId) {
        simulator.startRace()
    }

    // Save race result when finished
    LaunchedEffect(simulator.isFinished) {
        if (simulator.isFinished) {
            // Convert standings to team results
            val teamResults = simulator.standings.map { standing ->
                TeamResult(
                    driverName = standing.driverName,
                    teamName = standing.teamName,
                    position = standing.position,
                    totalTime = standing.totalTime
                )
            }

            // Create and save race result with simplified data class
            val raceResult = RaceResult(
                trackName = track.name,
                teamResults = teamResults
            )

            DataStore.raceResults.add(raceResult)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with lap counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (simulator.isRacing) "Live Race - ${track.name}" else "Race Results - ${track.name}",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            if (simulator.isRacing || simulator.isFinished) {
                Text(
                    text = "Lap ${simulator.currentLap}/${track.laps}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (simulator.isRacing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider()

        // Main content: Two-panel layout (60% leaderboard, 40% events)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left panel: Leaderboard (60%)
            LiveLeaderboard(
                standings = simulator.standings,
                modifier = Modifier.weight(0.6f)
            )

            // Right panel: Event log and controls (40%)
            Card(
                modifier = Modifier.weight(0.4f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Race Events",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider()

                    // Event log (scrollable list of last 10 events with emojis)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        simulator.eventLog.forEach { event ->
                            Text(
                                text = "${event.icon} Lap ${event.lap}: ${event.message}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Event trigger buttons (only active during race)
                    if (simulator.isRacing) {
                        HorizontalDivider()

                        Text(
                            text = "Trigger Race Events",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Crash - opens driver selection
                            Button(
                                onClick = {
                                    dialogEventType = "CRASH"
                                    showDriverDialog = true
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("${RaceEventIcons.CRASH} Crash (+20s)")
                            }

                            // Pit Stop - opens driver selection
                            Button(
                                onClick = {
                                    dialogEventType = "PIT_STOP"
                                    showDriverDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("${RaceEventIcons.PIT_STOP} Pit Stop (+3s)")
                            }

                            // Safety Car - affects all drivers
                            Button(
                                onClick = { simulator.triggerSafetyCar() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !simulator.safetyCarState.isActive
                            ) {
                                Text(
                                    if (simulator.safetyCarState.isActive)
                                        "${RaceEventIcons.SAFETY_CAR} Safety Car (${simulator.safetyCarState.lapsRemaining} laps)"
                                    else
                                        "${RaceEventIcons.SAFETY_CAR} Safety Car (3-5 laps)"
                                )
                            }

                            // Weather buttons row
                            Text(
                                text = "Weather",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { simulator.triggerWeatherChange(WeatherCondition.DRY) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (simulator.weatherState.condition == WeatherCondition.DRY)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(RaceEventIcons.WEATHER_DRY)
                                }

                                Button(
                                    onClick = { simulator.triggerWeatherChange(WeatherCondition.LIGHT_RAIN) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (simulator.weatherState.condition == WeatherCondition.LIGHT_RAIN)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(RaceEventIcons.WEATHER_LIGHT_RAIN)
                                }

                                Button(
                                    onClick = { simulator.triggerWeatherChange(WeatherCondition.HEAVY_RAIN) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (simulator.weatherState.condition == WeatherCondition.HEAVY_RAIN)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text(RaceEventIcons.WEATHER_HEAVY_RAIN)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Winner card (only show when finished)
        if (simulator.isFinished) {
            val winner = simulator.standings.firstOrNull()
            winner?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${RaceEventIcons.RACE_FINISH} Winner: ${it.driverName}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Team: ${it.teamName} • Time: ${String.format("%.1f", it.totalTime)}s",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        // Back button
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Back Home",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    // Driver selection dialog
    if (showDriverDialog && dialogEventType != null) {
        DriverSelectionDialog(
            eventTypeLabel = dialogEventType!!,
            drivers = simulator.standings.map { it.driverName },
            onConfirm = { selectedDrivers ->
                when (dialogEventType) {
                    "CRASH" -> simulator.triggerCrash(selectedDrivers)
                    "PIT_STOP" -> simulator.triggerPitStop(selectedDrivers)
                }
                showDriverDialog = false
                dialogEventType = null
            },
            onDismiss = {
                showDriverDialog = false
                dialogEventType = null
            }
        )
    }
}
