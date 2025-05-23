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
    object Login : Screen("login", "Login")                   // Pantalla de inicio de sesión
    object Register : Screen("register", "Registro")          // Pantalla de registro de usuario
    object ForgotPassword : Screen("forgot_password", "Recuperar contraseña") // Recuperar contraseña

    // --- Rutas principales de la app ---
    object Home : Screen("home", "Inicio")                    // Pantalla principal tras login
    object Timer : Screen("timer", "Temporizador")            // Temporizador de descanso o entreno
    object Settings : Screen("settings", "Ajustes")           // Configuración y preferencias

    // --- Rutas relacionadas con rutinas ---
    object RegisterRoutine : Screen("register_routine", "Registrar rutina")     // Crear nueva rutina
    object RoutineList : Screen("routineList?predefined={predefined}", "Ver rutinas")
    object RoutineDetail : Screen("routine_detail/{routineId}", "Detalle rutina") {
        // Genera la ruta real al navegar pasando el ID de la rutina
        fun createRoute(routineId: String) = "routine_detail/$routineId"
    }

    object FavoriteRoutines : Screen("favoritas", "Rutinas favoritas")          // Rutinas marcadas como favoritas
    object PredefinedRoutines : Screen("predefined_routines", "Rutinas predefinidas") // Rutinas que vienen de base
}
