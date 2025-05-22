package com.example.gymtrack.ui.screens

import android.R.id
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import kotlinx.coroutines.delay
import com.example.gymtrack.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.FavoriteYellow
import com.example.gymtrack.ui.theme.LightGray
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
    viewModel: RoutineViewModel = viewModel(),   // ViewModel para manejar lógica de rutinas y favoritos
    navController: NavHostController             // Controlador de navegación para moverse entre pantallas
) {
    // Scope para lanzar corutinas en la UI (ej: mostrar Snackbars)
    val scope = rememberCoroutineScope()
    // Estado para manejar los mensajes emergentes visuales
    val snackbarHostState = remember { SnackbarHostState() }
    // Estado reactivo con la lista de rutinas favoritas (ID y datos de rutina)
    var favorites by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }

    // Lógica de carga inicial: obtiene las rutinas favoritas al montar la pantalla
    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { allRoutines ->
            favorites = allRoutines.filter { it.second.esFavorita }
            if (favorites.isEmpty()) {
                // Si no hay favoritas, muestra un mensaje con una estrella
                scope.launch {
                    snackbarHostState.showSnackbar("No tienes rutinas favoritas aún ⭐")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { FancySnackbarHost(snackbarHostState) } // Snackbar personalizado animado
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Cabecera visual común (imagen, título y subtítulo animados)
            ScreenHeader(
                image = R.drawable.favorite_routines,
                title = "Tus elegidas",
                subtitle = "Rutinas que te motivan"
            )

            // Lista animada de rutinas favoritas
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Renderiza cada rutina favorita como una Card
                    items(favorites, key = { it.first }) { (id_rutina, rutina) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // Botón de estrella para marcar/desmarcar favorito
                                    IconToggleButton(
                                        checked = rutina.esFavorita,
                                        onCheckedChange = { isFavorite ->
                                            viewModel.toggleFavorite(id_rutina, isFavorite) { success ->
                                                if (success) {
                                                    // Si se marca/desmarca, recarga la lista filtrando solo favoritas
                                                    viewModel.getUserRoutines { updated ->
                                                        favorites = updated.filter { it.second.esFavorita }
                                                    }
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (isFavorite) "Añadida a favoritos ⭐" else "Eliminada de favoritos ❌"
                                                        )
                                                    }
                                                } else {
                                                    // Error al actualizar favorito
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
                                            imageVector = if (rutina.esFavorita) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = "Favorita",
                                            tint = if (rutina.esFavorita) FavoriteYellow else LightGray,
                                            modifier = Modifier.size(27.dp)
                                        )
                                    }

                                    // Contenido principal de la Card (nombre, nº ejercicios, botón de acceso)
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

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                                            color = LightGray,
                                            fontSize = 14.sp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Botón animado para ir al detalle de la rutina
                                        AnimatedAccessButton(
                                            buttonText = "Ver rutina",
                                            onClick = {
                                                navController.navigate(Screen.RoutineDetail.createRoute(id_rutina))
                                            },
                                            color = MaterialTheme.colorScheme.onBackground,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    // Espacio extra al final para evitar que el último elemento quede oculto
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}
