package com.david.f1_manager.data

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.david.f1_manager.Driver
import com.david.f1_manager.Team
import com.david.f1_manager.Track
import com.david.f1_manager.ai.AITeam
import com.david.f1_manager.car.*
import com.david.f1_manager.User
import com.david.f1_manager.UserRole
import com.david.f1_manager.domain.RaceResult

object DataStore {
    val teams = mutableMapOf<String, Team>()
    val drivers = mutableMapOf<String, Driver>()
    val carParts = mutableMapOf<String, CarPart>()
    val tracks = mutableMapOf<String, Track>()
    val aiTeams = mutableListOf<AITeam>()
    val raceResults = mutableListOf<RaceResult>()
    val users = mutableMapOf<String, User>()

    // Refresh trigger for Compose recomposition
    private val _refreshTrigger = mutableStateOf(0)
    val refreshTrigger: State<Int> = _refreshTrigger

    // Loading state for async operations
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    // Currently logged-in user
    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    // The managed team for the current user (derived from currentUser.managedTeamId)
    val currentManagedTeam: Team?
        get() = _currentUser.value?.managedTeamId?.let { teams[it] }

    fun notifyChange() {
        _refreshTrigger.value++
    }

    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun clearCurrentUser() {
        _currentUser.value = null
    }

    /**
     * Initialize data from database
     */
    fun loadData() {
        _isLoading.value = true

        try {
            val repo = DatabaseRepository()

            // Load in correct order (dependencies first)
            println("Loading users from database...")
            users.putAll(repo.loadUsers())

            println("Loading drivers from database...")
            drivers.putAll(repo.loadDrivers())

            println("Loading car parts from database...")
            val engines = repo.loadEngines()
            val aeros = repo.loadAerodynamics()
            val tires = repo.loadTires()
            val suspensions = repo.loadSuspensions()
            val gearboxes = repo.loadGearboxes()

            // Add all parts to carParts map
            carParts.putAll(engines)
            carParts.putAll(aeros)
            carParts.putAll(tires)
            carParts.putAll(suspensions)
            carParts.putAll(gearboxes)

            println("Loading cars from database...")
            val cars = repo.loadCars(engines, aeros, tires, suspensions, gearboxes)

            println("Loading teams from database...")
            val (loadedTeams, loadedAITeams) = repo.loadTeams(cars, drivers)
            teams.putAll(loadedTeams)
            aiTeams.addAll(loadedAITeams)

            println("Loading tracks from database...")
            tracks.putAll(repo.loadTracks())

            println("Database loaded successfully!")
            println("- ${users.size} users")
            println("- ${drivers.size} drivers")
            println("- ${carParts.size} car parts")
            println("- ${teams.size} teams")
            println("- ${aiTeams.size} AI teams")
            println("- ${tracks.size} tracks")
        } finally {
            _isLoading.value = false
        }
    }
}
