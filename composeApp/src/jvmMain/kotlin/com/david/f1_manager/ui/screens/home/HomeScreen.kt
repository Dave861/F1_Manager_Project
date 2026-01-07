package com.david.f1_manager.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.david.f1_manager.data.DataStore
import com.david.f1_manager.ui.theme.Spacing

@Composable
fun HomeScreen(currentUser: com.david.f1_manager.User? = null) {
    // Observe refresh trigger for reactivity
    val refreshTrigger = DataStore.refreshTrigger.value

    // Check loading state
    val isLoading = DataStore.isLoading.value

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Loading F1 Manager data...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    // Determine which team to show
    val managedTeamId = currentUser?.managedTeamId
    val playerTeam = if (managedTeamId != null) {
        DataStore.teams[managedTeamId]
    } else {
        null  // Guest user - no managed team
    }
    val allRaces = DataStore.raceResults.toList().reversed() // Most recent first

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Welcome header
        item {
            Text(
                text = "F1 Manager",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Text(
                text = "Manage your own F1 team, configure strategies, and race against AI opponents",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.small))
        }

        // Team overview card
        item {
            if (playerTeam != null) {
                // Admin user with managed team - show team details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(Spacing.medium),
                    elevation = CardDefaults.cardElevation(Spacing.tiny)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        Text(
                            text = "Your Team: ${playerTeam.name}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Drivers",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${playerTeam.drivers.size}/2",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Car Performance",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = playerTeam.car?.let {
                                        String.format("%.1f", it.calculateOverallPerformance())
                                    } ?: "N/A",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            } else {
                // Guest user - show all team names
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(Spacing.medium)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.large),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Text(
                            text = "All Teams",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Log in to manage your team",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = Spacing.small),
                            color = MaterialTheme.colorScheme.outline
                        )

                        DataStore.teams.values.forEach { team ->
                            Text(
                                text = team.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = Spacing.tiny)
                            )
                        }
                    }
                }
            }
        }

        // Recent Races header
        item {
            Text(
                text = "Recent Races",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = Spacing.medium)
            )
        }

        // Show all races in individual cards
        if (allRaces.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No races yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Complete a race to see it here!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(allRaces) { race ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(Spacing.medium)
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Spacing.small)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = race.trackName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = race.raceDate.toLocalDate().toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        // Show top 3
                        race.teamResults.take(3).forEach { result ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                                    Text(
                                        text = "P${result.position}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = when (result.position) {
                                            1 -> MaterialTheme.colorScheme.tertiary
                                            2 -> MaterialTheme.colorScheme.secondary
                                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                                        }
                                    )
                                    Text(
                                        text = result.driverName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Text(
                                    text = String.format("%.1fs", result.totalTime),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
