package com.example.gymtrack.navigation

sealed class Screen(val route: String, val title: String) {

    // Auth
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar contraseña")

    // App principales
    object Home : Screen("home", "Inicio")
    object Timer : Screen("timer", "Temporizador")
    object Settings : Screen("settings", "Ajustes")

    // Rutinas
    object RegisterRoutine : Screen("register_routine", "Registrar rutina")
    object MyRoutines : Screen("my_routines", "Mis rutinas")
    object RoutineDetail : Screen("routine_detail/{routineId}", "Detalle rutina") {
        fun createRoute(routineId: String) = "routine_detail/$routineId"
    }

    object FavoriteRoutines : Screen("favoritas", "Rutinas favoritas")
    object PredefinedRoutines : Screen("predefined_routines", "Rutinas predefinidas")

}