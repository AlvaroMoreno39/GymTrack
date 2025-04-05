package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun ViewRoutinesScreen(viewModel: RoutineViewModel) {
    var routines by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Cargar rutinas al entrar en pantalla
    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { loadedRoutines ->
            routines = loadedRoutines
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Tus rutinas", style = MaterialTheme.typography.titleLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(routines) { routine ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("🏋️ ${routine["exerciseName"]}", style = MaterialTheme.typography.titleMedium)
                        Text("Grupo muscular: ${routine["muscleGroup"]}")
                        Text("Tipo: ${routine["type"]}")
                        routine["series"]?.let { Text("Series: $it") }
                        routine["reps"]?.let { Text("Reps: $it") }
                        routine["duration"]?.let { Text("Duración: $it min") }
                        Text("Intensidad: ${routine["intensity"]}")
                    }
                }
            }
        }
    }
}