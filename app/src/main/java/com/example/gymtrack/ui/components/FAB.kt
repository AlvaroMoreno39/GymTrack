package com.example.gymtrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.ui.theme.VeryLightGray
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Componente que representa el menú flotante de la app (accesible desde el botón central).
 * Utiliza una hoja modal inferior (ModalBottomSheet) y un botón animado con icono de menú.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareMenuSample(navController: NavHostController) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    Box {
        // Botón flotante animado
        AnimatedIconButton(
            onClick = { coroutineScope.launch { showSheet = true } }
        )

        // Menú flotante que se muestra al pulsar el botón
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.wrapContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Opciones del admin: solo Inicio, Crear rutina predefinida, Rutinas predefinidas
                    if (isAdmin) {
                        ShareOption(Icons.Filled.Home, "Inicio") {
                            navController.navigate(Screen.Home.route)
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.LibraryAdd, "Crear rutina predefinida") {
                            navController.navigate(Screen.RegisterRoutine.route)
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.FitnessCenter, "Rutinas predefinidas") {
                            navController.navigate(Screen.RoutineList.createRoute(predefined = true))
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.Settings, "Ajustes") {
                            navController.navigate(Screen.Settings.route)
                            showSheet = false
                        }
                    } else {
                        // Opciones normales (todas)
                        ShareOption(Icons.Filled.Home, "Inicio") {
                            navController.navigate(Screen.Home.route)
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.AddCircle, "Registrar rutina") {
                            navController.navigate(Screen.RoutineList.createRoute(predefined = true))
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.FitnessCenter, "Rutinas predefinidas") {
                            navController.navigate(Screen.RoutineList.createRoute(predefined = true))
                            showSheet = false
                        }
                        ShareOption(Icons.AutoMirrored.Filled.ListAlt, "Mis rutinas") {
                            navController.navigate(Screen.RoutineList.createRoute(predefined = false))
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.Star, "Rutinas favoritas") {
                            navController.navigate("favoritas")
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.Timer, "Temporizador") {
                            navController.navigate(Screen.Timer.route)
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.Settings, "Ajustes") {
                            navController.navigate(Screen.Settings.route)
                            showSheet = false
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

/**
 * Botón circular animado con icono de menú.
 * Al hacer clic, invierte los colores y lanza la animación de pulsación.
 */
@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }

    // Configuración de la animación de color para el fondo del botón
    val animationSpec = tween<Color>(
        durationMillis = 350,
        easing = FastOutSlowInEasing
    )

    // Animación para el color de fondo según el estado de pulsado
    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) VeryLightGray else MaterialTheme.colorScheme.background,
        animationSpec = animationSpec,
        label = "BackgroundColor"
    )

    // Animación para el color del icono
    val iconColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.onBackground,
        animationSpec = animationSpec,
        label = "IconColor"
    )

    // Efecto de pulsación: cuando se pulsa, se retrasa la acción para ver la animación
    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
            onClick()
        }
    }

    Surface(
        shape = CircleShape,
        shadowElevation = 6.dp,
        color = backgroundColor,
        modifier = modifier.size(50.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(BorderStroke(1.dp, Color.LightGray), CircleShape)
                .clickable { pressed = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = iconColor
            )
        }
    }
}

/**
 * Opción visual individual para el menú compartido.
 * Muestra un icono y texto, con animación de color y click navegable.
 */
@Composable
fun ShareOption(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.background)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}









