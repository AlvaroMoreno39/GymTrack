package com.example.gymtrack.navigation

import android.util.Log

import androidx.compose.animation.Crossfade
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymtrack.ui.components.ShareMenuSample
import com.example.gymtrack.ui.theme.GymTrackTheme
import com.example.gymtrack.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * GymTrackApp.kt
 *
 * Este archivo contiene los componentes de UI principales para la estructura base de la app GymTrack.
 * Aquí se gestiona la navegación principal, la integración del tema claro/oscuro, el menú flotante personalizado
 * y varios componentes reutilizables de UI como botones animados, Snackbars y cabeceras animadas.
 *
 * El objetivo es ofrecer una estructura moderna, flexible y coherente para toda la app usando Jetpack Compose.
 */
@Composable
fun GymTrackApp(
    themeViewModel: ThemeViewModel,
    darkMode: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isAdmin = currentUser?.email == "admin@gymtrack.com"

    // Lista de rutas en las que NO queremos mostrar el botón flotante del menú (FAB).
    // Esto incluye las pantallas de Login, Registro y Recuperar contraseña.
    val noMenuScreens = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route
    )

    // Comprobamos si la ruta actual está en la lista de rutas "prohibidas" o si comienza por alguna de ellas,
    // incluyendo posibles variantes con parámetros (por ejemplo, "forgot_password?change=true").
    // De esta forma, nos aseguramos de ocultar el FAB en todas sus variantes.
    val hideFab = noMenuScreens.any { route ->
        currentRoute == route || // Ruta exacta (ej: "login")
                currentRoute?.startsWith("$route?") == true || // Variante con parámetros (ej: "forgot_password?change=true")
                currentRoute?.startsWith("$route/") == true    // Variante con subrutas (si las hubiera)
    }


    // Log para pruebas específicamente para ver donde la ruta actual, si se oculta el FAB y si es admin
    Log.d("NAV_DEBUG", "Ruta actual: $currentRoute | Ocultar FAB: $hideFab | Admin: $isAdmin")

    Crossfade(targetState = darkMode, label = "theme") { isDark ->
        GymTrackTheme(darkTheme = isDark) {
            Scaffold(
                topBar = {},
                bottomBar = {},
                // SOLO mostramos el menú flotante si NO estamos en una pantalla prohibida
                floatingActionButton = {
                    if (!hideFab) {
                        ShareMenuSample(navController)
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                GymTrackNavHost(navController, innerPadding, themeViewModel)
            }
        }
    }
}

