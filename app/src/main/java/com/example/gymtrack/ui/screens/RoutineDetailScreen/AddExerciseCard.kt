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
import com.example.gymtrack.viewmodel.Exercise
import kotlinx.coroutines.launch

/**
 * AddExerciseCard.kt
 *
 * Composable para añadir un nuevo ejercicio en la app GymTrack.
 *
 * Permite:
 * - Introducir nombre, grupo muscular, tipo, intensidad.
 * - Según el tipo (fuerza/cardio), pedir series y reps o duración.
 * - Validar todos los campos antes de aceptar, mostrando errores visuales en rojo si están vacíos o incorrectos.
 * - Cancelar la operación si el usuario cambia de idea.
 *
 * Usa:
 * - DropDownSelector para los campos de selección.
 * - AnimatedAccessButton para botones estilizados.
 * - Snackbar para feedback visual de errores.
 */

@Composable
fun AddExerciseCard(
    gruposMusculares: List<String>,              // Opciones de grupo muscular
    tipos: List<String>,                         // Opciones de tipo
    intensidades: List<String>,                  // Opciones de intensidad
    onAceptar: (Exercise) -> Unit,               // Callback al aceptar/guardar (recibe objeto Exercise)
    onCancelar: () -> Unit,                      // Callback al cancelar
    snackbarHostState: SnackbarHostState         // Snackbar para mostrar feedback
) {
    val coroutineScope = rememberCoroutineScope()

    // Estados internos de los campos
    var nombre by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var intensidad by remember { mutableStateOf("") }

    val isCardio = tipo.lowercase() == "cardio"

    // Estados de error internos (locales)
    var showNombreError by remember { mutableStateOf(false) }
    var showGrupoError by remember { mutableStateOf(false) }
    var showTipoError by remember { mutableStateOf(false) }
    var showIntensidadError by remember { mutableStateOf(false) }
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
            // Campo: Nombre del ejercicio
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; showNombreError = false },
                label = { Text("Nombre del ejercicio") },
                isError = showNombreError,
                modifier = Modifier.fillMaxWidth()
            )

            // Selector: Grupo muscular
            DropDownSelector(
                label = "Grupo Muscular",
                options = gruposMusculares,
                selectedOption = grupo,
                onOptionSelected = { grupo = it; showGrupoError = false },
                isError = showGrupoError
            )

            // Selector: Tipo
            DropDownSelector(
                label = "Tipo",
                options = tipos,
                selectedOption = tipo,
                onOptionSelected = {
                    tipo = it
                    showTipoError = false
                    // Resetea campos al cambiar tipo
                    if (tipo.lowercase() == "cardio") {
                        series = ""
                        reps = ""
                    } else {
                        duracion = ""
                    }
                },
                isError = showTipoError
            )

            // Según tipo, muestra duración (cardio) o series/reps (fuerza)
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

            // Selector: Intensidad
            DropDownSelector(
                label = "Intensidad",
                options = intensidades,
                selectedOption = intensidad,
                onOptionSelected = { intensidad = it; showIntensidadError = false },
                isError = showIntensidadError
            )

            // Botón: Añadir ejercicio (valida todos los campos)
            AnimatedAccessButton(
                buttonText = "Añadir ejercicio",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // Validaciones
                    val errorNombre = nombre.isBlank()
                    val errorGrupo = grupo.isBlank()
                    val errorTipo = tipo.isBlank()
                    val errorIntensidad = intensidad.isBlank()
                    val errorSeries = !isCardio && (series.toIntOrNull() == null || series.toInt() <= 0)
                    val errorReps = !isCardio && (reps.toIntOrNull() == null || reps.toInt() <= 0)
                    val errorDuracion = isCardio && (duracion.toIntOrNull() == null || duracion.toInt() <= 0)

                    // Activar errores visuales
                    showNombreError = errorNombre
                    showGrupoError = errorGrupo
                    showTipoError = errorTipo
                    showIntensidadError = errorIntensidad
                    showSeriesError = errorSeries
                    showRepsError = errorReps
                    showDuracionError = errorDuracion

                    if (errorNombre || errorGrupo || errorTipo || errorIntensidad || errorSeries || errorReps || errorDuracion) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ Completa todos los campos correctamente y usa valores mayores que 0")
                        }
                        return@AnimatedAccessButton
                    }

                    // Crear objeto Exercise y llamar callback onAceptar
                    val newExercise = Exercise(
                        nombre = nombre,
                        grupoMuscular = grupo,
                        tipo = tipo,
                        series = if (!isCardio) series.toInt() else 0,
                        reps = if (!isCardio) reps.toInt() else 0,
                        duracion = if (isCardio) duracion.toInt() else 0,
                        intensidad = intensidad
                    )
                    onAceptar(newExercise)

                    // Limpia los campos después de añadir
                    nombre = ""
                    grupo = ""
                    tipo = ""
                    series = ""
                    reps = ""
                    duracion = ""
                    intensidad = ""
                }
            )

            // Botón: Cancelar operación
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

