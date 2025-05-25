package com.example.gymtrack.ui.screens.RoutineListScreen

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

