package com.example.gymtrack.navigation

sealed class Screen(val route: String, val title: String) {
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar contraseña")

    object Home : Screen("home", "Inicio")
    object Timer : Screen("timer", "Temporizador")
    object GeneralProgress : Screen("general_progress", "Progreso")
    object Settings : Screen("settings", "Ajustes")

    object RegisterRoutine : Screen("register_routine", "Registrar rutina")
    object ViewRoutinesScreen : Screen("view_routines", "Ver rutinas")
    object PredefinedRoutines : Screen("predefined_routines", "Rutinas predefinidas")
    object ExerciseDashboard : Screen("exercise_dashboard", "Progreso por ejercicio")

    companion object {
        val bottomBarScreens = listOf(Home, Timer, GeneralProgress, Settings)
    }
}