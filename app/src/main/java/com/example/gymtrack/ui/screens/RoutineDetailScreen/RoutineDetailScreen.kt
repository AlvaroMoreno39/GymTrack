package com.example.gymtrack.ui.screens.RoutineDetailScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.viewmodel.Exercise
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoutineDetailScreen(
    navController: NavHostController,
    viewModel: RoutineViewModel,
    routineId: String? = null,
    routineArg: RoutineData? = null,
    isPredefined: Boolean = false
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"
    var routine by remember { mutableStateOf<RoutineData?>(null) }
    var showAddCard by remember { mutableStateOf(false) }

    // Estados para añadir ejercicio
    var nombreEjercicio by remember { mutableStateOf("") }
    var grupoMuscular by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }

    var showNombreError by remember { mutableStateOf(false) }
    var showGrupoError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }

    val gruposMusculares = listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")
    val isCardio = tipo.lowercase() == "cardio"

    // Cargar rutina inicial
    LaunchedEffect(routineId, routineArg) {
        if (routineArg != null) {
            routine = routineArg
        } else if (routineId != null) {
            viewModel.getUserRoutines { list ->
                routine = list.find { it.first == routineId }?.second
            }
        }
    }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) {
        routine?.let { rutina ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScreenHeader(
                    image = R.drawable.my_routines,
                    title = "Rutina",
                    subtitle = rutina.nombreRutina
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // --- Lista de ejercicios ---
                    rutina.ejercicios.forEachIndexed { index, ejercicioOriginal ->
                        var editing by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (editing) {
                                    // EDIT FORMULARIO SIN CARD (solo Column)
                                    EditExerciseForm(
                                        initial = ejercicioOriginal,
                                        gruposMusculares = gruposMusculares,
                                        tipos = tipos,
                                        intensidades = intensidades,
                                        onSave = { ejercicioEditado ->
                                            // Guardar cambios
                                            if (isPredefined && isAdmin) {
                                                routine?.nombreRutina?.let { nombre ->
                                                    viewModel.updateExerciseInPredefinedRoutine(
                                                        nombre, index, ejercicioEditado
                                                    ) { success ->
                                                        if (success) {
                                                            viewModel.fetchPredefinedRoutines { list ->
                                                                routine = list.find { it.nombreRutina == nombre }
                                                            }
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar("Ejercicio editado ✅")
                                                            }
                                                        } else {
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar("Error al editar ❌")
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (!isPredefined && routineId != null) {
                                                viewModel.updateExerciseInRoutine(
                                                    routineId, index, ejercicioEditado
                                                ) { success ->
                                                    if (success) {
                                                        viewModel.getUserRoutines { list ->
                                                            routine = list.find { it.first == routineId }?.second
                                                        }
                                                        scope.launch { snackbarHostState.showSnackbar("Ejercicio editado ✅") }
                                                    } else {
                                                        scope.launch { snackbarHostState.showSnackbar("Error al editar ❌") }
                                                    }
                                                }
                                            }
                                            editing = false
                                        },
                                        onCancel = { editing = false }
                                    )
                                } else {
                                    // VISTA NORMAL
                                    Text(
                                        "• ${ejercicioOriginal.nombre}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Grupo: ${ejercicioOriginal.grupoMuscular}")
                                        Text("Tipo: ${ejercicioOriginal.tipo}")
                                        if (ejercicioOriginal.tipo.lowercase() == "cardio" && ejercicioOriginal.duracion > 0) {
                                            Text("Duración: ${ejercicioOriginal.duracion} min")
                                        } else {
                                            if (ejercicioOriginal.series > 0) Text("Series: ${ejercicioOriginal.series}")
                                            if (ejercicioOriginal.reps > 0) Text("Reps: ${ejercicioOriginal.reps}")
                                        }
                                        Text("Intensidad: ${ejercicioOriginal.intensidad}")
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    if ((isPredefined && isAdmin) || !isPredefined) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            AnimatedAccessButton(
                                                buttonText = "Editar",
                                                onClick = { editing = true },
                                                color = MaterialTheme.colorScheme.onBackground,
                                                contentColor = MaterialTheme.colorScheme.background,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                                modifier = Modifier.weight(1f).height(50.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            AnimatedAccessButton(
                                                buttonText = "Eliminar",
                                                onClick = {
                                                    if (isPredefined && isAdmin) {
                                                        routine?.nombreRutina?.let { nombre ->
                                                            viewModel.deleteExerciseFromPredefinedRoutine(
                                                                nombre,
                                                                index
                                                            ) { success ->
                                                                if (success) {
                                                                    viewModel.fetchPredefinedRoutines { list ->
                                                                        routine = list.find { it.nombreRutina == nombre }
                                                                    }
                                                                    scope.launch {
                                                                        snackbarHostState.showSnackbar("Ejercicio eliminado correctamente ✅")
                                                                    }
                                                                } else {
                                                                    scope.launch {
                                                                        snackbarHostState.showSnackbar("Error al eliminar ejercicio ❌")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (!isPredefined && routineId != null) {
                                                        viewModel.deleteExerciseFromRoutine(
                                                            routineId,
                                                            index
                                                        ) { success ->
                                                            if (success) {
                                                                viewModel.getUserRoutines { list ->
                                                                    routine = list.find { it.first == routineId }?.second
                                                                }
                                                                scope.launch {
                                                                    snackbarHostState.showSnackbar("Ejercicio eliminado correctamente ✅")
                                                                }
                                                            } else {
                                                                scope.launch {
                                                                    snackbarHostState.showSnackbar("Error al eliminar ejercicio ❌")
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                color = MaterialTheme.colorScheme.error,
                                                contentColor = MaterialTheme.colorScheme.background,
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                                modifier = Modifier.weight(1f).height(50.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- Botón de añadir ejercicio SOLO UNA VEZ al final ---
                    if (((isPredefined && isAdmin) || !isPredefined)) {
                        if (showAddCard) {
                            AddExerciseCard(
                                nombre = nombreEjercicio,
                                grupo = grupoMuscular,
                                tipo = tipo,
                                series = series,
                                reps = reps,
                                duracion = duracion,
                                intensidad = intensidad,
                                isCardio = isCardio,
                                gruposMusculares = gruposMusculares,
                                tipos = tipos,
                                intensidades = intensidades,
                                showNombreError = showNombreError,
                                showGrupoError = showGrupoError,
                                showTipoError = showTipoError,
                                showIntensidadError = showIntensidadError,
                                onNombreChange = { nombreEjercicio = it; showNombreError = false },
                                onGrupoChange = { grupoMuscular = it; showGrupoError = false },
                                onTipoChange = {
                                    tipo = it; showTipoError = false; series = ""; reps = ""; duracion = ""
                                },
                                onDuracionChange = { duracion = it },
                                onSeriesChange = { series = it },
                                onRepsChange = { reps = it },
                                onIntensidadChange = { intensidad = it; showIntensidadError = false },
                                onCancelar = { showAddCard = false },
                                onAceptar = {
                                    val errores = listOf(
                                        nombreEjercicio.isBlank(),
                                        grupoMuscular.isBlank(),
                                        tipo.isBlank(),
                                        intensidad.isBlank(),
                                        if (isCardio) duracion.isBlank() else series.isBlank() || reps.isBlank()
                                    )
                                    showNombreError = errores[0]
                                    showGrupoError = errores[1]
                                    showTipoError = errores[2]
                                    showIntensidadError = errores[3]
                                    if (errores.any { it }) {
                                        scope.launch { snackbarHostState.showSnackbar("Rellena todos los campos obligatorios ⚠️") }
                                    } else {
                                        val newExercise = Exercise(
                                            nombre = nombreEjercicio,
                                            grupoMuscular = grupoMuscular,
                                            tipo = tipo,
                                            series = if (!isCardio) series.toIntOrNull() ?: 0 else 0,
                                            reps = if (!isCardio) reps.toIntOrNull() ?: 0 else 0,
                                            duracion = if (isCardio) duracion.toIntOrNull() ?: 0 else 0,
                                            intensidad = intensidad
                                        )
                                        if (isPredefined && isAdmin) {
                                            routine?.nombreRutina?.let { nombre ->
                                                viewModel.addExerciseToPredefinedRoutine(
                                                    nombre,
                                                    newExercise
                                                ) { success ->
                                                    if (success) {
                                                        viewModel.fetchPredefinedRoutines { list ->
                                                            routine = list.find { it.nombreRutina == nombre }
                                                        }
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Ejercicio añadido correctamente ✅")
                                                        }
                                                    } else {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Error al añadir ejercicio ❌")
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (!isPredefined && routineId != null) {
                                            viewModel.addExerciseToRoutine(
                                                routineId,
                                                newExercise
                                            ) { success ->
                                                if (success) {
                                                    viewModel.getUserRoutines { list ->
                                                        routine = list.find { it.first == routineId }?.second
                                                    }
                                                    scope.launch { snackbarHostState.showSnackbar("Ejercicio añadido correctamente ✅") }
                                                } else {
                                                    scope.launch { snackbarHostState.showSnackbar("Error al añadir ejercicio ❌") }
                                                }
                                            }
                                        }
                                        // Limpiar form
                                        showAddCard = false
                                        nombreEjercicio = ""
                                        grupoMuscular = ""
                                        tipo = ""
                                        series = ""
                                        reps = ""
                                        duracion = ""
                                        intensidad = ""
                                    }
                                }
                            )
                        } else {
                            AnimatedAccessButton(
                                buttonText = "Añadir ejercicio",
                                onClick = { showAddCard = true },
                                color = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

