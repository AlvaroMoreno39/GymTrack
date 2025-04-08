package com.example.gymtrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun RegisterRoutineScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current

    // Estado para el nombre de la rutina
    var nombreRutina by remember { mutableStateOf("") }

    // Estados para los campos del ejercicio actual
    var nombreEjercicio by remember { mutableStateOf("") }
    var grupoMuscular by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }

    var ejercicios by remember { mutableStateOf(mutableListOf<Exercise>()) }

    // Comprobación para mostrar campos distintos si es cardio
    val isCardio = tipo.lowercase() == "cardio"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear nueva rutina", style = MaterialTheme.typography.titleLarge)

        // Campo para el nombre de la rutina
        OutlinedTextField(
            value = nombreRutina,
            onValueChange = { nombreRutina = it },
            label = { Text("Nombre de la rutina") },
            modifier = Modifier.fillMaxWidth()
        )

        Divider()

        Text("Añadir ejercicio", style = MaterialTheme.typography.titleMedium)

        // Campo: nombre del ejercicio
        OutlinedTextField(
            value = nombreEjercicio,
            onValueChange = { nombreEjercicio = it },
            label = { Text("Nombre del ejercicio") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo: grupo muscular
        OutlinedTextField(
            value = grupoMuscular,
            onValueChange = { grupoMuscular = it },
            label = { Text("Grupo muscular") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo: tipo de ejercicio
        OutlinedTextField(
            value = tipo,
            onValueChange = { tipo = it },
            label = { Text("Tipo (fuerza/cardio/mixto)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Si el ejercicio es cardio, mostramos el campo duración
        if (isCardio) {
            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it },
                label = { Text("Duración (min)") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Si no es cardio, mostramos series y reps
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

        // Campo: intensidad
        OutlinedTextField(
            value = intensidad,
            onValueChange = { intensidad = it },
            label = { Text("Intensidad") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo: peso
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para añadir un ejercicio a la lista
        Button(
            onClick = {
                // Comprobamos que los campos estén completos
                if (nombreEjercicio.isBlank() || grupoMuscular.isBlank() || tipo.isBlank() || intensidad.isBlank()) {
                    Toast.makeText(context, "⚠️ Rellena todos los campos", Toast.LENGTH_SHORT).show()

                }

                // Creamos el objeto ejercicio y lo añadimos
                val nuevoEjercicio = Exercise(
                    nombre = nombreEjercicio,
                    grupoMuscular = grupoMuscular,
                    tipo = tipo,
                    series = series.toIntOrNull() ?: 0,
                    reps = reps.toIntOrNull() ?: 0,
                    duracion = duracion.toIntOrNull() ?: 0,
                    intensidad = intensidad,
                    peso = peso.toIntOrNull() ?: 0,
                )

                ejercicios.add(nuevoEjercicio)

                // Limpiar los campos para añadir otro ejercicio
                nombreEjercicio = ""
                grupoMuscular = ""
                tipo = ""
                series = ""
                reps = ""
                duracion = ""
                intensidad = ""
                peso = ""

                Toast.makeText(context, "✅ Ejercicio añadido", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Añadir ejercicio")
        }

        Divider()

        // Mostrar la cantidad de ejercicios añadidos
        Text("Ejercicios añadidos: ${ejercicios.size}")

        // Botón para guardar la rutina completa
        Button(
            onClick = {
                // Comprobamos que se haya definido nombre y al menos un ejercicio
                if (nombreRutina.isBlank() || ejercicios.isEmpty()) {
                    Toast.makeText(context, "⚠️ Añade un nombre y al menos un ejercicio", Toast.LENGTH_SHORT).show()

                }

                // Guardar rutina completa en Firestore
                viewModel.saveFullRoutine(
                    context = context,
                    nombreRutina = nombreRutina,
                    ejercicios = ejercicios
                )

                // Resetear estado tras guardar
                nombreRutina = ""
                ejercicios = mutableListOf()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Guardar rutina completa")
        }
    }
}
