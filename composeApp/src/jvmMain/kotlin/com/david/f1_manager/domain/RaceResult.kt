package com.david.f1_manager.domain

import java.time.LocalDateTime

/**
 * Represents the result of a completed race
 * Simplified Kotlin data classes - no boilerplate!
 */
data class RaceResult(
    val trackName: String,
    val raceDate: LocalDateTime = LocalDateTime.now(),
    val teamResults: List<TeamResult> = emptyList()
)

/**
 * Individual driver's result in the race
 */
data class TeamResult(
    val driverName: String,
    val teamName: String,
    val position: Int,
    val totalTime: Double  // Total race time in seconds
)
