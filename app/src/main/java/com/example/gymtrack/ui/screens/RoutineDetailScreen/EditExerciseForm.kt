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
import com.example.gymtrack.ui.screens.DropDownSelector
import com.example.gymtrack.viewmodel.Exercise

@Composable
fun EditExerciseForm(
    initial: Exercise,
    gruposMusculares: List<String>,
    tipos: List<String>,
    intensidades: List<String>,
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

    val isCardio = tipo.lowercase() == "cardio"

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
        DropDownSelector("Grupo Muscular", gruposMusculares, grupo) {
            grupo = it; showGrupoError = false
        }
        DropDownSelector("Tipo", tipos, tipo) {
            tipo = it; showTipoError = false
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
            intensidad = it; showIntensidadError = false
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            AnimatedAccessButton(
                buttonText = "Guardar",
                onClick = {
                    val errores = listOf(
                        nombre.isBlank(),
                        grupo.isBlank(),
                        tipo.isBlank(),
                        intensidad.isBlank(),
                        if (isCardio) duracion.isBlank() else series.isBlank() || reps.isBlank()
                    )
                    showNombreError = nombre.isBlank()
                    showGrupoError = grupo.isBlank()
                    showTipoError = tipo.isBlank()
                    showIntensidadError = intensidad.isBlank()
                    if (errores.any { it }) return@AnimatedAccessButton

                    val ejercicioEditado = Exercise(
                        nombre = nombre,
                        grupoMuscular = grupo,
                        tipo = tipo,
                        series = if (!isCardio) series.toIntOrNull() ?: 0 else 0,
                        reps = if (!isCardio) reps.toIntOrNull() ?: 0 else 0,
                        duracion = if (isCardio) duracion.toIntOrNull() ?: 0 else 0,
                        intensidad = intensidad
                    )
                    onSave(ejercicioEditado)
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
