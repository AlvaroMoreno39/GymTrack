package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtrack.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(), // ViewModel compartido
    onLoginSuccess: () -> Unit // Acción a ejecutar tras login correcto
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val user by authViewModel.user.collectAsState()
    val error by authViewModel.error.collectAsState()

    // Si el login fue exitoso
    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }

        // Mostrar error si existe
        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}