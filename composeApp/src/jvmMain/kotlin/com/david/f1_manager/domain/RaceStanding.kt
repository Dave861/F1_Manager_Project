package com.david.f1_manager.domain

/**
 * Represents a driver's position in the race leaderboard
 * Used during live race simulation
 */
data class RaceStanding(
    val position: Int,
    val driverName: String,
    val teamName: String,
    val totalTime: Double,  // Total race time in seconds
    val gap: String         // Gap to leader
)
