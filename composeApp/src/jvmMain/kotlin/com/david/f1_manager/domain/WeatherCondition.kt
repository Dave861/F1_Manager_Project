package com.david.f1_manager.domain

/**
 * Weather conditions that affect grip during races
 * Moved from Track.java to be simulation-only (not track property)
 */
enum class WeatherCondition {
    DRY,         // 100% grip (1.0 multiplier)
    LIGHT_RAIN,  // 90% grip (0.9 multiplier)
    HEAVY_RAIN   // 80% grip (0.8 multiplier)
}
