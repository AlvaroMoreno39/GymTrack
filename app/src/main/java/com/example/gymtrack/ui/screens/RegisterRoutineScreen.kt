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
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.AnimatedEntrance
import com.example.gymtrack.ui.components.DropDownSelector
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/*
RegisterRoutineScreen.kt

Pantalla para registrar nuevas rutinas en GymTrack.
Permite a usuarios normales crear rutinas personalizadas y, si eres administrador, crear rutinas predefinidas para todos los usuarios.
Incluye:
- Formulario dinámico para crear ejercicios (con validaciones por campo).
- Añadir varios ejercicios a una rutina antes de guardarla.
- Selección de nivel de dificultad (solo admin).
- Feedback visual inmediato por Snackbar.
- Diseño consistente, moderno y profesional.

Esta pantalla es clave para el sistema de rutinas personalizadas y predefinidas de la app.
*/

@Composable
fun RegisterRoutineScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Determina si el usuario actual es administrador (basado en su correo)
    val esAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    // Listas de opciones para los drop-down
    val niveles = listOf("Principiante", "Intermedio", "Avanzado")
    val gruposMusculares = listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")

    // Estados del formulario (nivel y rutina)
    var nivelSeleccionado by remember { mutableStateOf("") }
    var showNivelError by remember { mutableStateOf(false) }
    var nombreRutina by remember { mutableStateOf("") }

    // Estados para los campos del ejercicio
    var nombreEjercicio by remember { mutableStateOf("") }
    var grupoMuscular by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }

    // Lista de ejercicios añadidos hasta ahora
    var ejercicios by remember { mutableStateOf(mutableListOf<Exercise>()) }

    // Determina si es un ejercicio de cardio (cambia campos visibles)
    val isCardio = tipo.lowercase() == "cardio"

    // Estados de error por campo
    var showNombreRutinaError by remember { mutableStateOf(false) }
    var showNombreEjercicioError by remember { mutableStateOf(false) }
    var showGrupoMuscularError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }
    var showSeriesError by remember { mutableStateOf(false) }
    var showRepsError by remember { mutableStateOf(false) }
    var showDuracionError by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Encabezado de pantalla con imagen y título
            ScreenHeader(
                image = R.drawable.register_routine2,
                title = if (esAdmin) "Crea una rutina predefinida" else "Diseña tu progreso",
                subtitle = if (esAdmin) "Crea una rutina para todos" else "Crea tu rutina a medida"
            )

            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo: Nombre de la rutina
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

                    // Campo: Nivel de dificultad (solo admin)
                    if (esAdmin) {
                        DropDownSelector(
                            label = "Nivel de dificultad",
                            options = niveles,
                            selectedOption = nivelSeleccionado,
                            onOptionSelected = {
                                nivelSeleccionado = it
                                showNivelError = false
                            },
                            isError = showNivelError
                        )
                        if (showNivelError) {
                            Text("Selecciona un nivel", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    // Sección para añadir ejercicios
                    Text("Añadir ejercicio", style = MaterialTheme.typography.titleMedium)

                    // Campo: Nombre del ejercicio
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

                    // Drop-down: Grupo muscular
                    DropDownSelector(
                        label = "Grupo Muscular",
                        options = gruposMusculares,
                        selectedOption = grupoMuscular,
                        onOptionSelected = {
                            grupoMuscular = it
                            showGrupoMuscularError = false
                        },
                        isError = showGrupoMuscularError
                    )
                    if (showGrupoMuscularError) {
                        Text("Selecciona un grupo muscular", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    // Drop-down: Tipo de ejercicio
                    DropDownSelector(
                        label = "Tipo de Ejercicio",
                        options = tipos,
                        selectedOption = tipo,
                        onOptionSelected = {
                            tipo = it
                            showTipoError = false
                        },
                        isError = showTipoError
                    )
                    if (showTipoError) {
                        Text("Selecciona un tipo de ejercicio", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    // Campos numéricos: según tipo (cardio usa duración, fuerza usa series y reps)
                    if (isCardio) {
                        OutlinedTextField(
                            value = duracion,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) duracion = value
                                showDuracionError = false
                            },
                            label = { Text("Duración (minutos)") },
                            isError = showDuracionError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showDuracionError) {
                            Text("La duración debe ser mayor a 0", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    } else {
                        OutlinedTextField(
                            value = series,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) series = value
                                showSeriesError = false
                            },
                            label = { Text("Series") },
                            isError = showSeriesError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showSeriesError) {
                            Text("Las series deben ser mayores a 0", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }

                        OutlinedTextField(
                            value = reps,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) reps = value
                                showRepsError = false
                            },
                            label = { Text("Repeticiones") },
                            isError = showRepsError,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showRepsError) {
                            Text("Las repeticiones deben ser mayores a 0", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                    }

                    // Drop-down: Intensidad del ejercicio
                    DropDownSelector(
                        label = "Intensidad",
                        options = intensidades,
                        selectedOption = intensidad,
                        onOptionSelected = {
                            intensidad = it
                            showIntensidadError = false
                        },
                        isError = showIntensidadError
                    )
                    if (showIntensidadError) {
                        Text("Selecciona la intensidad", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                    }

                    // Botón para añadir ejercicio a la lista
                    AnimatedAccessButton(buttonText = "Añadir ejercicio", modifier = Modifier.fillMaxWidth()) {
                        val errorNombreEjercicio = nombreEjercicio.isBlank()
                        val errorGrupoMuscular = grupoMuscular.isBlank()
                        val errorTipo = tipo.isBlank()
                        val errorIntensidad = intensidad.isBlank()
                        val errorSeries = !isCardio && (series.toIntOrNull() == null || series.toInt() <= 0)
                        val errorReps = !isCardio && (reps.toIntOrNull() == null || reps.toInt() <= 0)
                        val errorDuracion = isCardio && (duracion.toIntOrNull() == null || duracion.toInt() <= 0)

                        showNombreEjercicioError = errorNombreEjercicio
                        showGrupoMuscularError = errorGrupoMuscular
                        showTipoError = errorTipo
                        showIntensidadError = errorIntensidad
                        showSeriesError = errorSeries
                        showRepsError = errorReps
                        showDuracionError = errorDuracion

                        if (errorNombreEjercicio || errorGrupoMuscular || errorTipo || errorIntensidad || errorSeries || errorReps || errorDuracion) {
                            scope.launch {
                                snackbarHostState.showSnackbar("⚠️ Rellena todos los campos obligatorios y asegúrate de que series, repeticiones o duración sean mayores que 0")
                            }
                            return@AnimatedAccessButton
                        }

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
                        scope.launch { snackbarHostState.showSnackbar("✅ Ejercicio añadido correctamente") }
                    }

                    // Indicador del número de ejercicios añadidos
                    Text("Ejercicios añadidos: ${ejercicios.size}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.CenterHorizontally))

                    // Botón para guardar la rutina completa
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
                                        } else {
                                            snackbarHostState.showSnackbar("Error al guardar la rutina ❌")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp)) // Espacio final para evitar que el último botón quede pegado abajo
                }
            }
        }
    }
}


