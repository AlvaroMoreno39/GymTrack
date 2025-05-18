package com.example.gymtrack.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterRoutineScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

    val gruposMusculares =
        listOf("Pecho", "Espalda", "Piernas", "Hombros", "Bíceps", "Tríceps", "Abdomen")
    val tipos = listOf("Fuerza", "Cardio", "Mixto")
    val intensidades = listOf("Baja", "Media", "Alta")

    // Estados de error visual
    var showNombreRutinaError by remember { mutableStateOf(false) }
    var showNombreEjercicioError by remember { mutableStateOf(false) }
    var showGrupoMuscularError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // ← Fondo blanco
        ) {

            // Cabecera animada
            AnimatedEntrance {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.registerroutine),
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
                            "Crea tu",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            "nueva rutina",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Formulario animado
            AnimatedEntrance {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo nombre de rutina
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
                        Text(
                            text = "Introduce el nombre de la rutina",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    Text(text = "Añadir ejercicio", style = MaterialTheme.typography.titleMedium)

                    // Campo nombre del ejercicio
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
                        Text(
                            text = "Introduce el nombre del ejercicio",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Selector grupo muscular
                    DropDownSelector(
                        label = "Grupo Muscular",
                        options = gruposMusculares,
                        selectedOption = grupoMuscular,
                        onOptionSelected = {
                            grupoMuscular = it
                            showGrupoMuscularError = false
                        }
                    )
                    if (showGrupoMuscularError) {
                        Text(
                            text = "Selecciona un grupo muscular",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Selector tipo ejercicio
                    DropDownSelector(
                        label = "Tipo de Ejercicio",
                        options = tipos,
                        selectedOption = tipo,
                        onOptionSelected = {
                            tipo = it
                            showTipoError = false
                        }
                    )
                    if (showTipoError) {
                        Text(
                            text = "Selecciona un tipo de ejercicio",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Campo duración o series + reps según tipo
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

                    // Selector intensidad
                    DropDownSelector(
                        label = "Intensidad",
                        options = intensidades,
                        selectedOption = intensidad,
                        onOptionSelected = {
                            intensidad = it
                            showIntensidadError = false
                        }
                    )
                    if (showIntensidadError) {
                        Text(
                            text = "Selecciona la intensidad",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    // Botón añadir ejercicio
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
                            scope.launch {
                                snackbarHostState.showSnackbar("Rellena todos los campos del ejercicio")
                            }
                        } else {
                            val nuevoEjercicio = Exercise(
                                nombre = nombreEjercicio,
                                grupoMuscular = grupoMuscular,
                                tipo = tipo.lowercase(),
                                series = series.toIntOrNull() ?: 0,
                                reps = reps.toIntOrNull() ?: 0,
                                duracion = if (isCardio) duracion.toIntOrNull() ?: 0 else 0,
                                intensidad = intensidad.lowercase(),
                            )
                            ejercicios.add(nuevoEjercicio)

                            // Reset
                            nombreEjercicio = ""
                            grupoMuscular = ""
                            tipo = ""
                            series = ""
                            reps = ""
                            duracion = ""
                            intensidad = ""

                            scope.launch {
                                snackbarHostState.showSnackbar("Ejercicio añadido correctamente")
                            }
                        }
                    }

                    Text(
                        text = "Ejercicios añadidos: ${ejercicios.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    // Botón guardar rutina
                    AnimatedAccessButton(buttonText = "Guardar rutina completa", modifier = Modifier.fillMaxWidth()) {
                        val errorNombreRutina = nombreRutina.isBlank()
                        showNombreRutinaError = errorNombreRutina

                        if (errorNombreRutina || ejercicios.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Añade un nombre de rutina y al menos un ejercicio")
                            }
                        } else {
                            val esAdmin =
                                FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

                            val guardarRutina = if (esAdmin) {
                                viewModel::savePredefinedRoutine
                            } else {
                                viewModel::saveFullRoutine
                            }

                            guardarRutina(nombreRutina, ejercicios) { success ->
                                scope.launch {
                                    if (success) {
                                        snackbarHostState.showSnackbar("Rutina guardada con éxito")
                                        nombreRutina = ""
                                        ejercicios = mutableListOf()
                                    } else {
                                        snackbarHostState.showSnackbar("Error al guardar la rutina")
                                    }
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(100.dp)) // Espacio extra para no solapar el menú inferior
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
