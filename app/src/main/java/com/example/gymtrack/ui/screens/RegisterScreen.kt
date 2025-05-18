package com.example.gymtrack.ui.screens

/*
RegisterScreen.kt

Este archivo contiene la pantalla de registro de la app GymTrack.
Permite a los usuarios crear una cuenta utilizando su correo electrónico y una contraseña segura.
Implementa validaciones visuales (correo válido, requisitos de contraseña, coincidencia de contraseñas) y mensajes de error mediante Snackbar.
También permite navegar a la pantalla de login si el usuario ya tiene una cuenta.
La lógica de autenticación está conectada al ViewModel `AuthViewModel` que gestiona Firebase Auth.
*/

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    val context = LocalContext.current
    val error by authViewModel.error.collectAsState()
    val user by authViewModel.user.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordEmptyError by remember { mutableStateOf(false) }
    var showConfirmEmptyError by remember { mutableStateOf(false) }
    var showConfirmPasswordError by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val passwordLengthValid by derivedStateOf { password.length >= 6 }
    val passwordDigitValid by derivedStateOf { password.any { it.isDigit() } }
    val passwordSpecialCharValid by derivedStateOf { password.any { !it.isLetterOrDigit() } }
    val passwordValid by derivedStateOf {
        passwordLengthValid && passwordDigitValid && passwordSpecialCharValid
    }

    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val passwordsMatch by derivedStateOf { confirmPassword == password }

    Scaffold(
        snackbarHost = {
            FancySnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // ← Fondo blanco
        ) {
            AnimatedEntrance {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.registerphoto),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .align(Alignment.BottomCenter)
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            "Crea tu",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            "cuenta",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            AnimatedEntrance {
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
                            focusedBorderColor = if (showEmailError) Color.Red else Color.Black,
                            unfocusedBorderColor = if (showEmailError) Color.Red else Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )
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
                    Spacer(modifier = Modifier.height(16.dp))

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
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description =
                                if (confirmPasswordVisible) "Mostrar contraseña" else "Ocultar contraseña"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        isError = showPasswordEmptyError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showPasswordEmptyError) Color.Red else Color.Black,
                            unfocusedBorderColor = if (showPasswordEmptyError) Color.Red else Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )


                    Column(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            "• Mínimo 6 caracteres",
                            fontSize = 12.sp,
                            color = if (passwordLengthValid) Color(0xFF00C853) else Color.Gray
                        )
                        Text(
                            "• Al menos un número",
                            fontSize = 12.sp,
                            color = if (passwordDigitValid) Color(0xFF00C853) else Color.Gray
                        )
                        Text(
                            "• Al menos un símbolo especial",
                            fontSize = 12.sp,
                            color = if (passwordSpecialCharValid) Color(0xFF00C853) else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

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
                                if (confirmPasswordVisible) "Mostrar contraseña" else "Ocultar contraseña"
                            IconButton(onClick = {
                                confirmPasswordVisible = !confirmPasswordVisible
                            }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        isError = showConfirmEmptyError || showConfirmPasswordError,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showConfirmEmptyError || showConfirmPasswordError) Color.Red else Color.Black,
                            unfocusedBorderColor = if (showConfirmEmptyError || showConfirmPasswordError) Color.Red else Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )

                    if (showConfirmPasswordError) {
                        Text(
                            text = "Las contraseñas no coinciden",
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier
                                .padding(top = 4.dp, start = 4.dp)
                                .align(Alignment.Start)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

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
                            authViewModel.register(email, password)
                        } else if (!passwordValid && !passwordEmpty) {
                            scope.launch {
                                snackbarHostState.showSnackbar("La contraseña no cumple los requisitos")
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor, completa todos los campos correctamente")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("¿Ya tienes una cuenta? ", color = Color.Gray, fontSize = 15.sp)
                        Text(
                            text = "Inicia sesión",
                            fontSize = 15.sp,
                            color = Color.Black,
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

    // Mostrar error si viene
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    // Mostrar éxito cuando el usuario no sea null (registro correcto)
    LaunchedEffect(user) {
        user?.let {
            scope.launch {
                snackbarHostState.showSnackbar("Registro completado con éxito")
                // Opcional: navegar automáticamente al inicio si quieres
                // navController.navigate(Screen.Home.route)
            }
        }
    }
}

