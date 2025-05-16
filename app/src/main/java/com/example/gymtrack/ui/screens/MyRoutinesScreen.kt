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
import com.example.gymtrack.navigation.FancySnackbarHost
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
        Column(modifier = Modifier.fillMaxSize()) {

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
                            .background(Color.White.copy(alpha = 0.65f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Text("Tus", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("rutinas", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                // 🏋️‍♂️ Nombre de la rutina
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

                                // 🧮 Cantidad de ejercicios
                                Text(
                                    text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                                    color = Color.Gray,
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
                                        onClick = {
                                            navController.navigate(
                                                Screen.RoutineDetail.createRoute(id_rutina)
                                            )
                                        },
                                        containerColor = Color.Black,
                                        contentColor = Color.White,
                                        borderColor = Color.Black,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(50.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // 🗑️ Eliminar rutina
                                    AnimatedAccessButton(
                                        buttonText = "Eliminar",
                                        onClick = {
                                            viewModel.deleteRoutine(id_rutina) { success ->
                                                if (success) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Rutina eliminada ✅")
                                                    }
                                                    viewModel.getUserRoutines { updated ->
                                                        routines = updated
                                                    }
                                                } else {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Error al eliminar ❌")
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

@Composable
fun AnimatedAccessButton(
    buttonText: String = "Acceder",
    onClick: () -> Unit,
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    borderColor: Color = Color.Black,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }

    val animatedBackground by animateColorAsState(
        targetValue = if (pressed) contentColor else containerColor,
        label = "ButtonBG"
    )
    val animatedContentColor by animateColorAsState(
        targetValue = if (pressed) containerColor else contentColor,
        label = "ButtonText"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
            onClick()
        }
    }

    Button(
        onClick = { pressed = true },
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedBackground,
            contentColor = animatedContentColor
        ),
        contentPadding = PaddingValues()
    ) {
        Text(buttonText, fontSize = 16.sp)
    }
}

