package com.david.f1_manager.ui.screens.race.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable dialog for selecting driver(s) when triggering race events
 * Features multi-select checkboxes and clear event descriptions
 */
@Composable
fun DriverSelectionDialog(
    eventTypeLabel: String,
    drivers: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDrivers by remember { mutableStateOf(setOf<String>()) }

    val (title, description) = when (eventTypeLabel) {
        "CRASH" -> Pair(
            "Select Driver(s) to Crash",
            "The selected driver(s) will receive a +20 second time penalty"
        )
        "PIT_STOP" -> Pair(
            "Select Driver(s) to Pit",
            "The selected driver(s) will receive a +3 second time penalty"
        )
        else -> Pair(
            "Select Driver(s)",
            "Choose which driver(s) to affect"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(drivers) { driver ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = selectedDrivers.contains(driver),
                                onCheckedChange = { checked ->
                                    selectedDrivers = if (checked) {
                                        selectedDrivers + driver
                                    } else {
                                        selectedDrivers - driver
                                    }
                                }
                            )
                            Text(
                                text = driver,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedDrivers.toList()) },
                enabled = selectedDrivers.isNotEmpty()
            ) {
                Text("Confirm (${selectedDrivers.size})")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
