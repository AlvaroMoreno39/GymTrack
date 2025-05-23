package com.example.gymtrack.navigation

/**
 * Screen.kt
 *
 * Define una sealed class llamada Screen que centraliza todas las rutas de navegación de la app GymTrack.
 * Cada pantalla importante de la app se representa como un objeto con su ruta única y un título asociado.
 * Esto permite manejar la navegación de manera tipada y segura, evitando errores por rutas mal escritas.
 */
sealed class Screen(val route: String, val title: String) {

    // --- Rutas para autenticación de usuario ---
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Registro")
    object ForgotPassword : Screen("forgot_password", "Recuperar contraseña")

    // --- Rutas principales de la app ---
    object Home : Screen("home", "Inicio")
    object Timer : Screen("timer", "Temporizador")
    object Settings : Screen("settings", "Ajustes")

    // --- Rutas relacionadas con rutinas ---
    object RegisterRoutine : Screen("register_routine", "Registrar rutina")

    object RoutineDetail : Screen("routine_detail/{routineId}", "Detalle rutina") {
        fun createRoute(routineId: String) = "routine_detail/$routineId"
    }

    object RoutineList : Screen("routineList?predefined={predefined}", "Listado de rutinas") {
        fun createRoute(predefined: Boolean = false) = "routineList?predefined=$predefined"
    }

    object FavoriteRoutines : Screen("favoritas", "Rutinas favoritas")

}