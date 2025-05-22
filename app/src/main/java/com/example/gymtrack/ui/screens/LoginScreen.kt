package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

/*
LoginScreen.kt

Pantalla de inicio de sesión de GymTrack.
Permite el login con email/contraseña o con Google, valida campos y muestra errores.
Incluye navegación a registro y recuperación, feedback con Snackbar, y una UI coherente con el resto de la app.
*/

@SuppressLint("UnrememberedMutableState")
@Composable
fun LoginScreen(
    navController: NavHostController,      // Controlador de navegación
    authViewModel: AuthViewModel = viewModel(), // ViewModel que gestiona la autenticación
    onLoginSuccess: () -> Unit             // Callback a ejecutar tras login correcto (normal o Google)
) {
    // Limpia errores previos al montar la pantalla
    LaunchedEffect(Unit) {
        authViewModel.clearError()
    }

    val context = LocalContext.current

    // Observa el usuario autenticado y los errores mediante StateFlow
    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Estados para los campos de formulario y su validación
    var email by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }

    // Valida el email en tiempo real usando patrones estándar de Android
    val isValidEmail by derivedStateOf {
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Lógica de inicio de sesión con Google, usando ActivityResult API y Firebase Auth
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
                        scope.launch {
                            snackbarHostState.showSnackbar("Inicio de sesión exitoso ✅")
                        }
                        onLoginSuccess()
                    } else {
                        Log.e("LOGIN", "Error con Google", loginTask.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.e("LOGIN", "Google sign in failed", e)
        }
    }

    // Reacciona al cambio de usuario o error: navega o muestra mensaje según el caso
    LaunchedEffect(user, error) {
        if (user != null) {
            scope.launch {
                onLoginSuccess()
            }
        } else if (!email.isBlank() && !password.isBlank() && error != null) {
            scope.launch {
                snackbarHostState.showSnackbar("Usuario o contraseña incorrectos ❌")
            }
        }
    }

    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Cabecera visual animada (imagen, título, subtítulo)
            ScreenHeader(
                image = R.drawable.login,
                title = "Bienvenido de nuevo",
                subtitle = "Accede a tu cuenta personal"
            )

            // Formulario animado de entrada
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 600)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo de email con validación y feedback visual
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña, con opción de mostrar/ocultar y feedback de error
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            showPasswordError = false
                        },
                        label = { Text("Contraseña") },
                        isError = showPasswordError,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            val description = if (passwordVisible) "Mostrar contraseña" else "Ocultar contraseña"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = icon, contentDescription = description)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (showPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            unfocusedBorderColor = if (showPasswordError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    if (showPasswordError) {
                        Text(
                            text = "Introduce tu contraseña",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
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
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                navController.navigate(Screen.ForgotPassword.route)
                            })

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón principal de login, con validación previa de campos
                    AnimatedAccessButton(
                        buttonText = "Acceder",
                        color = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                        fontSize = 16.sp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showEmailError = email.isBlank() || !isValidEmail
                            showPasswordError = password.isBlank()

                            if (!showEmailError && !showPasswordError) {
                                authViewModel.login(email, password)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Por favor, completa todos los campos correctamente ⚠️")
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Separador visual con texto
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LightGray)
                        Text("  O inicia sesión con  ", color = LightGray, fontSize = 14.sp)
                        HorizontalDivider(modifier = Modifier.weight(1f), color = LightGray)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de login con Google, integrado con Firebase Auth
                    Button(
                        onClick = {
                            val gso =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
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

                    // Enlace a pantalla de registro
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("¿No tienes una cuenta? ", color = LightGray, fontSize = 15.sp)
                        Text(
                            text = "Regístrate",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onBackground,
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
}
