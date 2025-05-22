package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gymtrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray

/*
ForgotPasswordScreen.kt

Pantalla de recuperación de contraseña en la app GymTrack.
Permite al usuario solicitar un email de restablecimiento de contraseña usando Firebase Auth.
Incluye validación del campo email, feedback visual, mensajes de error y navegación de retorno a login.
La UI usa una cabecera consistente y animaciones suaves para mantener coherencia con el resto de la app.
*/

@SuppressLint("UnrememberedMutableState")
@Composable
fun ForgotPasswordScreen(
    navController: NavController,        // Navegador para volver atrás o navegar tras recuperación
    authViewModel: AuthViewModel,        // ViewModel de autenticación, gestiona la lógica de recuperación
    isChangePassword: Boolean = false    // Si es true, cambia el título para mostrar 'restablecer' en vez de 'recuperar'
) {
    // Limpia errores previos al montar la pantalla
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    val context = LocalContext.current
    val error by authViewModel.error.collectAsState() // Observa errores emitidos por AuthViewModel

    var email by remember { mutableStateOf("") }         // Estado del campo email
    var showEmailError by remember { mutableStateOf(false) } // Controla la visibilidad del error de email

    // Valida el formato de email cada vez que cambia
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val snackbarHostState = remember { SnackbarHostState() } // Estado del snackbar (avisos)
    val scope = rememberCoroutineScope()                     // Scope para mostrar snackbars

    // Scaffold aplica el sistema de mensajes y el layout general
    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera visual animada (imagen, título y subtítulo)
            ScreenHeader(
                image = R.drawable.forgot_password,
                title = if (isChangePassword) "Restablece tu" else "Recupera tu",
                subtitle = if (isChangePassword) "nueva contraseña" else "contraseña olvidada"
            )

            // Formulario de recuperación de contraseña con animación de entrada
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 600))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Input de email con validación visual
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            showEmailError = false
                        },
                        label = { Text("Correo electrónico") },
                        isError = showEmailError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showEmailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = if (showEmailError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    // Mensaje de error visual bajo el campo email
                    if (showEmailError) {
                        Text(
                            text = "Introduce un correo electrónico válido",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(top = 4.dp, start = 4.dp)
                                .align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón animado para enviar el correo de recuperación
                    AnimatedAccessButton(buttonText = "Enviar correo", modifier = Modifier.fillMaxWidth()) {
                        showEmailError = email.isBlank() || !isValidEmail

                        if (!showEmailError) {
                            authViewModel.resetPassword(email)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Introduce un correo electrónico válido 📧")
                            }
                        }
                    }

                    // Si NO es cambio de contraseña, muestra el link de retorno a login
                    if (!isChangePassword) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("¿Recuerdas tu contraseña? ", color = LightGray, fontSize = 15.sp)
                            Text(
                                text = "Inicia sesión",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Muestra un Snackbar según el resultado (éxito o error) recibido desde el ViewModel
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                val esCorrecto = it.contains("enviado", ignoreCase = true)
                snackbarHostState.showSnackbar(
                    if (esCorrecto) "Correo de recuperación enviado con éxito ✅"
                    else it
                )
            }
        }
    }
}
