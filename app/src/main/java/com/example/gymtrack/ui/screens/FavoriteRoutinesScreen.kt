package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.example.gymtrack.ui.theme.FavoriteYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * FavoriteRoutinesScreen.kt
 *
 * Pantalla que muestra todas las rutinas marcadas como favoritas por el usuario en la app GymTrack.
 * Permite visualizar, desmarcar/marcar como favorito y navegar al detalle de cada rutina.
 * Utiliza Jetpack Compose y el patrón MVVM con RoutineViewModel.
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteRoutinesScreen(
    viewModel: RoutineViewModel = viewModel(),     // ViewModel que maneja las rutinas
    navController: NavHostController               // Controlador de navegación entre pantallas
) {
    val scope = rememberCoroutineScope()                       // Scope para lanzar corrutinas (ej. Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }   // Estado del Snackbar para mensajes flotantes
    var favorites by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) } // Lista de favoritas

    // Al iniciar la pantalla, cargamos las rutinas favoritas del usuario
    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { allRoutines ->
            favorites = allRoutines.filter { it.second.esFavorita } // Filtramos solo las favoritas
            if (favorites.isEmpty()) {
                scope.launch {
                    snackbarHostState.showSnackbar("No tienes rutinas favoritas aún ⭐")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { FancySnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Cabecera de la pantalla con imagen, título y subtítulo
            ScreenHeader(
                image = R.drawable.favorite_routines,
                title = "Tus elegidas",
                subtitle = "Rutinas que te motivan"
            )

            // Lista scrollable de rutinas favoritas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(favorites, key = { it.first }) { (id_rutina, rutina) ->
                    var isFavorite by remember { mutableStateOf(rutina.esFavorita) } // Estado local para icono
                    var visible by remember { mutableStateOf(true) }                 // Controla visibilidad (animación al eliminar)
                    val animatedColor by animateColorAsState(
                        targetValue = if (isFavorite) FavoriteYellow else Color.LightGray,
                        animationSpec = tween(durationMillis = 300)
                    )

                    AnimatedVisibility(
                        visible = visible,
                        exit = slideOutVertically(tween(400)) + fadeOut(tween(300)) // Animación al eliminar de favoritos
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // Botón para marcar/desmarcar favorito
                                    IconToggleButton(
                                        checked = isFavorite,
                                        onCheckedChange = { newValue ->
                                            isFavorite = newValue
                                            viewModel.toggleFavorite(id_rutina, newValue) { success ->
                                                if (success) {
                                                    if (!newValue) {
                                                        // Si se quita de favoritos, animamos y actualizamos lista
                                                        visible = false
                                                        scope.launch {
                                                            delay(400)
                                                            viewModel.getUserRoutines { updated ->
                                                                favorites = updated.filter { it.second.esFavorita }
                                                            }
                                                            snackbarHostState.showSnackbar("Eliminada de favoritos ❌")
                                                        }
                                                    } else {
                                                        viewModel.getUserRoutines { updated ->
                                                            favorites = updated.filter { it.second.esFavorita }
                                                        }
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Añadida a favoritos ⭐")
                                                        }
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

                                    // Contenido textual de la tarjeta: nombre, ejercicios y botón
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

                                        // Botón para navegar al detalle de la rutina
                                        AnimatedAccessButton(
                                            buttonText = "Ver rutina",
                                            color = MaterialTheme.colorScheme.onBackground,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                            height = 50.dp,
                                            fontSize = 16.sp,
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                navController.navigate(
                                                    Screen.RoutineDetail.createRoute(id_rutina)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                // Espacio final para evitar que el último ítem quede pegado al borde inferior
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

