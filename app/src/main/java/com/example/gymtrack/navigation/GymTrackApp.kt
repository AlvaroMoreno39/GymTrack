package com.example.gymtrack.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTrackApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = Screen.bottomBarScreens.find { it.route == currentRoute }

    val showBars = currentRoute !in listOf(Screen.Login.route, Screen.Register.route)

    Scaffold(
        topBar = {
            if (showBars) {
                TopAppBar(
                    title = { Text(currentScreen?.title ?: "") }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                NavigationBar {
                    Screen.bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = when (screen.route) {
                                        Screen.Home.route -> Icons.Default.Home
                                        Screen.Progress.route -> Icons.Default.BarChart
                                        Screen.Timer.route -> Icons.Default.Timer
                                        Screen.Settings.route -> Icons.Default.Settings
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) {
        GymTrackNavHost(navController, it)
    }
}