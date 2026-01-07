package com.david.f1_manager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "F1 Manager",
    ) {
        App()
    }
}