package com.david.f1_manager.ui.screens.team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.david.f1_manager.Driver
import com.david.f1_manager.car.*
import com.david.f1_manager.data.DataStore
import com.david.f1_manager.data.DatabaseRepository
import com.david.f1_manager.UserRole
import com.david.f1_manager.ui.components.RequiresAdmin
import com.david.f1_manager.ui.screens.team.components.CarPartSelector
import com.david.f1_manager.ui.screens.team.components.DriverCard

@Composable
fun TeamManagementScreen(
    currentRole: UserRole
) {
    RequiresAdmin(currentRole = currentRole) {
        // Observe refresh trigger to force recomposition
        val refreshTrigger = DataStore.refreshTrigger.value

        var selectedTab by remember { mutableStateOf(0) }
        val playerTeam = DataStore.currentManagedTeam ?: DataStore.teams["player_team"]
        val currentPerformance = playerTeam?.car?.calculateOverallPerformance()

        // Direct functions to modify DataStore with database persistence
        fun addDriverToTeam(driver: Driver) {
            val teamId = playerTeam?.id ?: return
            val repo = DatabaseRepository()

            if (repo.addDriverToTeamInDB(driver.id, teamId)) {
                playerTeam.addDriver(driver)
                DataStore.notifyChange()
            } else {
                println("Error: Failed to add driver to team in database")
            }
        }

        fun removeDriverFromTeam(driver: Driver) {
            val repo = DatabaseRepository()

            if (repo.removeDriverFromTeamInDB(driver.id)) {
                playerTeam?.removeDriver(driver)
                DataStore.notifyChange()
            } else {
                println("Error: Failed to remove driver from team in database")
            }
        }

        fun createDriver(name: String, skill: Int) {
            val repo = DatabaseRepository()
            val newDriver = repo.createDriver(name, skill)
            if (newDriver != null) {
                DataStore.drivers[newDriver.id] = newDriver
                DataStore.notifyChange()
            } else {
                println("Error: Failed to create driver in database")
            }
        }

        fun deleteDriver(driver: Driver) {
            val repo = DatabaseRepository()

            // Check if driver is on player team (in-memory check for immediate feedback)
            if (playerTeam?.drivers?.contains(driver) == true) {
                println("Cannot delete driver: currently on team")
                return
            }

            // Attempt database deletion (will also check team assignment)
            if (repo.deleteDriver(driver.id)) {
                DataStore.drivers.remove(driver.id)
                DataStore.notifyChange()
            } else {
                println("Error: Failed to delete driver - may be assigned to a team")
            }
        }

        fun assignEngine(engine: Engine?) {
            val carId = playerTeam?.car?.id ?: return
            val repo = DatabaseRepository()

            if (repo.updateCarEngine(carId, engine?.id)) {
                playerTeam?.car?.engine = engine
                DataStore.notifyChange()
            }
        }

        fun assignAerodynamics(aero: Aerodynamics?) {
            val carId = playerTeam?.car?.id ?: return
            val repo = DatabaseRepository()

            if (repo.updateCarAerodynamics(carId, aero?.id)) {
                playerTeam?.car?.aerodynamics = aero
                DataStore.notifyChange()
            }
        }

        fun assignTires(tires: Tires?) {
            val carId = playerTeam?.car?.id ?: return
            val repo = DatabaseRepository()

            if (repo.updateCarTires(carId, tires?.id)) {
                playerTeam?.car?.tires = tires
                DataStore.notifyChange()
            }
        }

        fun assignGearbox(gearbox: Gearbox?) {
            val carId = playerTeam?.car?.id ?: return
            val repo = DatabaseRepository()

            if (repo.updateCarGearbox(carId, gearbox?.id)) {
                playerTeam?.car?.gearbox = gearbox
                DataStore.notifyChange()
            }
        }

        fun assignSuspension(suspension: Suspension?) {
            val carId = playerTeam?.car?.id ?: return
            val repo = DatabaseRepository()

            if (repo.updateCarSuspension(carId, suspension?.id)) {
                playerTeam?.car?.suspension = suspension
                DataStore.notifyChange()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title header
            Text(
                text = "Team Management",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Set up your drivers and car",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Tabs
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Drivers (${playerTeam?.drivers?.size ?: 0}/2)") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Car Parts (${currentPerformance?.let { String.format("%.1f", it) } ?: "N/A"})") }
                )
            }

            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> DriversTab(
                        playerTeam = playerTeam,
                        onAddDriverToTeam = ::addDriverToTeam,
                        onRemoveDriverFromTeam = ::removeDriverFromTeam,
                        onCreateDriver = ::createDriver,
                        onDeleteDriver = ::deleteDriver
                    )
                    1 -> CarPartsTab(
                        playerTeam = playerTeam,
                        onAssignEngine = ::assignEngine,
                        onAssignAerodynamics = ::assignAerodynamics,
                        onAssignTires = ::assignTires,
                        onAssignGearbox = ::assignGearbox,
                        onAssignSuspension = ::assignSuspension
                    )
                }
            }
        }
    }
}

@Composable
private fun DriversTab(
    playerTeam: com.david.f1_manager.Team?,
    onAddDriverToTeam: (Driver) -> Unit,
    onRemoveDriverFromTeam: (Driver) -> Unit,
    onCreateDriver: (String, Int) -> Unit,
    onDeleteDriver: (Driver) -> Unit
) {
    // Observe refresh trigger to force recomposition of lists
    val refreshTrigger = DataStore.refreshTrigger.value

    var showAddDialog by remember { mutableStateOf(false) }

    // Recalculate these on every recomposition (when refresh trigger changes)
    val teamDrivers = (playerTeam?.drivers ?: emptyList()).toList() // Create new list
    val allDrivers = DataStore.drivers.values.toList()
    val availableDrivers = allDrivers.filter { it !in teamDrivers }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Team drivers section
        item {
            Text(
                text = "Your Team Drivers",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (teamDrivers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "No drivers in team. Add drivers from available list below.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(teamDrivers) { driver ->
                DriverCard(
                    driver = driver,
                    onRemoveFromTeam = { onRemoveDriverFromTeam(it) },
                    isInTeam = true
                )
            }
        }

        // Available drivers section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Drivers",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Create Driver", color = Color.Black)
                }
            }
        }

        items(availableDrivers) { driver ->
            DriverCard(
                driver = driver,
                onAddToTeam = if (teamDrivers.size < 2) {
                    { onAddDriverToTeam(it) }
                } else null,
                onDelete = { onDeleteDriver(it) },
                isInTeam = false
            )
        }
    }

    // Create driver dialog
    if (showAddDialog) {
        CreateDriverDialog(
            onDismiss = { showAddDialog = false },
            onCreate = { name, skill ->
                onCreateDriver(name, skill)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CarPartsTab(
    playerTeam: com.david.f1_manager.Team?,
    onAssignEngine: (Engine?) -> Unit,
    onAssignAerodynamics: (Aerodynamics?) -> Unit,
    onAssignTires: (Tires?) -> Unit,
    onAssignGearbox: (Gearbox?) -> Unit,
    onAssignSuspension: (Suspension?) -> Unit
) {
    // Observe refresh trigger to force recomposition when parts change
    val refreshTrigger = DataStore.refreshTrigger.value

    // Recalculate these on every recomposition
    val availableEngines = DataStore.carParts.values.filterIsInstance<Engine>()
    val availableAero = DataStore.carParts.values.filterIsInstance<Aerodynamics>()
    val availableTires = DataStore.carParts.values.filterIsInstance<Tires>()
    val availableGearboxes = DataStore.carParts.values.filterIsInstance<Gearbox>()
    val availableSuspensions = DataStore.carParts.values.filterIsInstance<Suspension>()

    // Force re-read of current parts
    val currentEngine = playerTeam?.car?.engine
    val currentAero = playerTeam?.car?.aerodynamics
    val currentTires = playerTeam?.car?.tires
    val currentGearbox = playerTeam?.car?.gearbox
    val currentSuspension = playerTeam?.car?.suspension

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Configure Your Car",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            CarPartSelector(
                label = "Engine (35% weight)",
                currentPart = currentEngine,
                availableParts = availableEngines,
                onPartSelected = { onAssignEngine(it) }
            )
        }

        item {
            CarPartSelector(
                label = "Aerodynamics (25% weight)",
                currentPart = currentAero,
                availableParts = availableAero,
                onPartSelected = { onAssignAerodynamics(it) }
            )
        }

        item {
            CarPartSelector(
                label = "Tires (20% weight)",
                currentPart = currentTires,
                availableParts = availableTires,
                onPartSelected = { onAssignTires(it) }
            )
        }

        item {
            CarPartSelector(
                label = "Gearbox (10% weight)",
                currentPart = currentGearbox,
                availableParts = availableGearboxes,
                onPartSelected = { onAssignGearbox(it) }
            )
        }

        item {
            CarPartSelector(
                label = "Suspension (10% weight)",
                currentPart = currentSuspension,
                availableParts = availableSuspensions,
                onPartSelected = { onAssignSuspension(it) }
            )
        }
    }
}

@Composable
private fun CreateDriverDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf(75) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Driver") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Driver Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column {
                    Text(
                        text = "Skill Rating: $skill",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Slider(
                        value = skill.toFloat(),
                        onValueChange = { skill = it.toInt() },
                        valueRange = 1f..100f,
                        steps = 98,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, skill) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
