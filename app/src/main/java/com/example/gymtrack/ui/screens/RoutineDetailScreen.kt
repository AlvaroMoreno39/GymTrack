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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.gymtrack.viewmodel.Exercise
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

    // Estado para nuevo ejercicio
    var nombreEjercicio by remember { mutableStateOf("") }
    var grupoMuscular by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }

    // Validaciones
    var showNombreError by remember { mutableStateOf(false) }
    var showGrupoError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }

    // Opciones de menú
    val gruposMusculares =
        listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")
    val isCardio = tipo.lowercase() == "cardio"

    LaunchedEffect(routineId) {
        viewModel.getUserRoutines { list ->
            routine = list.find { it.first == routineId }?.second
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        routine?.let { rutina ->
            Column(modifier = Modifier.fillMaxSize()) {
                // Cabecera visual
                AnimatedEntrance {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.my_routines),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .align(Alignment.BottomCenter)
                                .background(Color.White.copy(alpha = 0.65f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text(
                                "Rutina",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                rutina.nombreRutina,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mostrar ejercicios actuales
                    rutina.ejercicios.forEachIndexed { index, ejercicioOriginal ->
                        var editing by remember { mutableStateOf(false) }
                        var ejercicio by remember { mutableStateOf(ejercicioOriginal) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "• ${ejercicio.nombre}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Spacer(modifier = Modifier.height(6.dp))

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
                                                    ejercicio =
                                                        ejercicio.copy(reps = it.toIntOrNull() ?: 0)
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
                                                    snackbarHostState.showSnackbar("Cambios guardados (mock)")
                                                }
                                            }
                                            editing = !editing
                                        },
                                        containerColor = Color.Black,
                                        contentColor = Color.White,
                                        borderColor = Color.Black,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    )

                                    if (!editing) {
                                        Spacer(modifier = Modifier.width(12.dp))

                                        AnimatedAccessButton(
                                            buttonText = "Eliminar",
                                            onClick = {
                                                viewModel.deleteExerciseFromRoutine(
                                                    routineId,
                                                    index
                                                )
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Ejercicio eliminado correctamente")
                                                }
                                            },
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            borderColor = Color.Red,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Card para añadir ejercicio
                    if (showAddCard) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                DropDownSelector(
                                    "Grupo Muscular",
                                    gruposMusculares,
                                    grupoMuscular
                                ) {
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

                                AnimatedAccessButton(buttonText = "Añadir ejercicio") {
                                    val errorNombre = nombreEjercicio.isBlank()
                                    val errorGrupo = grupoMuscular.isBlank()
                                    val errorTipo = tipo.isBlank()
                                    val errorIntensidad = intensidad.isBlank()

                                    showNombreError = errorNombre
                                    showGrupoError = errorGrupo
                                    showTipoError = errorTipo
                                    showIntensidadError = errorIntensidad

                                    if (errorNombre || errorGrupo || errorTipo || errorIntensidad) {
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
                                            duracion = if (isCardio) duracion.toIntOrNull()
                                                ?: 0 else 0,
                                            intensidad = intensidad
                                        )
                                        viewModel.addExerciseToRoutine(
                                            routineId,
                                            newExercise
                                        ) { success ->
                                            if (success) {
                                                viewModel.getUserRoutines { list ->
                                                    routine =
                                                        list.find { it.first == routineId }?.second
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
                                // 🔴 Botón de cancelar (ahora está DENTRO de la card)
                                AnimatedAccessButton(
                                    buttonText = "Cancelar",
                                    onClick = { showAddCard = false },
                                    containerColor = Color.Red,
                                    contentColor = Color.White,
                                    borderColor = Color.Red,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                )
                            }
                        }
                    }

                    if (!showAddCard){
                        // Botón principal
                        AnimatedAccessButton(
                            buttonText = "Añadir ejercicio",
                            onClick = { showAddCard = !showAddCard },
                            containerColor = Color.Black,
                            contentColor = Color.White,
                            borderColor = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(100.dp)) // 👈 Añade este al final
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

