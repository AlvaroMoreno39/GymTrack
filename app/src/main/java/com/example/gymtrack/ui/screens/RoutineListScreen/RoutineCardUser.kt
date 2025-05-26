package com.example.gymtrack.ui.screens.RoutineListScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.theme.FavoriteYellow
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * RoutineCardUser.kt
 *
 * Componente visual para mostrar una tarjeta individual de rutina del usuario en GymTrack.
 * Incluye:
 * - Título de la rutina, número de ejercicios.
 * - Botón estrella para marcar/desmarcar como favorita (con animación de color).
 * - Botón para navegar al detalle de la rutina.
 * - Botón para eliminar la rutina (con feedback por Snackbar y actualización de lista).
 *
 * Usado en RoutineListScreen para renderizar cada entrada de rutina personalizada del usuario.
 * Implementado en Jetpack Compose, combinando animaciones, Card y controles interactivos.
 */

@Composable
fun RoutineCardUser(
    id_rutina: String,                            // ID único de la rutina (documento en Firebase)
    rutina: RoutineData,                          // Datos de la rutina (nombre, ejercicios, etc.)
    navController: NavHostController,             // Navegador para ir al detalle
    viewModel: RoutineViewModel,                  // ViewModel para actualizar favoritos o eliminar
    snackbarHostState: SnackbarHostState,         // Snackbar para feedback visual
    scope: CoroutineScope,                        // Scope para lanzar corrutinas (Snackbar, delays)
    onDeleted: () -> Unit                         // Callback que se llama cuando se elimina la rutina
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // ⭐ Estrella con animación solo de color
                var isFavorite by remember { mutableStateOf(rutina.esFavorita) }
                val animatedColor by animateColorAsState(
                    targetValue = if (isFavorite) FavoriteYellow else Color.LightGray,
                    animationSpec = tween(durationMillis = 300)
                )

                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { newValue ->
                        isFavorite = newValue
                        viewModel.toggleFavorite(id_rutina, newValue) { success ->
                            if (success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (newValue) "Añadida a favoritos ⭐" else "Eliminada de favoritos ❌"
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al actualizar favorito ❌")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 12.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorita",
                        tint = animatedColor,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Column(modifier = Modifier.padding(10.dp)) {
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnimatedAccessButton(
                            buttonText = "Ver rutina",
                            color = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                            height = 50.dp,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                navController.navigate(Screen.RoutineDetail.createRoute(id_rutina))
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        AnimatedAccessButton(
                            buttonText = "Eliminar",
                            color = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.background,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                            height = 50.dp,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                viewModel.deleteRoutine(id_rutina) { success ->
                                    if (success) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Rutina eliminada ✅")
                                        }
                                        onDeleted()
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error al eliminar ❌")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}



