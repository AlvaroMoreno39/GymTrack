package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.R
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.viewmodel.Exercise
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.AnimatedEntrance

import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoutineDetailScreen(
    routineId: String,
    viewModel: RoutineViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var routine by remember { mutableStateOf<RoutineData?>(null) }
    var showAddCard by remember { mutableStateOf(false) }

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

    LaunchedEffect(routineId) {
        viewModel.getUserRoutines { list ->
            routine = list.find { it.first == routineId }?.second
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
                    rutina.ejercicios.forEachIndexed { index, ejercicioOriginal ->
                        var editing by remember { mutableStateOf(false) }
                        var ejercicio by remember { mutableStateOf(ejercicioOriginal) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Fondo blanco
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                if (!editing) {
                                    Text(
                                        "• ${ejercicio.nombre}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                if (editing) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = ejercicio.nombre,
                                            onValueChange = {
                                                ejercicio = ejercicio.copy(nombre = it)
                                            },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        DropDownSelector(
                                            "Grupo Muscular",
                                            gruposMusculares,
                                            ejercicio.grupoMuscular
                                        ) {
                                            ejercicio = ejercicio.copy(grupoMuscular = it)
                                        }
                                        DropDownSelector("Tipo", tipos, ejercicio.tipo) {
                                            ejercicio = ejercicio.copy(tipo = it)
                                        }

                                        if (ejercicio.tipo.lowercase() == "cardio") {
                                            OutlinedTextField(
                                                value = ejercicio.duracion.toString(),
                                                onValueChange = {
                                                    ejercicio = ejercicio.copy(
                                                        duracion = it.toIntOrNull() ?: 0
                                                    )
                                                },
                                                label = { Text("Duración (min)") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        } else {
                                            OutlinedTextField(
                                                value = ejercicio.series.toString(),
                                                onValueChange = {
                                                    ejercicio = ejercicio.copy(
                                                        series = it.toIntOrNull() ?: 0
                                                    )
                                                },
                                                label = { Text("Series") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = ejercicio.reps.toString(),
                                                onValueChange = {
                                                    ejercicio = ejercicio.copy(reps = it.toIntOrNull() ?: 0)
                                                },
                                                label = { Text("Repeticiones") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }

                                        DropDownSelector(
                                            "Intensidad",
                                            intensidades,
                                            ejercicio.intensidad
                                        ) {
                                            ejercicio = ejercicio.copy(intensidad = it)
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Grupo: ${ejercicio.grupoMuscular}")
                                        Text("Tipo: ${ejercicio.tipo}")
                                        Text("Series: ${ejercicio.series}")
                                        Text("Reps: ${ejercicio.reps}")
                                        Text("Duración: ${ejercicio.duracion} min")
                                        Text("Intensidad: ${ejercicio.intensidad}")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    AnimatedAccessButton(
                                        buttonText = if (editing) "Guardar" else "Editar",
                                        onClick = {
                                            if (editing) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Cambios guardados")
                                                }
                                            }
                                            editing = !editing
                                        },
                                        color = MaterialTheme.colorScheme.onBackground,
                                        contentColor = MaterialTheme.colorScheme.background,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    )

                                    if (!editing) {
                                        Spacer(modifier = Modifier.width(12.dp))
                                        AnimatedAccessButton(
                                            buttonText = "Eliminar",
                                            onClick = {
                                                viewModel.deleteExerciseFromRoutine(routineId, index)
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Ejercicio eliminado correctamente")
                                                }
                                            },
                                            color = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Añadir ejercicio
                    if (showAddCard) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = nombreEjercicio,
                                    onValueChange = {
                                        nombreEjercicio = it
                                        showNombreError = false
                                    },
                                    label = { Text("Nombre del ejercicio") },
                                    isError = showNombreError,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                DropDownSelector("Grupo Muscular", gruposMusculares, grupoMuscular) {
                                    grupoMuscular = it
                                    showGrupoError = false
                                }
                                DropDownSelector("Tipo", tipos, tipo) {
                                    tipo = it
                                    showTipoError = false
                                }

                                if (isCardio) {
                                    OutlinedTextField(
                                        value = duracion,
                                        onValueChange = { duracion = it },
                                        label = { Text("Duración (min)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    OutlinedTextField(
                                        value = series,
                                        onValueChange = { series = it },
                                        label = { Text("Series") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = reps,
                                        onValueChange = { reps = it },
                                        label = { Text("Repeticiones") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                DropDownSelector("Intensidad", intensidades, intensidad) {
                                    intensidad = it
                                    showIntensidadError = false
                                }

                                AnimatedAccessButton(buttonText = "Añadir ejercicio", modifier = Modifier.fillMaxWidth()) {
                                    val errores = listOf(
                                        nombreEjercicio.isBlank(),
                                        grupoMuscular.isBlank(),
                                        tipo.isBlank(),
                                        intensidad.isBlank()
                                    )

                                    showNombreError = errores[0]
                                    showGrupoError = errores[1]
                                    showTipoError = errores[2]
                                    showIntensidadError = errores[3]

                                    if (errores.any { it }) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Rellena todos los campos obligatorios")
                                        }
                                    } else {
                                        val newExercise = Exercise(
                                            nombre = nombreEjercicio,
                                            grupoMuscular = grupoMuscular,
                                            tipo = tipo,
                                            series = series.toIntOrNull() ?: 0,
                                            reps = reps.toIntOrNull() ?: 0,
                                            duracion = if (isCardio) duracion.toIntOrNull() ?: 0 else 0,
                                            intensidad = intensidad
                                        )
                                        viewModel.addExerciseToRoutine(routineId, newExercise) { success ->
                                            if (success) {
                                                viewModel.getUserRoutines { list ->
                                                    routine = list.find { it.first == routineId }?.second
                                                }
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Ejercicio añadido correctamente")
                                                }
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Error al añadir ejercicio")
                                                }
                                            }
                                        }
                                    }
                                }

                                AnimatedAccessButton(
                                    buttonText = "Cancelar",
                                    onClick = { showAddCard = false },
                                    color = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.background,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                )
                            }
                        }
                    }

                    if (!showAddCard) {
                        AnimatedAccessButton(
                            buttonText = "Añadir ejercicio",
                            onClick = { showAddCard = true },
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
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
