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
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun ViewRoutinesScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current

    // Lista de rutinas del usuario (cada una con su ID y datos)
    var routines by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }

    // Cargar rutinas al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { loaded ->
            routines = loaded
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tus rutinas", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(routines) { (id, rutina) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Nombre de la rutina
                        Text("🏋️ ${rutina.nombreRutina}", style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(4.dp))

                        // Mostrar cada ejercicio
                        rutina.ejercicios.forEach { ejercicio ->
                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                Text("• ${ejercicio.nombre}", style = MaterialTheme.typography.bodyLarge)
                                Text("Grupo muscular: ${ejercicio.grupoMuscular}")
                                Text("Tipo: ${ejercicio.tipo}")
                                if (ejercicio.series > 0) Text("Series: ${ejercicio.series}")
                                if (ejercicio.reps > 0) Text("Reps: ${ejercicio.reps}")
                                if (ejercicio.duracion > 0) Text("Duración: ${ejercicio.duracion} min")
                                Text("Intensidad: ${ejercicio.intensidad}")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botón para eliminar la rutina completa
                        Button(
                            onClick = {
                                viewModel.deleteRoutine(id) { success ->
                                    if (success) {
                                        Toast.makeText(context, "Rutina eliminada", Toast.LENGTH_SHORT).show()
                                        viewModel.getUserRoutines { updated ->
                                            routines = updated
                                        }
                                    } else {
                                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Eliminar rutina")
                        }
                    }
                }
            }
        }
    }
}
