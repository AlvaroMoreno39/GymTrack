package com.example.gymtrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun PredefinedRoutineDetailScreen(
    navController: NavHostController
) {
    val rutina = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<RoutineData>("predefined_routine")

    rutina?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera visual
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
                        Text("Rutina", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(rutina.nombreRutina, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rutina.ejercicios.forEach { ejercicio ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8E8E8))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "• ${ejercicio.nombre}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Grupo: ${ejercicio.grupoMuscular}")
                                Text("Tipo: ${ejercicio.tipo}")
                                if (ejercicio.series > 0) Text("Series: ${ejercicio.series}")
                                if (ejercicio.reps > 0) Text("Reps: ${ejercicio.reps}")
                                if (ejercicio.peso > 0) Text("Peso: ${ejercicio.peso} kg")
                                if (ejercicio.duracion > 0) Text("Duración: ${ejercicio.duracion} min")
                                Text("Intensidad: ${ejercicio.intensidad}")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
