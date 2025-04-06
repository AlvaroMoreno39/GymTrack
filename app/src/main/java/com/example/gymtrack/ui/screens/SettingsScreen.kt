package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.AuthViewModel

@Composable
fun SettingsScreen(authViewModel: AuthViewModel, navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚙️ Ajustes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para cerrar sesión
        Button(
            onClick = {
                authViewModel.logout(context)
                navController.navigate(Screen.PredefinedRoutines.route)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
        }
    }
}
