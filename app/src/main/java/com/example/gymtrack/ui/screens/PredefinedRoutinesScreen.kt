package com.example.gymtrack.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
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

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Cabecera
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
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text(
                            "Rutinas",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            "predefinidas",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
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
                    items(routines) { rutina ->
                        val isAdmin =
                            FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Título
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

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // 🔍 Botón "Ver rutina"
                                    AnimatedAccessButton(
                                        buttonText = "Ver rutina",
                                        onClick = {
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("predefined_routine", rutina)
                                            navController.navigate("predefined_routine_detail")
                                        }
                                        ,
                                        containerColor = Color.Black,
                                        contentColor = Color.White,
                                        borderColor = Color.Black,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Botón condicional
                                    if (isAdmin) {
                                        // 🗑️ Botón "Eliminar"
                                        AnimatedAccessButton(
                                            buttonText = "Eliminar",
                                            onClick = {
                                                routineViewModel.deletePredefinedRoutine(rutina.nombreRutina) { success ->
                                                    scope.launch {
                                                        if (success) {
                                                            // Refresca la lista quitando esta rutina
                                                            routines =
                                                                routines.filterNot { it.nombreRutina == rutina.nombreRutina }
                                                            snackbarHostState.showSnackbar("Rutina eliminada correctamente")
                                                        } else {
                                                            snackbarHostState.showSnackbar("Error al eliminar rutina")
                                                        }
                                                    }
                                                }
                                            },
                                            containerColor = Color.Red,
                                            contentColor = Color.White,
                                            borderColor = Color.Red,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )
                                    } else {
                                        // ✅ Botón "Añadir" para usuario normal
                                        AnimatedAccessButton(
                                            buttonText = "Añadir",
                                            onClick = {
                                                routineViewModel.copyPredefinedRoutineToUser(
                                                    nombreRutina = rutina.nombreRutina,
                                                    ejercicios = rutina.ejercicios
                                                ) { success ->
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            if (success) "Rutina añadida correctamente"
                                                            else "Error al añadir rutina"
                                                        )
                                                    }
                                                }
                                            },
                                            containerColor = Color.Black,
                                            contentColor = Color.White,
                                            borderColor = Color.Black,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                        )
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

