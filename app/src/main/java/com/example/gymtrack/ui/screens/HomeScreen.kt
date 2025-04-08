package com.example.gymtrack.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController) {
    Column (modifier = Modifier.fillMaxSize()) {
        Text("Home")
        Button(
            onClick = {
                navController.navigate(Screen.RegisterRoutine.route)
            }
        ) {
            Text("Registrar nueva rutina")
        }

        Button(onClick = {
            navController.navigate(Screen.PredefinedRoutines.route)
        }) {
            Text("Ver rutinas predefinidas")
        }

        Button(
            onClick = { navController.navigate(Screen.ViewRoutinesScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver mis rutinas")
        }

        Button(onClick = {
            navController.navigate(Screen.ProgressGeneral.route)
        }) {
            Text("Ver reps de ejercicios gafico")
        }

        Button(onClick = {
            navController.navigate(Screen.ExerciseDashboard.route)
        }) {
            Text("Evolucion de carga por ejercicio")
        }


    }

}