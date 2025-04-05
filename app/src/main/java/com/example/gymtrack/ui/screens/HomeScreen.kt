package com.example.gymtrack.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController) {
    Text("Home")
    Button(
        onClick = {
            navController.navigate(Screen.RegisterRoutine.route)
        }
    ) {
        Text("Registrar nueva rutina")
    }
}