package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PredefinedRoutinesScreen(
    viewModel: PredefinedRoutinesViewModel,
    navController: NavHostController,
    routineViewModel: RoutineViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var routines by remember { mutableStateOf<List<RoutineData>>(emptyList()) }

    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    LaunchedEffect(Unit) {
        viewModel.fetchRoutines { result -> routines = result }
    }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            ScreenHeader(
                image = R.drawable.predefined_routine,
                title = "Rutinas predefinidas",
                subtitle = "Encuentra inspiración y empieza ya"
            )

            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(routines) { rutina ->
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

                                // Nivel de dificultad (solo si existe)
                                rutina.nivel?.let { nivel ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when (nivel.lowercase()) {
                                                    "principiante" -> Color(0xFFB2FF59) // Verde lima
                                                    "intermedio" -> Color(0xFFFFF176)   // Amarillo suave
                                                    "avanzado" -> Color(0xFFFF8A65)     // Naranja/rojo
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
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("predefined_routine", rutina)
                                            navController.navigate("predefined_routine_detail")
                                        },
                                        color = MaterialTheme.colorScheme.onBackground,
                                        contentColor = MaterialTheme.colorScheme.background,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    if (isAdmin) {
                                        AnimatedAccessButton(
                                            buttonText = "Eliminar",
                                            onClick = {
                                                routineViewModel.deletePredefinedRoutine(rutina.nombreRutina) { success ->
                                                    scope.launch {
                                                        routines = routines.filterNot { it.nombreRutina == rutina.nombreRutina }
                                                        snackbarHostState.showSnackbar(
                                                            if (success) "Rutina eliminada correctamente ✅"
                                                            else "Error al eliminar rutina ❌"
                                                        )
                                                    }
                                                }
                                            },
                                            color = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )
                                    } else {
                                        AnimatedAccessButton(
                                            buttonText = "Añadir",
                                            onClick = {
                                                routineViewModel.copyPredefinedRoutineToUser(
                                                    rutina.nombreRutina,
                                                    rutina.ejercicios
                                                ) { success ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (success) "Rutina añadida correctamente ✅"
                                                            else "Error al añadir rutina ❌"
                                                        )
                                                    }
                                                }
                                            },
                                            color = MaterialTheme.colorScheme.onBackground,
                                            contentColor = MaterialTheme.colorScheme.background,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                                            modifier = Modifier
                                                .weight(1f)
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
