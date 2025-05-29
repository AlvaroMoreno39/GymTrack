package com.alvaromoreno.gymtrack.ui.screens.RoutineListScreen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alvaromoreno.gymtrack.viewmodel.RoutineData
import com.alvaromoreno.gymtrack.viewmodel.RoutineViewModel
import androidx.compose.foundation.lazy.items
import com.alvaromoreno.gymtrack.ui.components.FancySnackbarHost
import com.alvaromoreno.gymtrack.ui.components.ScreenHeader
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.alvaromoreno.gymtrack.R


/**
 * RoutineListScreen.kt
 *
 * Pantalla que muestra las rutinas del usuario o las rutinas predefinidas, según el parámetro `showPredefined`.
 * Permite al usuario:
 * - Ver sus propias rutinas y acceder a los detalles.
 * - Ver rutinas predefinidas cargadas en la app.
 * - Si es administrador, puede eliminar rutinas predefinidas.
 *
 * Usa:
 * - LazyColumn para listar rutinas.
 * - RoutineCardUser y RoutineCardPredefined como componentes visuales reutilizables.
 * - Snackbars para mostrar feedback inmediato al usuario.
 *
 * Implementado con Jetpack Compose y patrón MVVM usando RoutineViewModel.
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RoutineListScreen(
    navController: NavHostController,         // Controlador de navegación para ir a detalles u otras pantallas
    viewModel: RoutineViewModel,              // ViewModel que gestiona las rutinas
    showPredefined: Boolean                   // Si true, muestra rutinas predefinidas; si false, muestra las del usuario
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    var routines by remember { mutableStateOf<List<Pair<String, RoutineData>>>(emptyList()) }
    var predefinedRoutines by remember { mutableStateOf<List<RoutineData>>(emptyList()) }

    // Al montar la pantalla, cargamos las rutinas según el tipo
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
            // Cabecera visual con imagen, título y subtítulo
            ScreenHeader(
                image = if (showPredefined) R.drawable.predefined_routine else R.drawable.my_routines,
                title = if (showPredefined) "Rutinas predefinidas" else "Tus rutinas",
                subtitle = if (showPredefined) "Rutinas disponibles en la app" else "Entrena con lo que ya tienes"
            )

            // Lista scrollable de rutinas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (showPredefined) {
                    // Mostrar rutinas predefinidas
                    items(predefinedRoutines, key = { it.nombreRutina }) { rutina ->
                        var visible by remember { mutableStateOf(true) }
                        AnimatedVisibility(
                            visible = visible,
                            exit = slideOutVertically(tween(400)) + fadeOut(tween(300))
                        ) {
                            RoutineCardPredefined(
                                rutina = rutina,
                                isAdmin = isAdmin,
                                navController = navController,
                                viewModel = viewModel,
                                onDeleted = {
                                    visible = false
                                    scope.launch {
                                        delay(400)
                                        viewModel.fetchPredefinedRoutines { updated -> predefinedRoutines = updated }
                                        snackbarHostState.showSnackbar("Rutina eliminada ✅")
                                    }
                                },
                                onAdded = {
                                    scope.launch { snackbarHostState.showSnackbar("Rutina añadida correctamente ✅") }
                                },
                                onError = {
                                    scope.launch { snackbarHostState.showSnackbar("Ocurrió un error ❌") }
                                }
                            )
                        }
                    }
                } else {
                    // Mostrar rutinas del usuario
                    items(routines, key = { it.first }) { (id_rutina, rutina) ->
                        var visible by remember { mutableStateOf(true) }
                        AnimatedVisibility(
                            visible = visible,
                            exit = slideOutVertically(tween(400)) + fadeOut(tween(300))
                        ) {
                            RoutineCardUser(
                                id_rutina = id_rutina,
                                rutina = rutina,
                                navController = navController,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                scope = scope,
                                onDeleted = {
                                    visible = false
                                    scope.launch {
                                        delay(400)
                                        viewModel.getUserRoutines { updated -> routines = updated }
                                    }
                                }
                            )
                        }
                    }
                }
                // Espacio final para evitar que el último ítem quede pegado al borde inferior
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}


