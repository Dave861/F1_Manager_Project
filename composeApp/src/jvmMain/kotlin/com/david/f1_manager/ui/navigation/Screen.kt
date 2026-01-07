package com.david.f1_manager.ui.navigation

sealed interface Screen {
    data object Home : Screen
    data object AdminLogin : Screen
    data object TeamManagement : Screen
    data object RaceSetup : Screen
    data class RaceView(val raceId: String) : Screen
}
