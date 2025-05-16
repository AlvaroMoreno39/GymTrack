package com.example.gymtrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.gymtrack.R
import com.example.gymtrack.navigation.FancySnackbarHost
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
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
                .background(Color.White) // ← Fondo blanco
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
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Ajustes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.email ?: "Usuario desconocido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                    }

                    // 🖤 Botón cambiar contraseña (negro)
                    AnimatedAccessButton(
                        buttonText = "Cambiar contraseña",
                        onClick = {
                            navController.navigate(Screen.ForgotPassword.route + "?change=true")
                        },
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        borderColor = Color.Black,
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
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        borderColor = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    // 📊 Resumen
                    Text("Resumen", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("Rutinas completadas: 12", fontWeight = FontWeight.Medium)
                    Text("Última rutina: Piernas explosivas", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

