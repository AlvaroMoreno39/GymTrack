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
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel

@Composable
fun PredefinedRoutinesScreen(
    viewModel: PredefinedRoutinesViewModel,
    routineViewModel: RoutineViewModel
) {
    val context = LocalContext.current
    var routines by remember { mutableStateOf<List<RoutineData>>(emptyList()) }

    // Cargar rutinas al entrar
    LaunchedEffect(Unit) {
        viewModel.fetchRoutines { result ->
            routines = result
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(routines) { rutina ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🏋️ ${rutina.nombreRutina}", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(4.dp))

                    // Mostrar los ejercicios de la rutina
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

                    // Botón para copiar la rutina predefinida al usuario
                    Button(
                        onClick = {
                            routineViewModel.copyPredefinedRoutineToUser(
                                nombreRutina = rutina.nombreRutina,
                                ejercicios = rutina.ejercicios
                            ) { success ->
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