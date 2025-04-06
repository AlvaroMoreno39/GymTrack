package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.Routine

@Composable
fun PredefinedRoutinesScreen(viewModel: PredefinedRoutinesViewModel) {
    val routines by viewModel.routines.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Rutinas predefinidas", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(routines) { routine ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ejercicio: ${routine.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Grupo: ${routine.group}")
                        Text("Tipo: ${routine.type}")
                        Text("Intensidad: ${routine.intensity}")
                        if (routine.type.lowercase() == "cardio") {
                            Text("Duración: ${routine.duration} min")
                        } else {
                            Text("Series: ${routine.series}, Reps: ${routine.reps}")
                        }
                    }
                }
            }
        }
    }
}