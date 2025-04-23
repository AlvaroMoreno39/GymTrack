package com.example.gymtrack.ui.screens

/*
LoginScreen.kt

Esta pantalla permite al usuario iniciar sesión en GymTrack mediante:
- Correo electrónico y contraseña.
- Inicio de sesión con Google (integrado con Firebase Auth).
También ofrece:
- Validación de campos vacíos.
- Mostrar errores mediante Snackbar.
- Navegación a las pantallas de registro y recuperación de contraseña.
- Un diseño atractivo con imagen de cabecera y estilos personalizados.
*/

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
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Estados que vienen del ViewModel
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Campos controlados por el usuario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Snackbar para mostrar mensajes de error
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Launcher para Google Sign-In
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

    // Si login fue exitoso o hay error, muestra mensaje
    LaunchedEffect(user, error) {
        if (user != null) {
            onLoginSuccess()
        } else if (!email.isBlank() && !password.isBlank() && error != null) {
            scope.launch {
                snackbarHostState.showSnackbar("Usuario o contraseña incorrectos")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {

            // CABECERA CON IMAGEN Y TEXTO
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
                // Lona blanca translúcida
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White.copy(alpha = 0.65f))
                )
                // Texto sobre la imagen
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text("Inicia sesión en tu", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text("cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // FORMULARIO DE LOGIN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Enlace recuperación de contraseña
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

                // BOTÓN PERSONALIZADO
                AnimatedAccessButton {
                    if (email.isBlank() || password.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor, completa todos los campos")
                        }
                    } else {
                        authViewModel.login(email, password)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // SEPARADOR "O INICIA CON"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                    Text("  O inicia sesión con  ", color = Color.Gray, fontSize = 14.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN DE GOOGLE
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

                // ENLACE PARA REGISTRARSE
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

// BOTÓN DE ACCESO CON ANIMACIÓN PERSONALIZADA
@Composable
fun AnimatedAccessButton(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }

    val animationSpec = tween<Color>(
        durationMillis = 350,
        easing = FastOutSlowInEasing
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) Color.White else Color.Black,
        animationSpec = animationSpec,
        label = "ButtonBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (pressed) Color.Black else Color.White,
        animationSpec = animationSpec,
        label = "ButtonContentColor"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
            onClick()
        }
    }

    Button(
        onClick = { pressed = true },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues()
    ) {
        Text("Acceder", fontSize = 16.sp)
    }
}