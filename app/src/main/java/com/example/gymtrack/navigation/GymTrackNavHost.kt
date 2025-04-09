package com.example.gymtrack.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymtrack.ui.screens.ExerciseProgressDashboardScreen
import com.example.gymtrack.ui.screens.ForgotPasswordScreen
import com.example.gymtrack.ui.screens.GeneralProgressScreen
import com.example.gymtrack.ui.screens.HomeScreen
import com.example.gymtrack.ui.screens.LoginScreen
import com.example.gymtrack.ui.screens.PredefinedRoutinesScreen
import com.example.gymtrack.ui.screens.RegisterRoutineScreen
import com.example.gymtrack.ui.screens.RegisterScreen
import com.example.gymtrack.ui.screens.SettingsScreen
import com.example.gymtrack.ui.screens.TimerScreen
import com.example.gymtrack.ui.screens.ViewRoutinesScreen
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

        composable(Screen.PredefinedRoutines.route) {
            val viewModel: PredefinedRoutinesViewModel = viewModel()
            PredefinedRoutinesScreen(
                viewModel = viewModel,
                routineViewModel = routineViewModel
            )
        }

        composable(Screen.ExerciseDashboard.route) {
            ExerciseProgressDashboardScreen(
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController, authViewModel)
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
            HomeScreen(navController)
        }

        composable(Screen.GeneralProgress.route) {
            GeneralProgressScreen(
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
        }

        composable(Screen.ViewRoutinesScreen.route) {
            ViewRoutinesScreen(viewModel = routineViewModel)
        }

        composable(Screen.Timer.route) {
            TimerScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(authViewModel, navController)
        }

    }
}
