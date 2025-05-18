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
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.ui.theme.FavoriteYellow
import com.example.gymtrack.ui.theme.LightGray
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyRoutineScreen(
    viewModel: RoutineViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var routines by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }

    // Carga las rutinas al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.getUserRoutines { loaded ->
            routines = loaded
            if (loaded.isEmpty()) {
                scope.launch {
                    snackbarHostState.showSnackbar("No tienes rutinas aún ⚠️")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { FancySnackbarHost(snackbarHostState) }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {

            // Cabecera animada
            AnimatedEntrance {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.my_routines),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .align(Alignment.BottomCenter)
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            "Tus",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "rutinas",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // Lista de rutinas animada
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(routines, key = { it.first }) { (id_rutina, rutina) ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    // ⭐ Estrella de favoritos arriba a la derecha
                                    IconToggleButton(
                                        checked = rutina.esFavorita,
                                        onCheckedChange = { isFavorite ->
                                            viewModel.toggleFavorite(id_rutina, isFavorite) { success ->
                                                if (success) {
                                                    viewModel.getUserRoutines { updated -> routines = updated }
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
                                            tint = if (rutina.esFavorita) FavoriteYellow else LightGray,
                                            modifier = Modifier.size(27.dp) // ⭐ TAMAÑO MÁS GRANDE
                                        )
                                    }

                                    // 📦 Contenido principal dentro de una Column
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        // 🏋️‍♂️ Nombre de la rutina
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

                                        // 🧮 Cantidad de ejercicios
                                        Text(
                                            text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                                            color = LightGray,
                                            fontSize = 14.sp
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Botones
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            // 🔍 Ver rutina
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

                                            // 🗑️ Eliminar rutina
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
                                                            viewModel.getUserRoutines { updated -> routines = updated }
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

                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

