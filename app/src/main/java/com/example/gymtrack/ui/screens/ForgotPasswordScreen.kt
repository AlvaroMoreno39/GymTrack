package com.example.gymtrack.ui.screens

/*
ForgotPasswordScreen.kt

Este archivo define la pantalla de recuperación de contraseña en la app GymTrack.
Permite a los usuarios solicitar un correo para recuperar su contraseña usando Firebase Auth.
Incluye validación del campo de email, mensajes de error y confirmación mediante Snackbar.
La interfaz visual mantiene coherencia con el resto de pantallas usando una cabecera con imagen y texto en overlay.
La navegación permite volver fácilmente a la pantalla de login si el usuario recuerda su contraseña.
*/

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gymtrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton

@SuppressLint("UnrememberedMutableState")
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Al abrir la pantalla, limpiamos cualquier error anterior del ViewModel
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    val context = LocalContext.current
    val error by authViewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }

    // Validación en tiempo real del email
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Estado del Snackbar para mostrar mensajes temporales
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Scaffold general con Snackbar incluido
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            // CABECERA VISUAL CON FOTO Y TEXTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.forgotpasswordphoto),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Capa semitransparente en la parte inferior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.65f))
                )

                // Título dentro de la capa
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text("Recupera tu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("contraseña", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Campo de texto para el email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        showEmailError = false // Oculta el error al cambiar el texto
                    },
                    label = { Text("Correo electrónico") },
                    isError = showEmailError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (showEmailError) Color.Red else Color.Black,
                        unfocusedBorderColor = if (showEmailError) Color.Red else Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                // Mensaje visual si el email no es válido
                if (showEmailError) {
                    Text(
                        text = "Introduce un correo electrónico válido",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .padding(top = 4.dp, start = 4.dp)
                            .align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN PARA ENVIAR EL CORREO DE RECUPERACIÓN
                AnimatedAccessButton(buttonText = "Enviar correo") {
                    showEmailError = email.isBlank() || !isValidEmail

                    if (!showEmailError) {
                        authViewModel.resetPassword(email)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Introduce un correo electrónico válido")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ENLACE PARA VOLVER AL LOGIN
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("¿Recuerdas tu contraseña? ", color = Color.Gray, fontSize = 15.sp)
                    Text(
                        text = "Inicia sesión",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.popBackStack() // Volver atrás en la navegación
                        }
                    )
                }
            }
        }
    }

    // Mostrar mensaje cuando Firebase envía el correo o devuelve un error
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                val esCorrecto = it.contains("enviado", ignoreCase = true)
                snackbarHostState.showSnackbar(
                    if (esCorrecto) "Correo de recuperación enviado con éxito"
                    else it
                )
            }
        }
    }
}