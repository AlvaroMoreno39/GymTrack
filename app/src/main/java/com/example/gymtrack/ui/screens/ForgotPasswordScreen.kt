package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gymtrack.viewmodel.AuthViewModel


@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") } // Estado del campo de email
    val error by authViewModel.error.collectAsState() // Estado del mensaje de Firebase

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Recuperar contraseña", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        // Campo de texto para el email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón para enviar el correo de recuperación
        Button(
            onClick = {
                authViewModel.resetPassword(email)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar correo")
        }

        // Mostrar mensaje si hay error o confirmación
        if (!error.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error ?: "",
                color = if (error!!.contains("enviado", ignoreCase = true)) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Opción para volver al login
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Volver al login")
        }
    }
}