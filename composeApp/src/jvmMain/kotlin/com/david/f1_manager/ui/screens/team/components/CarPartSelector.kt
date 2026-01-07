package com.david.f1_manager.ui.screens.team.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.david.f1_manager.car.CarPart

/**
 * Component for selecting car parts with dropdown
 */
@Composable
fun <T : CarPart> CarPartSelector(
    label: String,
    currentPart: T?,
    availableParts: List<T>,
    onPartSelected: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Current selection display
            if (currentPart != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentPart.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Performance: ${currentPart.performance}/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Performance indicator
                    LinearProgressIndicator(
                        progress = { currentPart.performance / 100f },
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            } else {
                Text(
                    text = "Not selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Dropdown button
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (currentPart != null) "Change" else "Select")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    // Option to clear selection
                    if (currentPart != null) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Remove",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                onPartSelected(null)
                                expanded = false
                            }
                        )
                        HorizontalDivider()
                    }

                    // All available parts
                    availableParts.forEach { part ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = part.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Perf: ${part.performance}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    if (part.id == currentPart?.id) {
                                        Text(
                                            text = "✓",
                                            color = MaterialTheme.colorScheme.primary,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onPartSelected(part)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
