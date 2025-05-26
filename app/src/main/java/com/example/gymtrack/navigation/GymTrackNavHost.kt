package com.example.gymtrack.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.example.gymtrack.ui.screens.FavoriteRoutinesScreen
import com.example.gymtrack.ui.screens.ForgotPasswordScreen
import com.example.gymtrack.ui.screens.HomeScreen
import com.example.gymtrack.ui.screens.LoginScreen
import com.example.gymtrack.ui.screens.RegisterRoutineScreen
import com.example.gymtrack.ui.screens.RegisterScreen
import com.example.gymtrack.ui.screens.RoutineDetailScreen.RoutineDetailScreen
import com.example.gymtrack.ui.screens.RoutineListScreen.RoutineListScreen
import com.example.gymtrack.ui.screens.SettingsScreen.SettingsScreen
import com.example.gymtrack.ui.screens.TimerScreen.TimerScreen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.viewmodel.ThemeViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost

/**
 * GymTrackNavHost.kt
 *
 * Este archivo define el sistema de navegación central de la app GymTrack.
 * Gestiona qué pantalla se muestra en función de la ruta actual usando Jetpack Navigation para Compose.
 * Aquí se conectan los diferentes ViewModels con cada pantalla y se configuran los argumentos necesarios para la navegación.
 */


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GymTrackNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    themeViewModel: ThemeViewModel
) {
    val authViewModel: AuthViewModel = viewModel()
    val routineViewModel: RoutineViewModel = viewModel()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- Login ---
        composable(
            route = Screen.Login.route,
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
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
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // --- Forgot Password ---
        composable(
            route = Screen.ForgotPassword.route + "?change={change}",
            arguments = listOf(navArgument("change") { defaultValue = "false" }),
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val isChange = backStackEntry.arguments?.getString("change") == "true"
            ForgotPasswordScreen(navController, authViewModel, isChangePassword = isChange)
        }

        // --- Home ---
        composable(
            route = Screen.Home.route,
            enterTransition = { slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400)) },
            exitTransition = { slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400)) },
            popExitTransition = { slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }

        // --- Register Routine ---
        composable(
            route = Screen.RegisterRoutine.route,
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            RegisterRoutineScreen(viewModel = routineViewModel)
        }

        // --- Favorite Routines ---
        composable(
            route = Screen.FavoriteRoutines.route,
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            FavoriteRoutinesScreen(
                viewModel = routineViewModel,
                navController = navController
            )
        }

        // --- Routine List (mis rutinas y predefinidas con parámetro) ---
        composable(
            route = Screen.RoutineList.route,
            arguments = listOf(
                navArgument("predefined") { type = NavType.BoolType; defaultValue = false }
            ),
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val isPredefined = backStackEntry.arguments?.getBoolean("predefined") ?: false
            RoutineListScreen(
                navController = navController,
                viewModel = routineViewModel,
                showPredefined = isPredefined
            )
        }

        // --- Detalle de Rutina ---
        composable(
            route = Screen.RoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            RoutineDetailScreen(
                routineId = routineId,
                viewModel = routineViewModel,
                navController = navController
            )
        }

        // --- Timer ---
        composable(
            route = Screen.Timer.route,
            enterTransition = { slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400)) },
            exitTransition = { slideOutVertically(
                targetOffsetY = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(400)) },
            popExitTransition = { slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            TimerScreen(navController)
        }

        // --- Settings ---
        composable(
            route = Screen.Settings.route,
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
            SettingsScreen(
                navController = navController,
                authViewModel = authViewModel,
                themeViewModel = themeViewModel
            )
        }

        // --- Detalle rutina predefinida (si lo usas como pantalla extra) ---
        composable(
            route = "predefined_routine_detail",
            enterTransition = { slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            exitTransition = { slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(350, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(350)) },
            popExitTransition = { slideOutHorizontally(
                targetOffsetX = { it / 2 },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(300)) }
        ) {
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
