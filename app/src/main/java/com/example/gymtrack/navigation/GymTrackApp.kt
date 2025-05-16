package com.example.gymtrack.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymTrackApp(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isAdmin = currentUser?.email == "admin@gymtrack.com"

    val noMenuScreens = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route
    )

    Scaffold(
        topBar = {},
        bottomBar = {},
        floatingActionButton = {
            if (currentRoute !in noMenuScreens && !isAdmin) {
                ShareMenuSample(navController)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = Color.White
    ) { innerPadding ->
        GymTrackNavHost(navController, innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareMenuSample(navController: NavHostController) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    Box {
        AnimatedIconButton(
            onClick = { coroutineScope.launch { showSheet = true } }
        )

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                modifier = Modifier.wrapContentSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.White) // ← Fondo blanco

                ) {
                    ShareOption(Icons.Filled.Home, "Inicio") {
                        navController.navigate(Screen.Home.route)
                        showSheet = false
                    }
                    ShareOption(Icons.Filled.FitnessCenter, "Mis rutinas") {
                        navController.navigate(Screen.MyRoutines.route)
                        showSheet = false
                    }

                    ShareOption(Icons.Filled.AddCircle, "Registrar rutina") {
                        navController.navigate(Screen.RegisterRoutine.route)
                        showSheet = false
                    }

                    ShareOption(Icons.Filled.LibraryAdd, "Rutinas predefinidas") {
                        navController.navigate(Screen.PredefinedRoutines.route)
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
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pressed by remember { mutableStateOf(false) }

    val animationSpec = tween<Color>(
        durationMillis = 350,
        easing = FastOutSlowInEasing
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) Color(0xFFF0F0F0) else Color.White,
        animationSpec = animationSpec,
        label = "BackgroundColor"
    )

    val iconColor by animateColorAsState(
        targetValue = Color.Black,
        animationSpec = animationSpec,
        label = "IconColor"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
            onClick()
        }
    }

    Surface(
        shape = CircleShape,
        shadowElevation = 6.dp, // 👈 sombra PRO bien aplicada
        color = backgroundColor,
        modifier = modifier.size(50.dp) // tamaño elegante
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(BorderStroke(1.dp, Color.LightGray), CircleShape) // borde perfecto
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

@Composable
fun ShareOption(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .clickable { onClick() } // Aquí hacemos que al pulsar, navegue
            .background(Color.White) // ← Fondo blanco
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

@Composable
fun AnimatedEntrance(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(500)
        )
    ) {
        content()
    }
}

@Composable
fun FancySnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter
) {
    val snackbarData = snackbarHostState.currentSnackbarData
    var show by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            show = true
            delay(3000)
            show = false
            delay(500) // tiempo para dejar que la animación termine
            snackbarData.dismiss()
        }
    }

    AnimatedVisibility(
        visible = show,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(500)),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(500)),
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        snackbarData?.let { data ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = alignment
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF5F5F5),
                    border = BorderStroke(1.dp, Color.LightGray),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = data.visuals.message,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}



