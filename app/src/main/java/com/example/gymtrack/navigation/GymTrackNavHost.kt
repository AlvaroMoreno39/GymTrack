package com.example.gymtrack.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.gymtrack.ui.screens.FavoriteRoutinesScreen
import com.example.gymtrack.ui.screens.ForgotPasswordScreen
import com.example.gymtrack.ui.screens.HomeScreen
import com.example.gymtrack.ui.screens.LoginScreen
import com.example.gymtrack.ui.screens.RegisterRoutineScreen
import com.example.gymtrack.ui.screens.RegisterScreen
import com.example.gymtrack.ui.screens.RoutineDetailScreen.RoutineDetailScreen
import com.example.gymtrack.ui.screens.RoutineListScreen
import com.example.gymtrack.ui.screens.SettingsScreen.SettingsScreen
import com.example.gymtrack.ui.screens.TimerScreen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.viewmodel.ThemeViewModel

/**
 * GymTrackNavHost.kt
 *
 * Este archivo define el sistema de navegación central de la app GymTrack.
 * Gestiona qué pantalla se muestra en función de la ruta actual usando Jetpack Navigation para Compose.
 * Aquí se conectan los diferentes ViewModels con cada pantalla y se configuran los argumentos necesarios para la navegación.
 */

@Composable
fun GymTrackNavHost(
    navController: NavHostController,        // Controlador de navegación principal
    paddingValues: PaddingValues,            // Relleno aplicado por Scaffold para respetar barras y menús
    themeViewModel: ThemeViewModel           // ViewModel para tema claro/oscuro y settings generales
) {
    // Instancia principal de AuthViewModel para mantener el estado global de autenticación
    val authViewModel: AuthViewModel = viewModel()
    // Instancia principal de RoutineViewModel para manejar rutinas del usuario
    val routineViewModel: RoutineViewModel = viewModel()

    // Definición del grafo de navegación usando NavHost
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,      // Pantalla inicial al abrir la app (Login)
        modifier = Modifier.padding(paddingValues)  // Aplica el relleno global a todas las pantallas
    ) {
        // ---- Pantalla de Login ----
        composable(Screen.Login.route) {
            LoginScreen(
                navController,
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        // ---- Pantalla de Favoritos ----
        composable(Screen.FavoriteRoutines.route) {
            FavoriteRoutinesScreen(
                viewModel = viewModel(),      // Usa un ViewModel local para esta pantalla
                navController = navController
            )
        }

        // ---- Pantalla de recuperación de contraseña, con argumento opcional "change" ----
        composable(
            route = Screen.ForgotPassword.route + "?change={change}",
            arguments = listOf(navArgument("change") {
                defaultValue = "false"
            })
        ) { backStackEntry ->
            val isChange = backStackEntry.arguments?.getString("change") == "true"
            ForgotPasswordScreen(navController, authViewModel, isChangePassword = isChange)
        }

        // ---- Pantalla de Registro de Usuario ----
        composable(Screen.Register.route) {
            RegisterScreen(
                navController,
                authViewModel = authViewModel
            )
        }

        // ---- Pantalla para Registrar Nueva Rutina ----
        composable(Screen.RegisterRoutine.route) {
            val routineViewModel: RoutineViewModel = viewModel()
            RegisterRoutineScreen(viewModel = routineViewModel)
        }

        // ---- Pantalla Principal (Home) ----
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(
            route = Screen.RoutineList.route,
            arguments = listOf(
                navArgument("predefined") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val isPredefined = backStackEntry.arguments?.getBoolean("predefined") ?: false
            RoutineListScreen(
                navController = navController,
                viewModel = routineViewModel,
                showPredefined = isPredefined
            )
        }

        // ---- Detalle de una Rutina, recibe un argumento obligatorio "routineId" ----
        composable(
            route = Screen.RoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            RoutineDetailScreen(
                routineId = routineId,
                viewModel = routineViewModel,     // Usa el ViewModel global para mantener el estado compartido
                navController = navController
            )
        }

        // ---- Pantalla de Temporizador ----
        composable(Screen.Timer.route) {
            TimerScreen(navController)
        }

        // ---- Pantalla de Ajustes ----
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController,
                authViewModel = authViewModel,
                themeViewModel = themeViewModel
            )
        }

        composable("predefined_routine_detail") {
            val routine = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<RoutineData>("routine_arg")

            RoutineDetailScreen(
                navController = navController,
                viewModel = routineViewModel,
                routineArg = routine,
                isPredefined = true
            )
        }

    }
}
