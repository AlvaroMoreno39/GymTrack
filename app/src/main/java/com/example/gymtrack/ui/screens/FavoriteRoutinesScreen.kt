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
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoriteRoutinesScreen(
    viewModel: RoutineViewModel = viewModel(),
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var favorites by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { allRoutines ->
            favorites = allRoutines.filter { it.second.esFavorita }
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
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

            // Cabecera visual
            AnimatedEntrance {
                Box(
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favorite_routines),
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
                        Text("Tus", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("favoritas ⭐", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            // Lista
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(favorites, key = { it.first }) { (id_rutina, rutina) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // ⭐ Estrella para desmarcar
                                    IconToggleButton(
                                        checked = rutina.esFavorita,
                                        onCheckedChange = { isFavorite ->
                                            viewModel.toggleFavorite(id_rutina, isFavorite) { success ->
                                                if (success) {
                                                    viewModel.getUserRoutines { updated ->
                                                        favorites = updated.filter { it.second.esFavorita }
                                                    }
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (isFavorite) "Añadida a favoritos ⭐" else "Eliminada de favoritos ❌"
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
                                            imageVector = if (rutina.esFavorita) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = "Favorita",
                                            tint = if (rutina.esFavorita) Color(0xFFFFC107) else Color.Gray,
                                            modifier = Modifier.size(27.dp)
                                        )
                                    }

                                    // Contenido
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.FitnessCenter,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = rutina.nombreRutina,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        AnimatedAccessButton(
                                            buttonText = "Ver rutina",
                                            onClick = {
                                                navController.navigate(Screen.RoutineDetail.createRoute(id_rutina))
                                            },
                                            color = Color.Black,
                                            contentColor = Color.White,
                                            border = BorderStroke(1.dp, Color.Black),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(50.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}
