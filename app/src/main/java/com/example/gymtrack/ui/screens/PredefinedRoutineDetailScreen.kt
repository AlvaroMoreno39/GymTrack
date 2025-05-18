package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PredefinedRoutineDetailScreen(
    navController: NavHostController,
    viewModel: RoutineViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val rutina = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<RoutineData>("predefined_routine")

    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    var showAddCard by remember { mutableStateOf(false) }

    // Estados para nuevo ejercicio
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

    val gruposMusculares = listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")
    val isCardio = tipo.lowercase() == "cardio"

    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) {
        rutina?.let { rutina ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // 📷 Cabecera visual sin padding lateral
                AnimatedEntrance {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.predefined_routine),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .align(Alignment.BottomCenter)
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text("Rutina", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text(rutina.nombreRutina, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(rutina.ejercicios) { index, ejercicio ->
                        var editing by remember { mutableStateOf(false) }
                        var ejercicioEditable by remember { mutableStateOf(ejercicio) }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 10.dp,
                                    bottom = 6.dp
                                )
                            ) {
                                Text("• ${ejercicio.nombre}", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                if (isAdmin && editing) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = ejercicioEditable.nombre,
                                            onValueChange = { ejercicioEditable = ejercicioEditable.copy(nombre = it) },
                                            label = { Text("Nombre") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        DropDownSelector("Grupo", gruposMusculares, ejercicioEditable.grupoMuscular) {
                                            ejercicioEditable = ejercicioEditable.copy(grupoMuscular = it)
                                        }
                                        DropDownSelector("Tipo", tipos, ejercicioEditable.tipo) {
                                            ejercicioEditable = ejercicioEditable.copy(tipo = it)
                                        }
                                        if (ejercicioEditable.tipo.lowercase() == "cardio") {
                                            OutlinedTextField(
                                                value = ejercicioEditable.duracion.toString(),
                                                onValueChange = {
                                                    ejercicioEditable = ejercicioEditable.copy(duracion = it.toIntOrNull() ?: 0)
                                                },
                                                label = { Text("Duración (min)") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        } else {
                                            OutlinedTextField(
                                                value = ejercicioEditable.series.toString(),
                                                onValueChange = {
                                                    ejercicioEditable = ejercicioEditable.copy(series = it.toIntOrNull() ?: 0)
                                                },
                                                label = { Text("Series") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            OutlinedTextField(
                                                value = ejercicioEditable.reps.toString(),
                                                onValueChange = {
                                                    ejercicioEditable = ejercicioEditable.copy(reps = it.toIntOrNull() ?: 0)
                                                },
                                                label = { Text("Reps") },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        DropDownSelector("Intensidad", intensidades, ejercicioEditable.intensidad) {
                                            ejercicioEditable = ejercicioEditable.copy(intensidad = it)
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Grupo: ${ejercicio.grupoMuscular}")
                                        Text("Tipo: ${ejercicio.tipo}")
                                        if (ejercicio.series > 0) Text("Series: ${ejercicio.series}")
                                        if (ejercicio.reps > 0) Text("Reps: ${ejercicio.reps}")
                                        if (ejercicio.duracion > 0) Text("Duración: ${ejercicio.duracion} min")
                                        Text("Intensidad: ${ejercicio.intensidad}")
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (isAdmin) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        AnimatedAccessButton(
                                            buttonText = if (editing) "Guardar" else "Editar",
                                            onClick = { editing = !editing },
                                            color = MaterialTheme.colorScheme.onBackground,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        if (!editing) {
                                            AnimatedAccessButton(
                                                buttonText = "Eliminar",
                                                onClick = {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Ejercicio eliminado")
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
                    }

                    if (isAdmin) {
                        item {
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
                                            label = { Text("Nombre") },
                                            isError = showNombreError,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        DropDownSelector("Grupo", gruposMusculares, grupoMuscular) {
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

                                        DropDownSelector("Intensidad", intensidades, intensidad) {
                                            intensidad = it
                                            showIntensidadError = false
                                        }

                                        AnimatedAccessButton(buttonText = "Añadir ejercicio") {
                                            val errores = listOf(
                                                nombreEjercicio.isBlank(),
                                                grupoMuscular.isBlank(),
                                                tipo.isBlank(),
                                                intensidad.isBlank()
                                            )

                                            if (errores.any { it }) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Rellena todos los campos obligatorios")
                                                }
                                            } else {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Ejercicio añadido (mock)")
                                                }
                                            }
                                        }

                                        AnimatedAccessButton(
                                            buttonText = "Cancelar",
                                            onClick = { showAddCard = false },
                                            color = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error	),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            if (!showAddCard) {
                                AnimatedAccessButton(
                                    buttonText = "Añadir ejercicio",
                                    onClick = { showAddCard = !showAddCard },
                                    color = MaterialTheme.colorScheme.onBackground,
                                    contentColor = MaterialTheme.colorScheme.background,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
