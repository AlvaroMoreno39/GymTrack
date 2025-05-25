package com.example.gymtrack.ui.screens.RoutineDetailScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.DropDownSelector
import kotlinx.coroutines.launch

@Composable
fun AddExerciseCard(
    nombre: String,
    grupo: String,
    tipo: String,
    series: String,
    reps: String,
    duracion: String,
    intensidad: String,
    isCardio: Boolean,
    gruposMusculares: List<String>,
    tipos: List<String>,
    intensidades: List<String>,
    showNombreError: Boolean,
    showGrupoError: Boolean,
    showTipoError: Boolean,
    showIntensidadError: Boolean,
    onNombreChange: (String) -> Unit,
    onGrupoChange: (String) -> Unit,
    onTipoChange: (String) -> Unit,
    onDuracionChange: (String) -> Unit,
    onSeriesChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onIntensidadChange: (String) -> Unit,
    onCancelar: () -> Unit,
    onAceptar: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    var showSeriesError by remember { mutableStateOf(false) }
    var showRepsError by remember { mutableStateOf(false) }
    var showDuracionError by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre del ejercicio") },
                isError = showNombreError,
                modifier = Modifier.fillMaxWidth()
            )
            DropDownSelector(
                label = "Grupo Muscular",
                options = gruposMusculares,
                selectedOption = grupo,
                onOptionSelected = onGrupoChange,
                isError = showGrupoError
            )

            DropDownSelector(
                label = "Tipo",
                options = tipos,
                selectedOption = tipo,
                onOptionSelected = onTipoChange,
                isError = showTipoError
            )

            if (isCardio) {
                OutlinedTextField(
                    value = duracion,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración (min)") },
                    isError = showDuracionError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = series,
                    onValueChange = onSeriesChange,
                    label = { Text("Series") },
                    isError = showSeriesError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = onRepsChange,
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
                onOptionSelected = onIntensidadChange,
                isError = showIntensidadError
            )

            AnimatedAccessButton(
                buttonText = "Añadir ejercicio",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val errorNombre = nombre.isBlank()
                    val errorGrupo = grupo.isBlank()
                    val errorTipo = tipo.isBlank()
                    val errorIntensidad = intensidad.isBlank()
                    val errorSeries = !isCardio && (series.toIntOrNull() == null || series.toInt() <= 0)
                    val errorReps = !isCardio && (reps.toIntOrNull() == null || reps.toInt() <= 0)
                    val errorDuracion = isCardio && (duracion.toIntOrNull() == null || duracion.toInt() <= 0)

                    showSeriesError = errorSeries
                    showRepsError = errorReps
                    showDuracionError = errorDuracion

                    if (errorNombre || errorGrupo || errorTipo || errorIntensidad || errorSeries || errorReps || errorDuracion) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ Completa todos los campos y usa valores mayores que 0")
                        }
                        return@AnimatedAccessButton
                    }

                    onAceptar()
                }
            )

            AnimatedAccessButton(
                buttonText = "Cancelar",
                onClick = onCancelar,
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

