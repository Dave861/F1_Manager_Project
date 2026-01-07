package com.david.f1_manager.domain

/**
 * A single event log entry with emoji icon
 */
data class RaceEventLog(
    val lap: Int,
    val icon: String,
    val message: String
)

/**
 * Safety car state that affects all drivers
 */
data class SafetyCarState(
    val isActive: Boolean = false,
    val lapsRemaining: Int = 0
)

/**
 * Weather state that affects track grip
 */
data class WeatherState(
    val condition: WeatherCondition,
    val gripMultiplier: Double  // 1.0 = dry, 0.9 = light rain, 0.8 = heavy rain
)
