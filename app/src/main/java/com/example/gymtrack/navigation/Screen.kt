package com.example.gymtrack.navigation

/**
 * Screen.kt
 *
 * Define una sealed class llamada Screen que centraliza todas las rutas de navegación de la app GymTrack.
 *
 * Ventajas:
 *  Proporciona rutas tipadas (no usar strings sueltos).
 *  Facilita construir rutas dinámicas (por ejemplo, pasando IDs).
 *  Centraliza las rutas en un solo sitio, evitando errores de navegación por nombres mal escritos.
 *
 * Cada objeto representa una pantalla con:
 * - route: String → la ruta usada por NavHost.
 * - title: String → el título asociado (puede usarse para cabeceras, logs, etc.).
 */
sealed class Screen(val route: String, val title: String) {

    // --- Rutas para autenticación de usuario ---
    object Login : Screen("login", "Login")                           // Pantalla de inicio de sesión
    object Register : Screen("register", "Registro")                  // Pantalla de registro
    object ForgotPassword : Screen("forgot_password", "Recuperar contraseña") // Pantalla de recuperación

    // --- Rutas principales de la app ---
    object Home : Screen("home", "Inicio")                           // Pantalla principal
    object Timer : Screen("timer", "Temporizador")                   // Temporizador de ejercicios
    object Settings : Screen("settings", "Ajustes")                  // Pantalla de ajustes

    // --- Rutas relacionadas con rutinas ---
    object RegisterRoutine : Screen("register_routine", "Registrar rutina") // Pantalla para crear rutinas

    // Ruta al detalle de una rutina específica (requiere routineId dinámico)
    object RoutineDetail : Screen("routine_detail/{routineId}", "Detalle rutina") {
        fun createRoute(routineId: String) = "routine_detail/$routineId"
    }

    // Ruta al listado de rutinas, con parámetro opcional 'predefined'
    object RoutineList : Screen("routineList?predefined={predefined}", "Listado de rutinas") {
        fun createRoute(predefined: Boolean = false) = "routineList?predefined=$predefined"
    }

    // Ruta a las rutinas favoritas del usuario
    object FavoriteRoutines : Screen("favoritas", "Rutinas favoritas")
}
