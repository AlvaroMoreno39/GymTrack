package com.example.gymtrack.ui.screens.RoutineDetailScreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.with
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
import kotlinx.coroutines.delay

/**
 * RoutineDetailScreen.kt
 *
 * Pantalla detallada para visualizar y editar una rutina específica en GymTrack.
 *
 * Permite:
 * - Mostrar ejercicios de la rutina.
 * - Editar, eliminar o añadir ejercicios.
 * - Diferenciar rutinas propias (user) y predefinidas (admin).
 *
 * Usa:
 * - Animaciones avanzadas (entrada, rebote, fade).
 * - Snackbars para feedback.
 * - ViewModel para acceder a Firebase.
 * - Componentes reutilizables como AddExerciseCard y EditExerciseForm.
 */

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoutineDetailScreen(
    viewModel: RoutineViewModel,
    routineId: String? = null,
    routineArg: RoutineData? = null,
    isPredefined: Boolean = false
) {
    // Snackbar para feedback visual
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Comprueba si el usuario actual es admin
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    // Estado principal de la rutina cargada (propia o predefinida)
    var routine by remember { mutableStateOf<RoutineData?>(null) }

    // Controla si se muestra el bloque de añadir ejercicio
    var showAddCard by remember { mutableStateOf(false) }

    // Mapas de control de animación por ID de ejercicio
    val visibleMap = remember { mutableStateMapOf<String, Boolean>() }
    val bounceMap = remember { mutableStateMapOf<String, Boolean>() }

    // Opciones fijas para los selectores de formulario
    val gruposMusculares = listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")

    // Carga inicial de rutina según si viene como argumento o por ID
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
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600))
            ) {
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
                        rutina.ejercicios.forEach { ejercicio ->
                            var editing by remember { mutableStateOf(false) }
                            val isVisible = visibleMap.getOrDefault(ejercicio.id, true)
                            val isBouncing = bounceMap.getOrDefault(ejercicio.id, false)

                            AnimatedVisibility(
                                visible = isVisible,
                                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(
                                    tween(
                                        200
                                    )
                                )
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(if (isBouncing) Modifier.animateContentSize() else Modifier),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    AnimatedContent(
                                        targetState = editing,
                                        transitionSpec = { fadeIn(tween(250)) with fadeOut(tween(180)) },
                                        label = "EditTransition"
                                    ) { isEditing ->
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            if (isEditing) {
                                                EditExerciseForm(
                                                    initial = ejercicio,
                                                    gruposMusculares = gruposMusculares,
                                                    tipos = tipos,
                                                    intensidades = intensidades,
                                                    snackbarHostState = snackbarHostState,
                                                    onSave = { updatedExercise ->
                                                        if (isPredefined && isAdmin) {
                                                            routine?.nombreRutina?.let { nombre ->
                                                                viewModel.updateExerciseInPredefinedRoutineById(
                                                                    nombre,
                                                                    ejercicio.id,
                                                                    updatedExercise
                                                                ) { success ->
                                                                    if (success) {
                                                                        bounceMap[ejercicio.id] =
                                                                            true
                                                                        viewModel.fetchPredefinedRoutines { list ->
                                                                            routine =
                                                                                list.find { it.nombreRutina == nombre }
                                                                        }
                                                                        scope.launch {
                                                                            snackbarHostState.showSnackbar(
                                                                                "Ejercicio editado ✅"
                                                                            )
                                                                            delay(600)
                                                                            bounceMap[ejercicio.id] =
                                                                                false
                                                                        }
                                                                    } else {
                                                                        scope.launch {
                                                                            snackbarHostState.showSnackbar(
                                                                                "Error al editar ❌"
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else if (!isPredefined && routineId != null) {
                                                            viewModel.updateExerciseInRoutineById(
                                                                routineId,
                                                                ejercicio.id,
                                                                updatedExercise
                                                            ) { success ->
                                                                if (success) {
                                                                    bounceMap[ejercicio.id] = true
                                                                    viewModel.getUserRoutines { list ->
                                                                        routine =
                                                                            list.find { it.first == routineId }?.second
                                                                    }
                                                                    scope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "Ejercicio editado ✅"
                                                                        )
                                                                        delay(600)
                                                                        bounceMap[ejercicio.id] =
                                                                            false
                                                                    }
                                                                } else {
                                                                    scope.launch {
                                                                        snackbarHostState.showSnackbar(
                                                                            "Error al editar ❌"
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        editing = false
                                                    },
                                                    onCancel = { editing = false }
                                                )
                                            } else {
                                                Text(
                                                    "• ${ejercicio.nombre}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text("Grupo: ${ejercicio.grupoMuscular}")
                                                    Text("Tipo: ${ejercicio.tipo}")
                                                    if (ejercicio.tipo.lowercase() == "cardio" && ejercicio.duracion > 0) {
                                                        Text("Duración: ${ejercicio.duracion} min")
                                                    } else {
                                                        if (ejercicio.series > 0) Text("Series: ${ejercicio.series}")
                                                        if (ejercicio.reps > 0) Text("Reps: ${ejercicio.reps}")
                                                    }
                                                    Text("Intensidad: ${ejercicio.intensidad}")
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
                                                            border = BorderStroke(
                                                                1.dp,
                                                                MaterialTheme.colorScheme.onBackground
                                                            ),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(50.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        AnimatedAccessButton(
                                                            buttonText = "Eliminar",
                                                            onClick = {
                                                                visibleMap[ejercicio.id] = false
                                                                scope.launch {
                                                                    delay(300)
                                                                    if (isPredefined && isAdmin) {
                                                                        routine?.nombreRutina?.let { nombre ->
                                                                            viewModel.deleteExerciseFromPredefinedRoutineById(
                                                                                nombre, ejercicio.id
                                                                            ) { success ->
                                                                                if (success) {
                                                                                    viewModel.fetchPredefinedRoutines { list ->
                                                                                        routine =
                                                                                            list.find { it.nombreRutina == nombre }
                                                                                    }
                                                                                    scope.launch {
                                                                                        snackbarHostState.showSnackbar(
                                                                                            "Ejercicio eliminado ✅"
                                                                                        )
                                                                                    }
                                                                                } else {
                                                                                    scope.launch {
                                                                                        snackbarHostState.showSnackbar(
                                                                                            "Error al eliminar ❌"
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } else if (!isPredefined && routineId != null) {
                                                                        viewModel.deleteExerciseFromRoutineById(
                                                                            routineId, ejercicio.id
                                                                        ) { success ->
                                                                            if (success) {
                                                                                viewModel.getUserRoutines { list ->
                                                                                    routine =
                                                                                        list.find { it.first == routineId }?.second
                                                                                }
                                                                                scope.launch {
                                                                                    snackbarHostState.showSnackbar(
                                                                                        "Ejercicio eliminado ✅"
                                                                                    )
                                                                                }
                                                                            } else {
                                                                                scope.launch {
                                                                                    snackbarHostState.showSnackbar(
                                                                                        "Error al eliminar ❌"
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            color = MaterialTheme.colorScheme.error,
                                                            contentColor = MaterialTheme.colorScheme.background,
                                                            border = BorderStroke(
                                                                1.dp,
                                                                MaterialTheme.colorScheme.error
                                                            ),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .height(50.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if ((isPredefined && isAdmin) || !isPredefined) {
                            AnimatedVisibility(
                                visible = showAddCard,
                                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                            ) {
                                AddExerciseCard(
                                    gruposMusculares = gruposMusculares,
                                    tipos = tipos,
                                    intensidades = intensidades,
                                    snackbarHostState = snackbarHostState,
                                    onAceptar = { newExercise ->
                                        if (isPredefined && isAdmin) {
                                            routine?.nombreRutina?.let { nombre ->
                                                viewModel.addExerciseToPredefinedRoutine(
                                                    nombre,
                                                    newExercise
                                                ) { success ->
                                                    scope.launch {
                                                        if (success) {
                                                            viewModel.fetchPredefinedRoutines { list ->
                                                                routine = list.find { it.nombreRutina == nombre }
                                                            }
                                                            snackbarHostState.showSnackbar("✅ Ejercicio añadido correctamente")
                                                        } else {
                                                            snackbarHostState.showSnackbar("❌ Error al añadir el ejercicio")
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (!isPredefined && routineId != null) {
                                            viewModel.addExerciseToRoutine(
                                                routineId,
                                                newExercise
                                            ) { success ->
                                                scope.launch {
                                                    if (success) {
                                                        viewModel.getUserRoutines { list ->
                                                            routine = list.find { it.first == routineId }?.second
                                                        }
                                                        snackbarHostState.showSnackbar("✅ Ejercicio añadido correctamente")
                                                    } else {
                                                        snackbarHostState.showSnackbar("❌ Error al añadir el ejercicio")
                                                    }
                                                }
                                            }
                                        }

                                        // Al terminar, oculta la tarjeta
                                        showAddCard = false
                                    },
                                    onCancelar = {
                                        showAddCard = false
                                    }
                                )
                            }
                            AnimatedAccessButton(
                                buttonText = if (showAddCard) "Cancelar" else "Añadir ejercicio",
                                onClick = { showAddCard = !showAddCard },
                                color = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.background,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

