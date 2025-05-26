package com.example.gymtrack.ui.screens.RoutineListScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.theme.LightGray
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel

/**
 * RoutineCardPredefined.kt
 *
 * Componente visual para mostrar una tarjeta individual de rutina predefinida en GymTrack.
 * Incluye:
 * - Nombre de la rutina, número de ejercicios y etiqueta visual de nivel (principiante, intermedio, avanzado).
 * - Botón para ver los detalles de la rutina predefinida.
 * - Si el usuario es administrador:
 *      → muestra botón para eliminar la rutina predefinida.
 * - Si el usuario es normal:
 *      → muestra botón para añadir una copia de la rutina predefinida a sus rutinas personales.
 *
 * Usado en RoutineListScreen cuando se activan las rutinas predefinidas.
 * Implementado en Jetpack Compose combinando Card, animaciones y controles interactivos.
 */

@Composable
fun RoutineCardPredefined(
    rutina: RoutineData,                          // Datos de la rutina (nombre, nivel, ejercicios)
    isAdmin: Boolean,                             // Si el usuario es admin, puede eliminar; si no, puede añadir
    navController: NavHostController,             // Navegador para ir al detalle
    viewModel: RoutineViewModel,                  // ViewModel para operaciones (eliminar, añadir)
    onDeleted: () -> Unit,                        // Callback cuando se elimina una rutina
    onAdded: () -> Unit,                          // Callback cuando se añade una rutina al usuario
    onError: () -> Unit                           // Callback cuando ocurre un error en alguna operación
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = rutina.nombreRutina,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            rutina.nivel?.let { nivel ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = when (nivel.lowercase()) {
                                "principiante" -> Color(0xFFB2FF59)
                                "intermedio" -> Color(0xFFFFF176)
                                "avanzado" -> Color(0xFFFF8A65)
                                else -> Color.LightGray
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = nivel.replaceFirstChar { it.uppercase() },
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                color = LightGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnimatedAccessButton(
                    buttonText = "Ver rutina",
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("routine_arg", rutina)
                        navController.navigate("predefined_routine_detail")
                    },
                    color = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.weight(1f).height(50.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                if (isAdmin) {
                    AnimatedAccessButton(
                        buttonText = "Eliminar",
                        onClick = {
                            viewModel.deletePredefinedRoutine(rutina.nombreRutina) { success ->
                                if (success) onDeleted() else onError()
                            }
                        },
                        color = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f).height(50.dp)
                    )
                } else {
                    AnimatedAccessButton(
                        buttonText = "Añadir",
                        onClick = {
                            viewModel.copyPredefinedRoutineToUser(rutina.nombreRutina, rutina.ejercicios) { success ->
                                if (success) onAdded() else onError()
                            }
                        },
                        color = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.weight(1f).height(50.dp)
                    )
                }
            }
        }
    }
}

