package com.example.gymtrack.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.SmoothSwitch
import com.example.gymtrack.notification.NotificationWorker
import com.example.gymtrack.viewmodel.ThemeViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    themeViewModel: ThemeViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        snackbarHost = { FancySnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background) // ← Fondo blanco
        ) {
            // 🔝 Cabecera animada
            AnimatedEntrance {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Ajustes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            // 🧩 Contenido principal
            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp) // 🔽 Espaciado reducido
                ) {
                    // 🔐 Cuenta
                    Text("Cuenta", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

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

                    // 🖤 Botón cambiar contraseña (negro)
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

                    // ❤️ Botón cerrar sesión (rojo)
                    AnimatedAccessButton(
                        buttonText = "Cerrar sesión",
                        onClick = {
                            authViewModel.logout()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Sesión cerrada")
                            }
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



                    Text("Preferencias", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

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

                    val context = LocalContext.current

                    // Probar notificaciones
//                    Button(onClick = {
//                        val request = OneTimeWorkRequestBuilder<NotificationWorker>().build()
//                        WorkManager.getInstance(context).enqueue(request)
//                    }) {
//                        Text("Probar notificación")
//                    }

                }
            }
        }
    }
}

