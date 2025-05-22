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

/*
PredefinedRoutinesScreen.kt

Pantalla que muestra todas las rutinas predefinidas disponibles en la app.
Permite a cualquier usuario visualizarlas y añadirlas a sus rutinas personales.
Si eres administrador, puedes además eliminar rutinas predefinidas.
Presenta cards visuales para cada rutina, con animaciones, nivel de dificultad y feedback inmediato por Snackbar.
*/

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PredefinedRoutinesScreen(
    viewModel: PredefinedRoutinesViewModel,   // ViewModel que obtiene las rutinas predefinidas de Firebase
    navController: NavHostController,         // Navegador de pantallas
    routineViewModel: RoutineViewModel        // ViewModel para copiar/eliminar rutinas
) {
    // Estado para mostrar mensajes animados (snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Estado reactivo que almacena la lista de rutinas predefinidas
    var routines by remember { mutableStateOf<List<RoutineData>>(emptyList()) }

    // Detecta si el usuario actual es el admin
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    // Al montar la pantalla, recupera todas las rutinas predefinidas de Firestore
    LaunchedEffect(Unit) {
        viewModel.fetchRoutines { result -> routines = result }
    }

    Scaffold(snackbarHost = { FancySnackbarHost(snackbarHostState) }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Cabecera visual animada, coherente con el resto de la app
            ScreenHeader(
                image = R.drawable.predefined_routine,
                title = "Rutinas predefinidas",
                subtitle = "Encuentra inspiración y empieza ya"
            )

            // Lista animada de rutinas
            AnimatedEntrance {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Renderiza cada rutina como una Card profesional
                    items(routines) { rutina ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Título e icono
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

                                // Muestra nivel de dificultad si existe (colores diferentes)
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

                                // Cantidad de ejercicios de la rutina
                                Text(
                                    text = "${rutina.ejercicios.size} ejercicio${if (rutina.ejercicios.size == 1) "" else "s"}",
                                    color = LightGray,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Acciones: ver detalle, eliminar (admin) o añadir (usuario)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Botón para ver el detalle completo de la rutina
                                    AnimatedAccessButton(
                                        buttonText = "Ver rutina",
                                        onClick = {
                                            // Guarda la rutina seleccionada en el SavedStateHandle para la siguiente pantalla
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

                                    // Si es admin, muestra botón de eliminar; si es usuario, muestra botón de añadir
                                    if (isAdmin) {
                                        AnimatedAccessButton(
                                            buttonText = "Eliminar",
                                            onClick = {
                                                // Llama a la función de borrado del ViewModel y actualiza la UI tras borrar
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
                                                // Copia la rutina predefinida al usuario actual y muestra feedback
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

                    // Espaciado al final para evitar que el último elemento quede oculto
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

