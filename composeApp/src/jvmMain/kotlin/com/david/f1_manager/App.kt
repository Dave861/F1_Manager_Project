package com.david.f1_manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.david.f1_manager.data.DataStore
import com.david.f1_manager.UserRole
import com.david.f1_manager.domain.WeatherCondition
import com.david.f1_manager.ui.navigation.Screen
import com.david.f1_manager.ui.screens.admin.AdminLoginScreen
import com.david.f1_manager.ui.screens.home.HomeScreen
import com.david.f1_manager.ui.screens.race.setup.RaceSetupScreen
import com.david.f1_manager.ui.screens.race.view.RaceViewScreen
import com.david.f1_manager.ui.screens.team.TeamManagementScreen
import com.david.f1_manager.ui.theme.F1Theme

@Composable
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var currentUser by remember { mutableStateOf<User?>(null) }
    val currentRole = currentUser?.role ?: UserRole.VIEWER

    // Race state for passing data between setup and view
    var currentRaceParticipants by remember { mutableStateOf<List<Pair<Driver, Team>>>(emptyList()) }
    var currentRaceTrack by remember { mutableStateOf<Track?>(null) }
    var currentRaceWeather by remember { mutableStateOf(WeatherCondition.DRY) }

    // Data initialization
    remember {
        DataStore.loadData()
    }

    // Race state managed directly in screens now

    F1Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                is Screen.AdminLogin -> {
                    AdminLoginScreen(
                        onLoginSuccess = { user ->
                            currentUser = user
                            DataStore.setCurrentUser(user)
                            currentScreen = Screen.Home
                        },
                        onBack = {
                            currentScreen = Screen.Home
                        }
                    )
                }
                else -> {
                    MainContent(
                        currentScreen = currentScreen,
                        currentUser = currentUser,
                        raceParticipants = currentRaceParticipants,
                        raceTrack = currentRaceTrack,
                        raceWeather = currentRaceWeather,
                        onNavigate = { screen -> currentScreen = screen },
                        onStartRace = { participants, track, weather ->
                            currentRaceParticipants = participants
                            currentRaceTrack = track
                            currentRaceWeather = weather
                        },
                        onLogout = {
                            currentUser = null
                            DataStore.clearCurrentUser()
                            currentScreen = Screen.Home
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContent(
    currentScreen: Screen,
    currentUser: User?,
    raceParticipants: List<Pair<Driver, Team>>,
    raceTrack: Track?,
    raceWeather: WeatherCondition,
    onNavigate: (Screen) -> Unit,
    onStartRace: (List<Pair<Driver, Team>>, Track, WeatherCondition) -> Unit,
    onLogout: () -> Unit
) {
    val currentRole = currentUser?.role ?: UserRole.VIEWER
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Sidebar Navigation
        F1NavigationRail(
            currentScreen = currentScreen,
            currentRole = currentRole,
            onNavigate = onNavigate,
            onLogout = onLogout
        )

        // Content Area - takes remaining space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(currentUser = currentUser)
                Screen.TeamManagement -> TeamManagementScreen(currentRole)
                Screen.RaceSetup -> {
                    RaceSetupScreen(
                        currentRole = currentRole,
                        onStartRace = { raceId, participants, track, weather ->
                            onStartRace(participants, track, weather)
                            onNavigate(Screen.RaceView(raceId))
                        }
                    )
                }
                is Screen.RaceView -> {
                    if (raceTrack != null && raceParticipants.isNotEmpty()) {
                        RaceViewScreen(
                            raceId = currentScreen.raceId,
                            participants = raceParticipants,
                            track = raceTrack,
                            initialWeather = raceWeather,
                            onBack = {
                                onNavigate(Screen.Home)
                            }
                        )
                    } else {
                        // Fallback if race data is missing
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Race data not available",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { onNavigate(Screen.RaceSetup) },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Return to Race Setup")
                            }
                        }
                    }
                }
                else -> HomeScreen()
            }
        }
    }
}

@Composable
private fun F1NavigationRail(
    currentScreen: Screen,
    currentRole: UserRole,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    NavigationRail(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        header = {
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🏎️", style = MaterialTheme.typography.headlineSmall)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Role badge
                Surface(
                    color = if (currentRole == UserRole.ADMIN)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = currentRole.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (currentRole == UserRole.ADMIN)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        // Home
        NavigationRailItem(
            icon = { Text("🏠", style = MaterialTheme.typography.headlineSmall) },
            label = { Text("Home") },
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Team Management (Admin only)
        if (currentRole.canManageTeam()) {
            NavigationRailItem(
                icon = { Text("👥", style = MaterialTheme.typography.headlineSmall) },
                label = { Text("Team") },
                selected = currentScreen is Screen.TeamManagement,
                onClick = { onNavigate(Screen.TeamManagement) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        // Race Setup (Admin only)
        if (currentRole.canRunRaces()) {
            NavigationRailItem(
                icon = { Text("🏁", style = MaterialTheme.typography.headlineSmall) },
                label = { Text("Race") },
                selected = currentScreen is Screen.RaceSetup,
                onClick = { onNavigate(Screen.RaceSetup) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Admin login/logout
        NavigationRailItem(
            icon = {
                Text(
                    if (currentRole == UserRole.ADMIN) "🔓" else "🔒",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            label = {
                Text(if (currentRole == UserRole.ADMIN) "Logout" else "Login")
            },
            selected = false,
            onClick = {
                if (currentRole == UserRole.ADMIN) {
                    onLogout()
                } else {
                    onNavigate(Screen.AdminLogin)
                }
            },
            colors = NavigationRailItemDefaults.colors(
                selectedIconColor = if (currentRole == UserRole.ADMIN)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.secondary,
                unselectedIconColor = if (currentRole == UserRole.ADMIN)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.secondary
            )
        )
    }
}

