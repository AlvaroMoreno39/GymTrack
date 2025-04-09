package com.example.gymtrack.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTrackApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute !in listOf(
        Screen.Login.route,
        Screen.Register.route
    )

    val showTopBar = currentRoute !in listOf(
        Screen.Login.route,
        Screen.Register.route
    ) && currentRoute !in Screen.bottomBarScreens.map { it.route }

    val currentScreen = Screen.bottomBarScreens.find { it.route == currentRoute }

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            currentScreen?.title ?: "",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar && currentRoute in Screen.bottomBarScreens.map { it.route }) {
                NavigationBar {
                    Screen.bottomBarScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
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
                                        Screen.Timer.route -> Icons.Default.Timer
                                        Screen.GeneralProgress.route -> Icons.Default.BarChart
                                        Screen.Settings.route -> Icons.Default.Settings
                                        else -> Icons.Default.Star
                                    },
                                    contentDescription = screen.title,
                                    tint = if (isSelected) Color.Black else Color.Gray
                                )
                            },
                            label = { Text(screen.title) },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        GymTrackNavHost(navController, innerPadding)
    }
}
