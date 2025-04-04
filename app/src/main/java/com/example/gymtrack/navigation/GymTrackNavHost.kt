package com.example.gymtrack.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymtrack.ui.screens.*
import com.example.gymtrack.viewmodel.AuthViewModel

@Composable
fun GymTrackNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Progress.route) {
            ProgressScreen(navController)
        }

        composable(Screen.Timer.route) {
            TimerScreen(navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}
