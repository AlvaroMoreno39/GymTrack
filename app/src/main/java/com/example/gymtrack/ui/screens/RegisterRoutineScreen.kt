package com.example.gymtrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun RegisterRoutineScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current // Necesario para mostrar Toast

    // Estados del formulario
    var exerciseName by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var intensity by remember { mutableStateOf("") }

    val isCardio = type.lowercase() == "cardio"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Campos de entrada (igual que antes)
        OutlinedTextField(
            value = exerciseName,
            onValueChange = { exerciseName = it },
            label = { Text("Nombre del ejercicio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = muscleGroup,
            onValueChange = { muscleGroup = it },
            label = { Text("Grupo muscular") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = type,
            onValueChange = { type = it },
            label = { Text("Tipo de rutina (fuerza/cardio/mixto)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isCardio) {
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duración (min)") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = series,
                onValueChange = { series = it },
                label = { Text("Series") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Repeticiones") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = intensity,
            onValueChange = { intensity = it },
            label = { Text("Intensidad (suave/media/reventarse)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón que llama a addRoutine y muestra feedback
        Button(
            onClick = {
                viewModel.addRoutine(
                    context = context,
                    name = exerciseName,
                    group = muscleGroup,
                    type = type,
                    series = series,
                    reps = reps,
                    duration = duration,
                    intensity = intensity
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Guardar rutina")
        }
    }
}