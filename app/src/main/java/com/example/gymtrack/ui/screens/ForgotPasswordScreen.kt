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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray

@SuppressLint("UnrememberedMutableState")
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    isChangePassword: Boolean = false
) {
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    val context = LocalContext.current
    val error by authViewModel.error.collectAsState()

    var email by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }

    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            ScreenHeader(
                image = R.drawable.forgotpasswordphoto,
                title = if (isChangePassword) "Restablece tu" else "Recupera tu",
                subtitle = if (isChangePassword) "nueva contraseña" else "contraseña olvidada"
            )

            // FORMULARIO animado
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

                    AnimatedAccessButton(buttonText = "Enviar correo", modifier = Modifier.fillMaxWidth()) {
                        showEmailError = email.isBlank() || !isValidEmail

                        if (!showEmailError) {
                            authViewModel.resetPassword(email)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Introduce un correo electrónico válido")
                            }
                        }
                    }

                    // 👇 Solo si es pantalla de recuperación, muestra esta parte
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

    // Snackbar según resultado
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

