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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.screens.DropDownSelector

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
    onAceptar: () -> Unit
) {
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
            DropDownSelector("Grupo Muscular", gruposMusculares, grupo, onGrupoChange)
            DropDownSelector("Tipo", tipos, tipo, onTipoChange)

            if (isCardio) {
                OutlinedTextField(
                    value = duracion,
                    onValueChange = onDuracionChange,
                    label = { Text("Duración (min)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = series,
                    onValueChange = onSeriesChange,
                    label = { Text("Series") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = onRepsChange,
                    label = { Text("Repeticiones") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DropDownSelector("Intensidad", intensidades, intensidad, onIntensidadChange)

            AnimatedAccessButton(
                buttonText = "Añadir ejercicio",
                modifier = Modifier.fillMaxWidth(),
                onClick = onAceptar
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
