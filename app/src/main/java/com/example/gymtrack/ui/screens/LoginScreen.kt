package com.example.gymtrack.ui.screens

/*
LoginScreen.kt

Esta pantalla permite al usuario iniciar sesión en GymTrack mediante:
- Correo electrónico y contraseña.
- Inicio de sesión con Google (integrado con Firebase Auth).
También ofrece:
- Validación de campos vacíos y errores.
- Mostrar mensajes de error mediante Snackbar.
- Navegación a las pantallas de registro y recuperación de contraseña.
- Un diseño atractivo con imagen de cabecera y estilos personalizados.
*/

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    // Limpiar errores previos al cargar la pantalla
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    // Contexto actual de la app (para navegación, recursos, etc.)
    val context = LocalContext.current

    // Observadores de usuario autenticado y errores desde el ViewModel
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Estados locales para email y contraseña ingresados por el usuario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados para mostrar errores visuales en los campos
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }

    // Validación de formato de email
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Snackbar para mostrar errores y mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Configuración de login con Google
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        Log.e("LOGIN", "Error con Google", loginTask.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.e("LOGIN", "Google sign in failed", e)
        }
    }

    // Manejo de éxito o error en login
    LaunchedEffect(user, error) {
        if (user != null) {
            onLoginSuccess()
        } else if (!email.isBlank() && !password.isBlank() && error != null) {
            scope.launch {
                snackbarHostState.showSnackbar("Usuario o contraseña incorrectos")
            }
        }
    }

    // Estructura visual general
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera con imagen y título
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loginphoto),
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
                    Text("Inicia sesión en tu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Formulario principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo de correo electrónico con validación
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

                // Campo de contraseña con validación
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showPasswordError = false
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = showPasswordError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (showPasswordError) Color.Red else Color.Black,
                        unfocusedBorderColor = if (showPasswordError) Color.Red else Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )
                if (showPasswordError) {
                    Text(
                        text = "Introduce tu contraseña",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .padding(top = 4.dp, start = 4.dp)
                            .align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace a recuperación de contraseña
                Text(
                    "¿Olvidaste tu contraseña?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            navController.navigate(Screen.ForgotPassword.route)
                        })

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de login con validación previa
                AnimatedAccessButton {
                    showEmailError = email.isBlank() || !isValidEmail
                    showPasswordError = password.isBlank()

                    if (!showEmailError && !showPasswordError) {
                        authViewModel.login(email, password)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor, completa todos los campos correctamente")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Separador visual
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text("  O inicia sesión con  ", color = Color.Gray, fontSize = 14.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de login con Google
                Button(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = "Google logo",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text("Google", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Enlace a registro
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("¿No tienes una cuenta? ", color = Color.Gray, fontSize = 15.sp)
                    Text(
                        text = "Regístrate",
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }
            }
        }
    }
}