package com.alvaromoreno.gymtrack.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.alvaromoreno.gymtrack.ui.screens.FavoriteRoutinesScreen
import com.alvaromoreno.gymtrack.ui.screens.ForgotPasswordScreen
import com.alvaromoreno.gymtrack.ui.screens.HomeScreen.HomeScreen
import com.alvaromoreno.gymtrack.ui.screens.LoginScreen
import com.alvaromoreno.gymtrack.ui.screens.RegisterRoutineScreen
import com.alvaromoreno.gymtrack.ui.screens.RegisterScreen
import com.alvaromoreno.gymtrack.ui.screens.RoutineDetailScreen.RoutineDetailScreen
import com.alvaromoreno.gymtrack.ui.screens.RoutineListScreen.RoutineListScreen
import com.alvaromoreno.gymtrack.ui.screens.SettingsScreen.SettingsScreen
import com.alvaromoreno.gymtrack.ui.screens.TimerScreen.TimerScreen
import com.alvaromoreno.gymtrack.viewmodel.AuthViewModel
import com.alvaromoreno.gymtrack.viewmodel.RoutineData
import com.alvaromoreno.gymtrack.viewmodel.RoutineViewModel
import com.alvaromoreno.gymtrack.viewmodel.ThemeViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.alvaromoreno.gymtrack.R

/**
 * GymTrackNavHost.kt
 *
 * Este archivo define el sistema de navegación central de la app GymTrack usando Jetpack Navigation Compose.
 *
 * Gestiona todas las rutas y pantallas de la app desde un único NavHost.
 * Conecta cada pantalla con su ViewModel correspondiente.
 * Define transiciones animadas personalizadas para hacer la navegación más fluida.
 * Maneja argumentos dinámicos (como IDs de rutina o banderas booleanas) para pasar datos entre pantallas.
 */

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymTrackNavHost(
    navController: NavHostController,       // Controlador de navegación principal
    paddingValues: PaddingValues,           // Relleno para adaptar a barras superiores/inferiores
    themeViewModel: ThemeViewModel          // ViewModel que gestiona el tema oscuro/claro
) {
    val authViewModel: AuthViewModel = viewModel()          // ViewModel global para autenticación
    val routineViewModel: RoutineViewModel = viewModel()    // ViewModel global para rutinas

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Login.route,              // Pantalla inicial al arrancar la app
        modifier = Modifier.padding(paddingValues)
    ) {
        // -------------------------
        // BLOQUE DE PANTALLAS
        // -------------------------

        // --- Login ---
        composable(
            route = Screen.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        // --- Register ---
        composable(
            route = Screen.Register.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- Forgot Password (con parámetro opcional de modo cambio contraseña) ---
        composable(
            route = Screen.ForgotPassword.route + "?change={change}",
            arguments = listOf(navArgument("change") { defaultValue = "false" }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val isChange = backStackEntry.arguments?.getString("change") == "true"
            ForgotPasswordScreen(navController, authViewModel, isChangePassword = isChange)
        }

        // --- Home ---
        composable(
            route = Screen.Home.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(tween(400)) },
            exitTransition = { slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -it }, animationSpec = tween(400)) + fadeIn(tween(400)) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }

        // --- Register Routine ---
        composable(
            route = Screen.RegisterRoutine.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            RegisterRoutineScreen(viewModel = routineViewModel)
        }

        // --- Favorite Routines ---
        composable(
            route = Screen.FavoriteRoutines.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            FavoriteRoutinesScreen(viewModel = routineViewModel, navController = navController)
        }

        // --- Routine List (con parámetro booleano) ---
        composable(
            route = Screen.RoutineList.route,
            arguments = listOf(navArgument("predefined") { type = NavType.BoolType; defaultValue = false }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val isPredefined = backStackEntry.arguments?.getBoolean("predefined") ?: false
            RoutineListScreen(navController = navController, viewModel = routineViewModel, showPredefined = isPredefined)
        }

        // --- Routine Detail ---
        composable(
            route = Screen.RoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            RoutineDetailScreen(routineId = routineId, viewModel = routineViewModel)
        }

        // --- Timer ---
        composable(
            route = Screen.Timer.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)) + fadeIn(tween(400)) },
            exitTransition = { slideOutVertically(targetOffsetY = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -it }, animationSpec = tween(400)) + fadeIn(tween(400)) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            TimerScreen(navController)
        }

        // --- Settings ---
        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            SettingsScreen(navController = navController, authViewModel = authViewModel, themeViewModel = themeViewModel)
        }

        // --- Predefined Routine Detail (opcional) ---
        composable(
            route = "predefined_routine_detail",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(350)) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 2 }, animationSpec = tween(300)) + fadeOut(tween(300)) }
        ) {
            val routine = navController.previousBackStackEntry?.savedStateHandle?.get<RoutineData>("routine_arg")
            RoutineDetailScreen(viewModel = routineViewModel, routineArg = routine, isPredefined = true)
        }
    }
}

