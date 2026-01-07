package com.david.f1_manager.domain

import androidx.compose.runtime.*
import com.david.f1_manager.Driver
import com.david.f1_manager.Team
import com.david.f1_manager.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Live race simulator that runs lap-by-lap with real-time updates
 * 1 lap = 1 second in real time
 * Enhanced with weather, safety car, and auto-overtake detection
 */
class LiveRaceSimulator(
    private val participants: List<Pair<Driver, Team>>,
    private val track: Track,
    initialWeather: WeatherCondition = WeatherCondition.DRY
) {
    var currentLap by mutableStateOf(0)
        private set

    var standings by mutableStateOf<List<RaceStanding>>(emptyList())
        private set

    var isRacing by mutableStateOf(false)
        private set

    var isFinished by mutableStateOf(false)
        private set

    // Structured event log with emojis
    var eventLog by mutableStateOf<List<RaceEventLog>>(emptyList())
        private set

    // Weather state that affects all drivers
    var weatherState by mutableStateOf(WeatherState(WeatherCondition.DRY, 1.0))
        private set

    // Safety car state that slows all drivers
    var safetyCarState by mutableStateOf(SafetyCarState())
        private set

    // Time penalties for each participant (in seconds)
    private val penalties = mutableMapOf<String, Double>()

    // Cumulative race times tracked lap-by-lap (in seconds)
    // This allows weather changes to affect only future laps, not historical ones
    private val cumulativeTimes = mutableMapOf<String, Double>()

    init {
        // Initialize standings with starting positions
        standings = participants.mapIndexed { index, (driver, team) ->
            RaceStanding(
                position = index + 1,
                driverName = driver.name,
                teamName = team.name,
                totalTime = 0.0,
                gap = if (index == 0) "Leader" else "0.0s"
            )
        }

        // Initialize weather state from parameter
        weatherState = WeatherState(initialWeather, getGripMultiplier(initialWeather))

        // Initialize cumulative times to 0.0 for all drivers
        participants.forEach { (driver, _) ->
            cumulativeTimes[driver.name] = 0.0
        }
    }

    /**
     * Lap time calculation with percentage-based bonuses.
     *
     * Formula breakdown:
     *   baseLapTime = 90 seconds (reference lap time)
     *   totalBonus = driver (0-10%) + car (0-5%) + track (0-5%) = max 20% improvement
     *   weatherPenalty = 0% (dry), 10% (light rain), 20% (heavy rain)
     *   safetyCarMultiplier = 1.0 (normal) or 1.5 (50% slower under safety car)
     *
     *   finalLapTime = 90 * (1 - totalBonus) * (1 + weatherPenalty) * safetyCarMultiplier
     *
     * Example (Max Verstappen, 95 skill, RB20 with 93 performance, Monza SPEED, DRY):
     *   driverBonus = 0.095 (9.5%), carBonus = 0.0465 (4.65%), trackBonus = 0.047 (4.7%)
     *   totalBonus = 0.1885 (18.85% improvement)
     *   lapTime = 90 * (1 - 0.1885) = 73.04 seconds
     *
     */
    private fun calculateLapTime(driver: Driver, team: Team): Double {
        val baseLapTime = RaceConstants.BASE_LAP_TIME_SECONDS

        // 1. Driver skill bonus (0-10%)
        val driverBonus = (driver.skill / 100.0) * RaceConstants.MAX_DRIVER_BONUS

        // 2. Car performance bonus (0-5%)
        val carPerformance = team.car?.calculateOverallPerformance() ?: 50.0
        val carBonus = (carPerformance / 100.0) * RaceConstants.MAX_CAR_BONUS

        // 3. Track characteristic bonus
        val trackBonus = calculateTrackBonus(team, track.characteristics)

        // 4. Weather penalty
        val weatherPenalty = 1.0 - weatherState.gripMultiplier

        // 5. Safety car slowdown
        val safetyCarMultiplier = if (safetyCarState.isActive) RaceConstants.SAFETY_CAR_SPEED_MULTIPLIER else 1.0

        // Final calculation
        val totalBonus = driverBonus + carBonus + trackBonus
        return baseLapTime * (1.0 - totalBonus) * (1.0 + weatherPenalty) * safetyCarMultiplier
    }

    /**
     * Calculate track-specific bonus based on car characteristics
     */
    private fun calculateTrackBonus(team: Team, characteristic: Track.TrackCharacteristics?): Double {
        val car = team.car ?: return 0.0

        return when (characteristic) {
            Track.TrackCharacteristics.SPEED -> {
                // Engine power bonus (up to 5%)
                val enginePerf = (car.engine?.performance ?: 50) / 100.0
                enginePerf * RaceConstants.MAX_TRACK_BONUS_SPECIALIZED
            }
            Track.TrackCharacteristics.TECHNICAL -> {
                // Aero bonus (up to 5%)
                val aeroPerf = (car.aerodynamics?.performance ?: 50) / 100.0
                aeroPerf * RaceConstants.MAX_TRACK_BONUS_SPECIALIZED
            }
            Track.TrackCharacteristics.BALANCED -> {
                // BALANCED tracks get 3% max bonus (vs 5% for specialized tracks)
                // Why? Because the reward is spread across all car components equally,
                // no single strength dominates. On SPEED tracks, a great engine gives
                // full 5% bonus. On BALANCED, you need the whole package to excel.
                val overallPerf = car.calculateOverallPerformance() / 100.0
                overallPerf * RaceConstants.MAX_TRACK_BONUS_BALANCED
            }
            else -> 0.0
        }
    }

    /**
     * Start the race simulation
     */
    fun startRace() {
        if (isRacing || isFinished) return

        isRacing = true
        currentLap = 0
        addEventLog(RaceEventIcons.RACE_START, "Race started at ${track.name}!")

        CoroutineScope(Dispatchers.Default).launch {
            for (lap in 1..track.laps) {
                delay(1000L) // 1 second per lap
                currentLap = lap

                // Accumulate lap times based on CURRENT conditions
                // This ensures weather/safety car changes only affect the current lap
                participants.forEach { (driver, team) ->
                    val lapTime = calculateLapTime(driver, team)
                    cumulativeTimes[driver.name] = (cumulativeTimes[driver.name] ?: 0.0) + lapTime
                }

                // Store previous standings for overtake detection
                val previousStandings = standings.toList()

                // Update standings based on lap times
                updateStandings()

                // Detect overtakes
                detectOvertakes(previousStandings, standings)

                // Countdown safety car duration each lap
                // Safety car is deployed for 3-5 laps (random), then automatically removed
                // During safety car: all drivers lap 50% slower (lapTime × 1.5)
                if (safetyCarState.isActive) {
                    val remaining = safetyCarState.lapsRemaining - 1
                    if (remaining <= 0) {
                        // Safety car period ended - return to normal racing
                        safetyCarState = SafetyCarState(false, 0)
                        addEventLog(RaceEventIcons.RACE_START, "Safety car returns to pits")
                    } else {
                        // Still under safety car - decrement remaining laps
                        safetyCarState = safetyCarState.copy(lapsRemaining = remaining)
                    }
                }

                // Add lap completion event every 10 laps
                if (lap % 10 == 0 || lap == track.laps) {
                    addEventLog(RaceEventIcons.LAP_COMPLETE, "Lap $lap/${track.laps} completed")
                }
            }

            isRacing = false
            isFinished = true
            addEventLog(RaceEventIcons.RACE_FINISH, "Race finished! Winner: ${standings.firstOrNull()?.driverName ?: "Unknown"}")
        }
    }

    /**
     * Detect overtakes by comparing previous and current standings
     */
    private fun detectOvertakes(previous: List<RaceStanding>, current: List<RaceStanding>) {
        previous.forEachIndexed { index, prevStanding ->
            val currentPos = current.indexOfFirst { it.driverName == prevStanding.driverName }
            if (currentPos != -1 && currentPos < index) {
                val overtakenDriver = current.getOrNull(currentPos + 1)?.driverName ?: "unknown"
                addEventLog(RaceEventIcons.OVERTAKE, "${prevStanding.driverName} overtakes $overtakenDriver!")
            }
        }
    }

    /**
     * Trigger a crash event for selected drivers
     */
    fun triggerCrash(driverNames: List<String>) {
        if (!isRacing || driverNames.isEmpty()) return

        driverNames.forEach { driverName ->
            // CUMULATIVE penalties: Add 20s to any existing penalty total
            // Multiple crashes stack: crash + crash = +40s total
            // This simulates time lost from incidents (repairs, lost positions, etc.)
            penalties[driverName] = (penalties[driverName] ?: 0.0) + RaceConstants.CRASH_PENALTY
            addEventLog(RaceEventIcons.CRASH, "$driverName crashed! +20s penalty")
        }
        updateStandings()
    }

    /**
     * Trigger a pit stop event for selected drivers
     */
    fun triggerPitStop(driverNames: List<String>) {
        if (!isRacing || driverNames.isEmpty()) return

        driverNames.forEach { driverName ->
            penalties[driverName] = (penalties[driverName] ?: 0.0) + RaceConstants.PIT_STOP_TIME
            addEventLog(RaceEventIcons.PIT_STOP, "$driverName pits (+3s)")
        }
        updateStandings()
    }

    /**
     * Trigger a safety car event (affects all drivers)
     */
    fun triggerSafetyCar() {
        if (!isRacing || safetyCarState.isActive) return

        val laps = (RaceConstants.SAFETY_CAR_MIN_LAPS..RaceConstants.SAFETY_CAR_MAX_LAPS).random()
        safetyCarState = SafetyCarState(isActive = true, lapsRemaining = laps)
        addEventLog(RaceEventIcons.SAFETY_CAR, "SAFETY CAR deployed for $laps laps!")
    }

    /**
     * Trigger a weather change event (affects all drivers)
     */
    fun triggerWeatherChange(newWeather: WeatherCondition) {
        if (!isRacing) return

        val gripMultiplier = getGripMultiplier(newWeather)
        weatherState = WeatherState(newWeather, gripMultiplier)

        val weatherIcon = when (newWeather) {
            WeatherCondition.DRY -> RaceEventIcons.WEATHER_DRY
            WeatherCondition.LIGHT_RAIN -> RaceEventIcons.WEATHER_LIGHT_RAIN
            WeatherCondition.HEAVY_RAIN -> RaceEventIcons.WEATHER_HEAVY_RAIN
        }

        addEventLog(weatherIcon, "Weather: ${newWeather.name.replace("_", " ")}! Grip: ${(gripMultiplier * 100).toInt()}%")
    }

    /**
     * Update standings based on current lap and penalties
     */
    private fun updateStandings() {
        // Calculate times for all participants
        val unsortedStandings = participants.map { (driver, team) ->
            // Use ACCUMULATED lap times (tracks weather changes lap-by-lap)
            // This correctly handles weather changes - only future laps affected
            val baseTime = cumulativeTimes[driver.name] ?: 0.0
            val penalty = penalties[driver.name] ?: 0.0
            val totalTime = baseTime + penalty

            RaceStanding(
                position = 0, // Will be updated after sorting
                driverName = driver.name,
                teamName = team.name,
                totalTime = totalTime,
                gap = "" // Will be calculated after sorting
            )
        }

        // Sort by time and assign positions with gaps
        val sortedStandings = unsortedStandings.sortedBy { it.totalTime }
        val leaderTime = sortedStandings.firstOrNull()?.totalTime ?: 0.0

        standings = sortedStandings.mapIndexed { index, standing ->
            standing.copy(
                position = index + 1,
                gap = if (index == 0) "Leader" else "+${String.format("%.1f", standing.totalTime - leaderTime)}s"
            )
        }
    }

    /**
     * Add event to log, keeping only last 10 events for UI display
     */
    private fun addEventLog(icon: String, message: String) {
        eventLog = (eventLog + RaceEventLog(
            lap = currentLap,
            icon = icon,
            message = message
        )).takeLast(10)
    }

    /**
     * Convert weather condition to grip multiplier
     * DRY: 1.0 (100% grip, 0% penalty)
     * LIGHT_RAIN: 0.9 (90% grip, 10% penalty)
     * HEAVY_RAIN: 0.8 (80% grip, 20% penalty)
     */
    private fun getGripMultiplier(weather: WeatherCondition): Double {
        return when (weather) {
            WeatherCondition.DRY -> RaceConstants.GRIP_DRY
            WeatherCondition.LIGHT_RAIN -> RaceConstants.GRIP_LIGHT_RAIN
            WeatherCondition.HEAVY_RAIN -> RaceConstants.GRIP_HEAVY_RAIN
        }
    }
}
