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
import com.example.gymtrack.ui.screens.MyRoutineScreen
import com.example.gymtrack.ui.screens.PredefinedRoutineDetailScreen
import com.example.gymtrack.ui.screens.PredefinedRoutinesScreen
import com.example.gymtrack.ui.screens.RegisterRoutineScreen
import com.example.gymtrack.ui.screens.RegisterScreen
import com.example.gymtrack.ui.screens.RoutineDetailScreen
import com.example.gymtrack.ui.screens.SettingsScreen
import com.example.gymtrack.ui.screens.TimerScreen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun GymTrackNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    val authViewModel: AuthViewModel = viewModel()
    val routineViewModel: RoutineViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController,
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(Screen.FavoriteRoutines.route) {
            FavoriteRoutinesScreen(
                viewModel = viewModel(),
                navController = navController
            )
        }

        composable(Screen.PredefinedRoutines.route) {
            val viewModel: PredefinedRoutinesViewModel = viewModel()
            PredefinedRoutinesScreen(
                viewModel = viewModel,
                navController,
                routineViewModel = routineViewModel
            )
        }

        composable(
            route = Screen.ForgotPassword.route + "?change={change}",
            arguments = listOf(navArgument("change") {
                defaultValue = "false"
            })
        ) { backStackEntry ->
            val isChange = backStackEntry.arguments?.getString("change") == "true"
            ForgotPasswordScreen(navController, authViewModel, isChangePassword = isChange)
        }


        composable(Screen.Register.route) {
            RegisterScreen(
                navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.RegisterRoutine.route) {
            val routineViewModel: RoutineViewModel = viewModel()
            RegisterRoutineScreen(viewModel = routineViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }

        composable(Screen.MyRoutines.route) {
            MyRoutineScreen(
                viewModel = viewModel(),
                navController = navController
            )
        }

        composable(
            route = Screen.RoutineDetail.route,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId") ?: ""
            RoutineDetailScreen(
                routineId = routineId,
                viewModel = routineViewModel, // usa uno compartido
                navController = navController
            )
        }

        composable(Screen.Timer.route) {
            TimerScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController, authViewModel = authViewModel)
        }

        composable("predefined_routine_detail") {
            PredefinedRoutineDetailScreen(
                navController = navController,
                viewModel = viewModel<RoutineViewModel>()
            )
        }

    }
}
