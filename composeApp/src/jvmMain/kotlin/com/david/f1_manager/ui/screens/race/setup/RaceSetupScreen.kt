package com.david.f1_manager.ui.screens.race.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.david.f1_manager.Driver
import com.david.f1_manager.Team
import com.david.f1_manager.Track
import com.david.f1_manager.domain.WeatherCondition
import com.david.f1_manager.car.Tires.TireCompound
import com.david.f1_manager.UserRole
import com.david.f1_manager.ai.AITeam
import com.david.f1_manager.data.DataStore
import com.david.f1_manager.race.RaceStrategy
import com.david.f1_manager.ui.components.RequiresAdmin

@Composable
fun RaceSetupScreen(
    currentRole: UserRole,
    onStartRace: (String, List<Pair<Driver,Team>>, Track, WeatherCondition) -> Unit
) {
    RequiresAdmin(currentRole = currentRole) {
        // Observe refresh trigger to force recomposition
        val refreshTrigger = DataStore.refreshTrigger.value

        // Direct access to DataStore
        val availableTracks = DataStore.tracks.values.toList()
        val playerTeam = DataStore.currentManagedTeam
        val availableAITeams = DataStore.aiTeams

        // Simple state
        var selectedTrack by remember { mutableStateOf<Track?>(availableTracks.firstOrNull()) }
        var selectedAITeams by remember { mutableStateOf<List<AITeam>>(emptyList()) }
        var initialWeather by remember { mutableStateOf(WeatherCondition.DRY) }

        // Strategy is created and displayed in UI for demonstration purposes,
        // but is NOT passed to LiveRaceSimulator - it doesn't affect race outcomes yet.
        var playerStrategy by remember { mutableStateOf(
            RaceStrategy(
                "default",
                2,
                TireCompound.MEDIUM,
                100.0
            )
        ) }

        // Direct functions to modify state
        fun selectTrack(track: Track) {
            selectedTrack = track
        }

        fun setWeather(weather: WeatherCondition) {
            initialWeather = weather
        }

        fun toggleAITeam(aiTeam: AITeam) {
            val current = selectedAITeams.toMutableList()
            if (current.contains(aiTeam)) {
                current.remove(aiTeam)
            } else {
                if (current.size < 3) {
                    current.add(aiTeam)
                }
            }
            selectedAITeams = current
        }

        // Updates strategy state for UI display only - not used in simulation
        fun updateStrategy(pitStops: Int, tireCompound: TireCompound, fuelLoad: Double) {
            playerStrategy = RaceStrategy("player_race_strategy", pitStops, tireCompound, fuelLoad)
        }

        // Computed value that updates when dependencies change
        val canStartRace = selectedTrack != null &&
                playerTeam != null &&
                playerTeam.drivers.isNotEmpty() &&
                playerTeam.car != null &&
                selectedAITeams.isNotEmpty()

        fun buildRaceParticipants(): List<Pair<Driver, Team>> {
            val participants = mutableListOf<Pair<Driver, Team>>()

            // Add all player drivers
            playerTeam?.let { team ->
                // Add ALL drivers from the team
                team.drivers.forEach { driver ->
                    participants.add(driver to team)
                }
            }

            // Add all AI drivers
            selectedAITeams.forEach { aiTeam ->
                // Add ALL drivers from each AI team
                aiTeam.drivers.forEach { driver ->
                    participants.add(driver to aiTeam)
                }
            }

            return participants
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Race Setup",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                Text(
                    text = "Configure your strategy and race conditions",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Track Selection
            item {
                TrackSelectionCard(
                    tracks = availableTracks,
                    selectedTrack = selectedTrack,
                    onTrackSelected = ::selectTrack
                )
            }

            // Weather Selection
            item {
                WeatherSelectionCard(
                    currentWeather = initialWeather,
                    onWeatherSelected = ::setWeather
                )
            }

            // Strategy Configuration
            item {
                StrategyConfigCard(
                    strategy = playerStrategy,
                    onStrategyUpdate = { pitStops, compound, fuel ->
                        updateStrategy(pitStops, compound, fuel)
                    }
                )
            }

            // AI Opponent Selection
            item {
                Text(
                    text = "Select AI Opponents (${selectedAITeams.size}/3)",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(availableAITeams) { aiTeam ->
                AITeamCard(
                    aiTeam = aiTeam,
                    isSelected = selectedAITeams.contains(aiTeam),
                    onToggle = { toggleAITeam(aiTeam) }
                )
            }

            // Start Race Button
            item {
                Button(
                    onClick = {
                        if (canStartRace && selectedTrack != null) {
                            val raceId = "race_${System.currentTimeMillis()}"
                            val participants = buildRaceParticipants()
                            onStartRace(raceId, participants, selectedTrack!!, initialWeather)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = canStartRace,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Start Race",
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackSelectionCard(
    tracks: List<Track>,
    selectedTrack: Track?,
    onTrackSelected: (Track) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select Track",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            selectedTrack?.let { track ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Laps: ${track.laps}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Type: ${track.characteristics?.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Track")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    tracks.forEach { track ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(track.name, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "${track.laps} laps • ${track.characteristics?.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onTrackSelected(track)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherSelectionCard(
    currentWeather: WeatherCondition,
    onWeatherSelected: (WeatherCondition) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Weather Conditions",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeatherCondition.entries.forEach { weather ->
                    FilterChip(
                        selected = currentWeather == weather,
                        onClick = { onWeatherSelected(weather) },
                        label = {
                            Text(
                                weather.name.replace("_", " "),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StrategyConfigCard(
    strategy: RaceStrategy,
    onStrategyUpdate: (Int, TireCompound, Double) -> Unit
) {
    var pitStops by remember { mutableStateOf(strategy.pitStopCount) }
    var tireCompound by remember { mutableStateOf(strategy.startingTireCompound) }
    var fuelLoad by remember { mutableStateOf(strategy.initialFuelLoad) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Race Strategy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Pit Stops
            Column {
                Text("Pit Stops: $pitStops", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = pitStops.toFloat(),
                    onValueChange = {
                        pitStops = it.toInt()
                        onStrategyUpdate(pitStops, tireCompound, fuelLoad)
                    },
                    valueRange = 0f..3f,
                    steps = 2
                )
            }

            // Tire Compound
            Column {
                Text("Starting Tires", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(TireCompound.SOFT, TireCompound.MEDIUM, TireCompound.HARD).forEach { compound ->
                        FilterChip(
                            selected = tireCompound == compound,
                            onClick = {
                                tireCompound = compound
                                onStrategyUpdate(pitStops, tireCompound, fuelLoad)
                            },
                            label = { Text(compound.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Fuel Load
            Column {
                Text("Fuel Load: ${fuelLoad.toInt()} kg", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = fuelLoad.toFloat(),
                    onValueChange = {
                        fuelLoad = it.toDouble()
                        onStrategyUpdate(pitStops, tireCompound, fuelLoad)
                    },
                    valueRange = 50f..100f
                )
            }
        }
    }
}

@Composable
private fun AITeamCard(
    aiTeam: AITeam,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = aiTeam.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Strategy: ${aiTeam.strategy.name} • Drivers: ${aiTeam.drivers.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                aiTeam.drivers.firstOrNull()?.let { driver ->
                    Text(
                        text = "${driver.name} (Skill: ${driver.skill})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isSelected) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
