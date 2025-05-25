package com.example.gymtrack.ui.screens.RoutineDetailScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.DropDownSelector
import com.example.gymtrack.viewmodel.Exercise
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@Composable
fun EditExerciseForm(
    initial: Exercise,
    gruposMusculares: List<String>,
    tipos: List<String>,
    intensidades: List<String>,
    snackbarHostState: SnackbarHostState,
    onSave: (Exercise) -> Unit,
    onCancel: () -> Unit
) {
    var nombre by remember { mutableStateOf(initial.nombre) }
    var grupo by remember { mutableStateOf(initial.grupoMuscular) }
    var tipo by remember { mutableStateOf(initial.tipo) }
    var series by remember { mutableStateOf(if (initial.tipo.lowercase() != "cardio") initial.series.toString() else "") }
    var reps by remember { mutableStateOf(if (initial.tipo.lowercase() != "cardio") initial.reps.toString() else "") }
    var duracion by remember { mutableStateOf(if (initial.tipo.lowercase() == "cardio") initial.duracion.toString() else "") }
    var intensidad by remember { mutableStateOf(initial.intensidad) }

    var showNombreError by remember { mutableStateOf(false) }
    var showGrupoError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }
    var showSeriesError by remember { mutableStateOf(false) }
    var showRepsError by remember { mutableStateOf(false) }
    var showDuracionError by remember { mutableStateOf(false) }

    val isCardio = tipo.lowercase() == "cardio"
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tipo) {
        if (isCardio) {
            series = ""
            reps = ""
        } else {
            duracion = ""
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; showNombreError = false },
            label = { Text("Nombre del ejercicio") },
            isError = showNombreError,
            modifier = Modifier.fillMaxWidth()
        )
        DropDownSelector(
            label = "Grupo Muscular",
            options = gruposMusculares,
            selectedOption = grupo,
            onOptionSelected = {
                grupo = it
                showGrupoError = false
            },
            isError = showGrupoError
        )
        DropDownSelector(
            label = "Tipo",
            options = tipos,
            selectedOption = tipo,
            onOptionSelected = {
                tipo = it
                showTipoError = false
            },
            isError = showTipoError
        )
        if (isCardio) {
            OutlinedTextField(
                value = duracion,
                onValueChange = { duracion = it; showDuracionError = false },
                label = { Text("Duración (min)") },
                isError = showDuracionError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            OutlinedTextField(
                value = series,
                onValueChange = { series = it; showSeriesError = false },
                label = { Text("Series") },
                isError = showSeriesError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it; showRepsError = false },
                label = { Text("Repeticiones") },
                isError = showRepsError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

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

        Spacer(modifier = Modifier.height(2.dp))

        Row {
            AnimatedAccessButton(
                buttonText = "Guardar",
                onClick = {
                    val errorNombre = nombre.isBlank()
                    val errorGrupo = grupo.isBlank()
                    val errorTipo = tipo.isBlank()
                    val errorIntensidad = intensidad.isBlank()
                    val errorSeries = !isCardio && (series.toIntOrNull() == null || series.toInt() <= 0)
                    val errorReps = !isCardio && (reps.toIntOrNull() == null || reps.toInt() <= 0)
                    val errorDuracion = isCardio && (duracion.toIntOrNull() == null || duracion.toInt() <= 0)

                    showNombreError = errorNombre
                    showGrupoError = errorGrupo
                    showTipoError = errorTipo
                    showIntensidadError = errorIntensidad
                    showSeriesError = errorSeries
                    showRepsError = errorReps
                    showDuracionError = errorDuracion

                    if (errorNombre || errorGrupo || errorTipo || errorIntensidad || errorSeries || errorReps || errorDuracion) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ Completa todos los campos y usa valores mayores que 0")
                        }
                        return@AnimatedAccessButton
                    }

                    val ejercicioEditado = Exercise(
                        nombre = nombre,
                        grupoMuscular = grupo,
                        tipo = tipo,
                        series = if (!isCardio) series.toInt() else 0,
                        reps = if (!isCardio) reps.toInt() else 0,
                        duracion = if (isCardio) duracion.toInt() else 0,
                        intensidad = intensidad
                    )
                    onSave(ejercicioEditado)

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("✅ Ejercicio guardado con éxito")
                    }
                },
                color = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.weight(1f).height(50.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            AnimatedAccessButton(
                buttonText = "Cancelar",
                onClick = onCancel,
                color = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.background,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                modifier = Modifier.weight(1f).height(50.dp)
            )
        }
    }
}

