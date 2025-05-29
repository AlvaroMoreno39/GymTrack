package com.alvaromoreno.gymtrack.ui.screens.SettingsScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alvaromoreno.gymtrack.navigation.Screen
import com.alvaromoreno.gymtrack.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.alvaromoreno.gymtrack.ui.components.AnimatedAccessButton
import com.alvaromoreno.gymtrack.ui.components.AnimatedEntrance
import com.alvaromoreno.gymtrack.ui.components.FancySnackbarHost
import com.alvaromoreno.gymtrack.ui.components.ScreenHeader
import com.alvaromoreno.gymtrack.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch
import com.alvaromoreno.gymtrack.R


/**
 * SettingsScreen.kt
 *
 * Pantalla de ajustes de la app GymTrack.
 * Permite al usuario:
 * - Visualizar su cuenta (email),
 * - Cambiar su contraseña (redirige a la pantalla ForgotPassword en modo cambio),
 * - Cerrar sesión (deslogándose de FirebaseAuth y navegando al login),
 * - Activar/desactivar el modo oscuro mediante un switch conectado a ThemeViewModel,
 * - (Opcional) Probar notificaciones locales usando WorkManager.
 *
 * El diseño mantiene la coherencia visual con el resto de la app usando componentes animados,
 * colores adaptativos y estructura minimalista.
 * La pantalla está construida en Jetpack Compose y sigue el patrón MVVM.
 */

@Composable
fun SettingsScreen(
    navController: NavHostController,           // Controlador de navegación para volver/cambiar de pantalla
    authViewModel: AuthViewModel,               // ViewModel de autenticación para logout, etc.
    themeViewModel: ThemeViewModel              // ViewModel de tema para cambiar modo claro/oscuro
) {
    // Estado para mostrar Snackbars (mensajes de aviso/feedback)
    val snackbarHostState = remember { SnackbarHostState() }
    // Scope para lanzar corrutinas (necesario para mostrar Snackbars)
    val coroutineScope = rememberCoroutineScope()
    // Usuario actualmente autenticado (si no hay, muestra 'desconocido')
    val user = FirebaseAuth.getInstance().currentUser

    // Estructura principal: Scaffold con barra superior personalizada
    Scaffold(
        snackbarHost = { FancySnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background) // Fondo adaptable a tema claro/oscuro
        ) {

            // Cabecera visual animada y consistente con resto de la app
            ScreenHeader(
                image = R.drawable.settings,
                title = "Ajustes",
                subtitle = "Personaliza tu experiencia"
            )

            // Contenido principal animado (se desliza al aparecer)
            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp) // Espaciado vertical estándar
                ) {
                    // --- Sección de CUENTA ---
                    Text("Cuenta", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                    // Muestra el email del usuario autenticado
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.email ?: "Usuario desconocido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    // Botón para cambiar contraseña (navega a ForgotPasswordScreen en modo cambio)
                    AnimatedAccessButton(
                        buttonText = "Cambiar contraseña",
                        onClick = {
                            navController.navigate(Screen.ForgotPassword.route + "?change=true")
                        },
                        color = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    // Botón para cerrar sesión (llama a logout en AuthViewModel y navega a login)
                    AnimatedAccessButton(
                        buttonText = "Cerrar sesión",
                        onClick = {
                            authViewModel.logout() // Cierra la sesión en FirebaseAuth
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Sesión cerrada 🔒")
                            }
                            // Navega a login eliminando el historial (para no volver atrás tras logout)
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        color = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    // --- Sección de PREFERENCIAS (tema) ---
                    Text("Preferencias", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                    // Switch para activar/desactivar modo oscuro (con reactividad y animación)
                    val darkMode by themeViewModel.darkMode.collectAsState()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Modo oscuro", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                        SmoothSwitch(
                            checked = darkMode,
                            onCheckedChange = { enabled -> themeViewModel.toggleDarkMode(enabled) }
                        )
                    }

                    // --- (OPCIONAL) Botón para probar notificaciones ---
                    // Descomentar si se quiere testear notificaciones locales con WorkManager
//                    val context = LocalContext.current
//                    AnimatedAccessButton(
//                        buttonText = "Generar notificacion (solo prueba)",
//                        onClick = {
//                            val request = OneTimeWorkRequestBuilder<NotificationWorker>().build()
//                            WorkManager.getInstance(context).enqueue(request)
//                        },
//                        color = MaterialTheme.colorScheme.onBackground,
//                        contentColor = MaterialTheme.colorScheme.background,
//                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(56.dp)
//                    )

                }
            }
        }
    }
}

