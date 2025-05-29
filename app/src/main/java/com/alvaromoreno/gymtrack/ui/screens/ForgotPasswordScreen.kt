package com.alvaromoreno.gymtrack.ui.screens

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
import com.alvaromoreno.gymtrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.alvaromoreno.gymtrack.ui.components.AnimatedAccessButton
import com.alvaromoreno.gymtrack.ui.components.FancySnackbarHost
import com.alvaromoreno.gymtrack.ui.components.ScreenHeader
import com.alvaromoreno.gymtrack.ui.theme.LightGray
import com.alvaromoreno.gymtrack.R

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

    // Observa errores emitidos por AuthViewModel
    val error by authViewModel.error.collectAsState()

    // Estado del campo email
    var email by remember { mutableStateOf("") }

    // Controla si mostrar error visual en el campo email
    var showEmailError by remember { mutableStateOf(false) }

    // Valida el formato de email usando patrón estándar de Android
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Estado para mostrar Snackbars (mensajes flotantes)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Scaffold aplica la estructura general de la pantalla, incluyendo el sistema de mensajes
    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera visual animada con imagen, título y subtítulo
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

                    // Mensaje de error bajo el campo email
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

                    // Si NO es cambio de contraseña, muestra link de retorno a login
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

    // Escucha los errores o mensajes exitosos y muestra Snackbar correspondiente
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

