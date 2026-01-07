package com.david.f1_manager.domain

/**
 * Constants for race simulation calculations and penalties
 */
object RaceConstants {
    // Penalties (in seconds)
    const val CRASH_PENALTY = 20.0
    const val PIT_STOP_TIME = 3.0

    // Safety car settings
    const val SAFETY_CAR_MIN_LAPS = 3
    const val SAFETY_CAR_MAX_LAPS = 5
    const val SAFETY_CAR_SPEED_MULTIPLIER = 1.5

    // Lap time calculation
    const val BASE_LAP_TIME_SECONDS = 90.0
    const val MAX_DRIVER_BONUS = 0.10
    const val MAX_CAR_BONUS = 0.05
    const val MAX_TRACK_BONUS_SPECIALIZED = 0.05
    const val MAX_TRACK_BONUS_BALANCED = 0.03

    // Weather grip multipliers
    const val GRIP_DRY = 1.0
    const val GRIP_LIGHT_RAIN = 0.9
    const val GRIP_HEAVY_RAIN = 0.8
}
