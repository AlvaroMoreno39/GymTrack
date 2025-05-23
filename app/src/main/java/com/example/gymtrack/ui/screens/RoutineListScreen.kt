package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
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
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.AnimatedEntrance
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.example.gymtrack.ui.theme.FavoriteYellow
import com.example.gymtrack.ui.theme.LightGray
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoutineListScreen(
    navController: NavHostController,
    viewModel: RoutineViewModel,
    showPredefined: Boolean
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    // Carga reactiva de rutinas
    var routines by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }
    var predefinedRoutines by remember { mutableStateOf<List<RoutineData>>(emptyList()) }

    // Carga inicial
    LaunchedEffect(showPredefined) {
        if (showPredefined) {
            viewModel.fetchPredefinedRoutines { list -> predefinedRoutines = list }
        } else {
            viewModel.getUserRoutines { list -> routines = list }
        }
    }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScreenHeader(
                image = if (showPredefined) R.drawable.predefined_routine else R.drawable.my_routines,
                title = if (showPredefined) "Rutinas predefinidas" else "Tus rutinas",
                subtitle = if (showPredefined) "Rutinas disponibles en la app" else "Entrena con lo que ya tienes"
            )

            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (showPredefined) {
                        items(predefinedRoutines) { rutina ->
                            RoutineCardPredefined(
                                rutina = rutina,
                                isAdmin = isAdmin,
                                navController = navController,
                                viewModel = viewModel,
                                onDeleted = {
                                    // Recarga después de eliminar
                                    viewModel.fetchPredefinedRoutines { updated -> predefinedRoutines = updated }
                                    scope.launch { snackbarHostState.showSnackbar("Rutina eliminada ✅") }
                                },
                                onAdded = {
                                    scope.launch { snackbarHostState.showSnackbar("Rutina añadida correctamente ✅") }
                                },
                                onError = {
                                    scope.launch { snackbarHostState.showSnackbar("Ocurrió un error ❌") }
                                }
                            )
                        }
                    } else {
                        items(routines, key = { it.first }) { (id_rutina, rutina) ->
                            RoutineCardUser(
                                id_rutina = id_rutina,
                                rutina = rutina,
                                navController = navController,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                scope = scope,
                                onDeleted = {
                                    // Recarga después de eliminar
                                    viewModel.getUserRoutines { updated -> routines = updated }
                                }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun RoutineCardUser(
    id_rutina: String,
    rutina: RoutineData,
    navController: NavHostController,
    viewModel: RoutineViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    onDeleted: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                // Estrella de favoritos
                IconToggleButton(
                    checked = rutina.esFavorita,
                    onCheckedChange = { isFavorite ->
                        viewModel.toggleFavorite(id_rutina, isFavorite) { success ->
                            if (success) {
                                onDeleted() // Recarga
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

@Composable
fun RoutineCardPredefined(
    rutina: RoutineData,
    isAdmin: Boolean,
    navController: NavHostController,
    viewModel: RoutineViewModel,
    onDeleted: () -> Unit,
    onAdded: () -> Unit,
    onError: () -> Unit
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

