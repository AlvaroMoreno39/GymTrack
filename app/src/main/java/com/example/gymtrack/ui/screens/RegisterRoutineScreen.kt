package com.example.gymtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RegisterRoutineScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val esAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    val niveles = listOf("Principiante", "Intermedio", "Avanzado")
    var nivelSeleccionado by remember { mutableStateOf("") }
    var showNivelError by remember { mutableStateOf(false) }

    var nombreRutina by remember { mutableStateOf("") }
    var nombreEjercicio by remember { mutableStateOf("") }
    var grupoMuscular by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }

    var ejercicios by remember { mutableStateOf(mutableListOf<Exercise>()) }

    val isCardio = tipo.lowercase() == "cardio"

    val gruposMusculares = listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")

    var showNombreRutinaError by remember { mutableStateOf(false) }
    var showNombreEjercicioError by remember { mutableStateOf(false) }
    var showGrupoMuscularError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            ScreenHeader(
                image = R.drawable.register_routine2,
                title = "Diseña tu progreso",
                subtitle = "Crea tu rutina a medida"
            )


            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nombreRutina,
                        onValueChange = {
                            nombreRutina = it
                            showNombreRutinaError = false
                        },
                        label = { Text("Nombre de la rutina") },
                        isError = showNombreRutinaError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showNombreRutinaError) {
                        Text("Introduce el nombre de la rutina", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    if (esAdmin) {
                        DropDownSelector(
                            label = "Nivel de dificultad",
                            options = niveles,
                            selectedOption = nivelSeleccionado,
                            onOptionSelected = {
                                nivelSeleccionado = it
                                showNivelError = false
                            }
                        )
                        if (showNivelError) {
                            Text("Selecciona un nivel", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    Text("Añadir ejercicio", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = nombreEjercicio,
                        onValueChange = {
                            nombreEjercicio = it
                            showNombreEjercicioError = false
                        },
                        label = { Text("Nombre del ejercicio") },
                        isError = showNombreEjercicioError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showNombreEjercicioError) {
                        Text("Introduce el nombre del ejercicio", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    DropDownSelector("Grupo Muscular", gruposMusculares, grupoMuscular) {
                        grupoMuscular = it
                        showGrupoMuscularError = false
                    }
                    if (showGrupoMuscularError) {
                        Text("Selecciona un grupo muscular", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    DropDownSelector("Tipo de Ejercicio", tipos, tipo) {
                        tipo = it
                        showTipoError = false
                    }
                    if (showTipoError) {
                        Text("Selecciona un tipo de ejercicio", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    if (isCardio) {
                        OutlinedTextField(
                            value = duracion,
                            onValueChange = { duracion = it },
                            label = { Text("Duración (minutos)") },
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
                    if (showIntensidadError) {
                        Text("Selecciona la intensidad", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    AnimatedAccessButton(buttonText = "Añadir ejercicio", modifier = Modifier.fillMaxWidth()) {
                        val errorNombreEjercicio = nombreEjercicio.isBlank()
                        val errorGrupoMuscular = grupoMuscular.isBlank()
                        val errorTipo = tipo.isBlank()
                        val errorIntensidad = intensidad.isBlank()

                        showNombreEjercicioError = errorNombreEjercicio
                        showGrupoMuscularError = errorGrupoMuscular
                        showTipoError = errorTipo
                        showIntensidadError = errorIntensidad

                        if (errorNombreEjercicio || errorGrupoMuscular || errorTipo || errorIntensidad) {
                            scope.launch { snackbarHostState.showSnackbar("Rellena todos los campos del ejercicio ⚠️") }
                        } else {
                            ejercicios.add(
                                Exercise(
                                    nombre = nombreEjercicio,
                                    grupoMuscular = grupoMuscular,
                                    tipo = tipo.lowercase(),
                                    series = series.toIntOrNull() ?: 0,
                                    reps = reps.toIntOrNull() ?: 0,
                                    duracion = if (isCardio) duracion.toIntOrNull() ?: 0 else 0,
                                    intensidad = intensidad.lowercase()
                                )
                            )
                            nombreEjercicio = ""
                            grupoMuscular = ""
                            tipo = ""
                            series = ""
                            reps = ""
                            duracion = ""
                            intensidad = ""
                            scope.launch { snackbarHostState.showSnackbar("Ejercicio añadido correctamente ✅") }
                        }
                    }

                    Text("Ejercicios añadidos: ${ejercicios.size}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.CenterHorizontally))

                    AnimatedAccessButton(buttonText = "Guardar rutina completa", modifier = Modifier.fillMaxWidth()) {
                        val errorNombreRutina = nombreRutina.isBlank()
                        showNombreRutinaError = errorNombreRutina
                        showNivelError = esAdmin && nivelSeleccionado.isBlank()

                        if (errorNombreRutina || ejercicios.isEmpty() || showNivelError) {
                            scope.launch { snackbarHostState.showSnackbar("Rellena todos los campos obligatorios ⚠️") }
                        } else {
                            if (esAdmin) {
                                viewModel.savePredefinedRoutine(nombreRutina, ejercicios, nivelSeleccionado) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Rutina guardada con éxito ✅")
                                            nombreRutina = ""
                                            ejercicios = mutableListOf()
                                            nivelSeleccionado = ""
                                            nombreEjercicio = ""
                                            grupoMuscular = ""
                                            tipo = ""
                                            series = ""
                                            reps = ""
                                            duracion = ""
                                            intensidad = ""
                                        } else {
                                            snackbarHostState.showSnackbar("Error al guardar la rutina ❌")
                                        }
                                    }
                                }
                            } else {
                                viewModel.saveFullRoutine(nombreRutina, ejercicios) { success ->
                                    scope.launch {
                                        if (success) {
                                            snackbarHostState.showSnackbar("Rutina guardada con éxito ✅")
                                            nombreRutina = ""
                                            ejercicios = mutableListOf()
                                            nombreEjercicio = ""
                                            grupoMuscular = ""
                                            tipo = ""
                                            series = ""
                                            reps = ""
                                            duracion = ""
                                            intensidad = ""
                                        } else {
                                            snackbarHostState.showSnackbar("Error al guardar la rutina ❌")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
