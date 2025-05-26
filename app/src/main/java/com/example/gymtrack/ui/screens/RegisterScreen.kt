package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.AnimatedEntrance
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray
import com.example.gymtrack.ui.theme.ValidGreen
import com.example.gymtrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/*
RegisterScreen.kt

Pantalla de registro de usuario para la app GymTrack.
Permite a cualquier usuario crear una cuenta mediante correo electrónico y contraseña segura (integración con Firebase Auth).
Incluye:
- Validación visual de email y contraseña (requisitos mínimos, confirmación, feedback en tiempo real).
- Mensajes de error y confirmación mediante Snackbar.
- Enlace directo a la pantalla de login si ya tienes cuenta.
- Diseño moderno y coherente con el resto de la app.
- Toda la lógica de autenticación delegada al AuthViewModel.

El objetivo es ofrecer una experiencia de registro clara, cuidada y segura, ayudando al usuario a no cometer errores habituales.
*/

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    // Al abrir la pantalla, limpia cualquier error anterior
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    // Obtiene contexto para uso interno (por si quieres mostrar Toast o recursos)
    val context = LocalContext.current

    // Estado reactivo para mostrar errores de autenticación del ViewModel
    val error by authViewModel.error.collectAsState()

    // Estado reactivo para saber si ya hay usuario logueado (tras registrarse)
    val user by authViewModel.user.collectAsState()

    // Estados locales para los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados para controlar visibilidad de contraseñas (iconos)
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Estados de error visual para cada campo
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordEmptyError by remember { mutableStateOf(false) }
    var showConfirmEmptyError by remember { mutableStateOf(false) }
    var showConfirmPasswordError by remember { mutableStateOf(false) }

    // Snackbar personalizado para mostrar errores/éxitos
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Validaciones en tiempo real para la contraseña
    val passwordLengthValid by derivedStateOf { password.length >= 6 }                  // Mínimo 6 caracteres
    val passwordDigitValid by derivedStateOf { password.any { it.isDigit() } }         // Al menos un número
    val passwordSpecialCharValid by derivedStateOf { password.any { !it.isLetterOrDigit() } } // Al menos un símbolo
    val passwordValid by derivedStateOf {
        passwordLengthValid && passwordDigitValid && passwordSpecialCharValid
    }

    // Validación de email usando patrón de Android
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Comprobación de coincidencia de contraseñas
    val passwordsMatch by derivedStateOf { confirmPassword == password }

    // Estructura visual principal: Scaffold con Snackbar y fondo consistente
    Scaffold(
        snackbarHost = {
            FancySnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Fondo blanco/oscuro según tema
        ) {

            // Cabecera visual animada
            ScreenHeader(
                image = R.drawable.register,
                title = "Empieza tu camino",
                subtitle = "Crea tu cuenta y comienza"
            )

            // Formulario animado de entrada
            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo de email con validación visual
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
                    // Muestra mensaje de error si el email es inválido
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
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña con icono para mostrar/ocultar y validación visual
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            showPasswordEmptyError = false
                        },
                        label = { Text("Contraseña") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description =
                                if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        isError = showPasswordEmptyError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showPasswordEmptyError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = if (showPasswordEmptyError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    // Requisitos de la contraseña (colores verdes al cumplirlos)
                    Column(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            "• Mínimo 6 caracteres",
                            fontSize = 12.sp,
                            color = if (passwordLengthValid) ValidGreen else LightGray
                        )
                        Text(
                            "• Al menos un número",
                            fontSize = 12.sp,
                            color = if (passwordDigitValid) ValidGreen else LightGray
                        )
                        Text(
                            "• Al menos un símbolo especial",
                            fontSize = 12.sp,
                            color = if (passwordSpecialCharValid) ValidGreen else LightGray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo para confirmar contraseña con icono y validación visual
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            showConfirmPasswordError = false
                            showConfirmEmptyError = false
                        },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description =
                                if (confirmPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        isError = showConfirmEmptyError || showConfirmPasswordError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showConfirmEmptyError || showConfirmPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = if (showConfirmEmptyError || showConfirmPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    // Mensaje de error si no coinciden las contraseñas
                    if (showConfirmPasswordError) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(top = 4.dp, start = 4.dp)
                                .align(Alignment.Start)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de registro con validación previa de todos los campos
                    AnimatedAccessButton(buttonText = "Registrarse", modifier = Modifier.fillMaxWidth()) {
                        val emailError = email.isBlank() || !isValidEmail
                        val passwordEmpty = password.isBlank()
                        val confirmEmpty = confirmPassword.isBlank()
                        val confirmMismatch = confirmPassword != password

                        showEmailError = emailError
                        showPasswordEmptyError = passwordEmpty
                        showConfirmEmptyError = confirmEmpty
                        showConfirmPasswordError = !confirmEmpty && confirmMismatch

                        if (!emailError && !passwordEmpty && !confirmEmpty && passwordValid && !confirmMismatch) {
                            // Llama al ViewModel para registrar
                            authViewModel.register(email, password)
                        } else if (!passwordValid && !passwordEmpty) {
                            scope.launch {
                                snackbarHostState.showSnackbar("La contraseña no cumple los requisitos ⚠️")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor, completa todos los campos correctamente ⚠️")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enlace visual a la pantalla de login si ya tienes cuenta
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("¿Ya tienes una cuenta? ", color = LightGray, fontSize = 15.sp)
                        Text(
                            text = "Inicia sesión",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Login.route)
                            }
                        )
                    }
                }
            }
        }
    }

    // Muestra cualquier error de registro por Snackbar en tiempo real
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    // Muestra confirmación de registro cuando el usuario se crea con éxito
    LaunchedEffect(user) {
        user?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Registro completado con éxito ✅")
            }
        }
    }
}

