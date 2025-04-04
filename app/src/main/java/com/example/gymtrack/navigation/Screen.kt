package com.example.gymtrack.navigation

sealed class Screen(val route: String, val title: String) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object Home : Screen("home", "Inicio")
    object Progress : Screen("progress", "Progreso")
    object Timer : Screen("timer", "Temporizador")
    object Settings : Screen("settings", "Ajustes")

    companion object {
        val bottomBarScreens = listOf(Home, Progress, Timer, Settings)
    }
}