package com.example.gymtrack.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTrackApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Si quieres condicionar la visibilidad luego, déjalo aquí aunque ahora no lo uses
    val showBottomBar = false
    val showTopBar = false

    Scaffold(
        // SIN topBar
        topBar = {},
        // SIN bottomBar
        bottomBar = {}
    ) { innerPadding ->
        GymTrackNavHost(navController, innerPadding)
    }
}

// BOTÓN DE ACCESO CON ANIMACIÓN PERSONALIZADA
@Composable
fun AnimatedAccessButton(buttonText: String = "Acceder", onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }

    val animationSpec = tween<Color>(
        durationMillis = 350,
        easing = FastOutSlowInEasing
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) Color.White else Color.Black,
        animationSpec = animationSpec,
        label = "ButtonBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (pressed) Color.Black else Color.White,
        animationSpec = animationSpec,
        label = "ButtonContentColor"
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
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues()
    ) {
        Text(buttonText, fontSize = 16.sp)
    }
}
