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
 * Este archivo contiene los componentes principales que definen la estructura base de la app GymTrack.
 *
 * Integra el tema claro/oscuro (darkMode) usando GymTrackTheme.
 * Configura el Scaffold principal, incluyendo:
 *    - un FloatingActionButton (FAB) central que se oculta en pantallas específicas.
 *    - integración del sistema de navegación a través de GymTrackNavHost.
 * Usa un Crossfade para animar el cambio de tema (transición suave al cambiar entre claro y oscuro).
 * Incluye lógica para detectar si el usuario actual es admin (según el correo Firebase) y ajustar comportamientos.
 */

@Composable
fun GymTrackApp(
    themeViewModel: ThemeViewModel,    // ViewModel que gestiona las preferencias de tema
    darkMode: Boolean                  // Estado actual del modo oscuro
) {
    val navController = rememberNavController()                          // Controlador de navegación principal
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route             // Ruta actual activa
    val currentUser = FirebaseAuth.getInstance().currentUser             // Usuario actual autenticado (Firebase)
    val isAdmin = currentUser?.email == "admin@gymtrack.com"            // ¿Es usuario administrador?

    // Lista de rutas donde NO queremos mostrar el FAB (menú flotante central)
    val noMenuScreens = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route
    )

    // Determina si el FAB debe ocultarse en la pantalla actual,
    // considerando variantes con parámetros (como "?change=true").
    val hideFab = noMenuScreens.any { route ->
        currentRoute == route || currentRoute?.startsWith("$route?") == true || currentRoute?.startsWith("$route/") == true
    }

    // Debug log para ver en consola la ruta actual, si se oculta el FAB y si el usuario es admin
    Log.d("NAV_DEBUG", "Ruta actual: $currentRoute | Ocultar FAB: $hideFab | Admin: $isAdmin")

    // Aplica transición suave al cambiar entre tema claro y oscuro
    Crossfade(targetState = darkMode, label = "theme") { isDark ->
        GymTrackTheme(darkTheme = isDark) {
            Scaffold(
                topBar = {},                                   // (opcional) barra superior personalizada
                bottomBar = {},                                // (opcional) barra inferior personalizada
                floatingActionButton = {
                    if (!hideFab) {
                        ShareMenuSample(navController)         // FAB flotante que abre el menú compartido
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,  // Posición centrada para el FAB
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                GymTrackNavHost(navController, innerPadding, themeViewModel) // Sistema de navegación principal
            }
        }
    }
}


