package com.example.gymtrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.Routine
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun PredefinedRoutinesScreen(
    viewModel: PredefinedRoutinesViewModel,          // ViewModel de rutinas predefinidas
    routineViewModel: RoutineViewModel               // ViewModel para copiar la rutina al usuario actual
) {
    val context = LocalContext.current
    var routines by remember { mutableStateOf<List<Routine>>(emptyList()) }

    // Cargar rutinas predefinidas cuando se entra en esta pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchRoutines { result ->
            routines = result
        }
    }

    // Mostrar la lista de rutinas predefinidas
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(routines) { routine ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "🏋️ ${routine.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Grupo muscular: ${routine.group}")
                    Text("Tipo: ${routine.type}")
                    if (routine.series > 0) Text("Series: ${routine.series}")
                    if (routine.reps > 0) Text("Reps: ${routine.reps}")
                    if (routine.duration > 0) Text("Duración: ${routine.duration} min")
                    Text("Intensidad: ${routine.intensity}")

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón para añadir a tus rutinas (colección del usuario)
                    Button(
                        onClick = {
                            val mapRoutine = mapOf(
                                "exerciseName" to routine.name,
                                "muscleGroup" to routine.group,
                                "type" to routine.type,
                                "series" to routine.series,
                                "reps" to routine.reps,
                                "duration" to routine.duration,
                                "intensity" to routine.intensity
                            )
                            routineViewModel.copyPredefinedRoutineToUser(mapRoutine) { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Rutina añadida a tus rutinas" else "Error al añadir rutina",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Añadir a mis rutinas")
                    }
                }
            }
        }
    }
}