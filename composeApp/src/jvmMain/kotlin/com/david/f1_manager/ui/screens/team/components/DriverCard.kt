package com.david.f1_manager.ui.screens.team.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.david.f1_manager.Driver

/**
 * Card component to display driver information
 */
@Composable
fun DriverCard(
    driver: Driver,
    onAddToTeam: ((Driver) -> Unit)? = null,
    onRemoveFromTeam: ((Driver) -> Unit)? = null,
    onDelete: ((Driver) -> Unit)? = null,
    isInTeam: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Driver name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = driver.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isInTeam)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (isInTeam) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "IN TEAM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Skill rating
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Skill Rating",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isInTeam)
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${driver.skill}/100",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isInTeam)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                // Skill bar
                LinearProgressIndicator(
                    progress = { driver.skill / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = when {
                        driver.skill >= 90 -> MaterialTheme.colorScheme.secondary // Gold for top drivers
                        driver.skill >= 75 -> MaterialTheme.colorScheme.primary // Red for good drivers
                        else -> MaterialTheme.colorScheme.tertiary // Green for average
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isInTeam) {
                    // Remove from team button
                    onRemoveFromTeam?.let { handler ->
                        OutlinedButton(
                            onClick = { handler(driver) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Remove")
                        }
                    }
                } else {
                    // Add to team button
                    onAddToTeam?.let { handler ->
                        Button(
                            onClick = { handler(driver) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Add to Team")
                        }
                    }

                    // Delete button (only for available drivers)
                    onDelete?.let { handler ->
                        OutlinedButton(
                            onClick = { handler(driver) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
    }
}
