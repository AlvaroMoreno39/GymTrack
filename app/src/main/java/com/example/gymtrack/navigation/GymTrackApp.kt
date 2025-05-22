package com.example.gymtrack.navigation

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gymtrack.ui.theme.GymTrackTheme
import com.example.gymtrack.ui.theme.SoftWhite
import com.example.gymtrack.ui.theme.VeryLightGray
import com.example.gymtrack.viewmodel.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * GymTrackApp.kt
 *
 * Este archivo contiene los componentes de UI principales para la estructura base de la app GymTrack.
 * Aquí se gestiona la navegación principal, la integración del tema claro/oscuro, el menú flotante personalizado
 * y varios componentes reutilizables de UI como botones animados, Snackbars y cabeceras animadas.
 *
 * El objetivo es ofrecer una estructura moderna, flexible y coherente para toda la app usando Jetpack Compose.
 */

@Composable
fun GymTrackApp(
    themeViewModel: ThemeViewModel,
    darkMode: Boolean
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isAdmin = currentUser?.email == "admin@gymtrack.com"

    // Lista de rutas en las que NO queremos mostrar el botón flotante del menú (FAB).
    // Esto incluye las pantallas de Login, Registro y Recuperar contraseña.
    val noMenuScreens = listOf(
        Screen.Login.route,
        Screen.Register.route,
        Screen.ForgotPassword.route
    )

    // Comprobamos si la ruta actual está en la lista de rutas "prohibidas" o si comienza por alguna de ellas,
    // incluyendo posibles variantes con parámetros (por ejemplo, "forgot_password?change=true").
    // De esta forma, nos aseguramos de ocultar el FAB en todas sus variantes.
    val hideFab = noMenuScreens.any { route ->
        currentRoute == route || // Ruta exacta (ej: "login")
                currentRoute?.startsWith("$route?") == true || // Variante con parámetros (ej: "forgot_password?change=true")
                currentRoute?.startsWith("$route/") == true    // Variante con subrutas (si las hubiera)
    }


    // Log para pruebas específicamente para ver donde la ruta actual, si se oculta el FAB y si es admin
    Log.d("NAV_DEBUG", "Ruta actual: $currentRoute | Ocultar FAB: $hideFab | Admin: $isAdmin")

    Crossfade(targetState = darkMode, label = "theme") { isDark ->
        GymTrackTheme(darkTheme = isDark) {
            Scaffold(
                topBar = {},
                bottomBar = {},
                // SOLO mostramos el menú flotante si NO estamos en una pantalla prohibida
                floatingActionButton = {
                    if (!hideFab) {
                        ShareMenuSample(navController)
                    }
                },
                floatingActionButtonPosition = FabPosition.Center,
                containerColor = MaterialTheme.colorScheme.background
            ) { innerPadding ->
                GymTrackNavHost(navController, innerPadding, themeViewModel)
            }
        }
    }
}


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
                            navController.navigate(Screen.PredefinedRoutines.route)
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
                            navController.navigate(Screen.RegisterRoutine.route)
                            showSheet = false
                        }
                        ShareOption(Icons.Filled.FitnessCenter, "Mis rutinas") {
                            navController.navigate(Screen.MyRoutines.route)
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

/**
 * Botón animado reutilizable para acciones principales.
 * Al pulsar, invierte colores con animación y ejecuta la acción tras un breve retardo.
 */
@Composable
fun AnimatedAccessButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 56.dp,
    fontSize: TextUnit = 16.sp,
    border: BorderStroke? = BorderStroke(1.dp, color),
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) contentColor else color,
        animationSpec = tween(durationMillis = 350),
        label = "AnimatedButtonBackground"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (pressed) color else contentColor,
        animationSpec = tween(durationMillis = 350),
        label = "AnimatedButtonContent"
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
            .height(height)
            .defaultMinSize(minWidth = 100.dp),
        shape = RoundedCornerShape(cornerRadius),
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = animatedContentColor
        ),
        contentPadding = PaddingValues()
    ) {
        Text(buttonText, fontSize = fontSize)
    }
}

/**
 * Permite que un bloque de contenido aparezca con animación de entrada (fade + slide).
 * Ideal para cabeceras y pantallas de bienvenida.
 */
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

/**
 * Componente Snackbar personalizado animado, para notificaciones visuales en la app.
 * Aparece/desaparece con animación y diseño profesional.
 */
@Composable
fun FancySnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter
) {
    val snackbarData = snackbarHostState.currentSnackbarData
    var show by remember { mutableStateOf(false) }

    // Controla la aparición/desaparición automática de la Snackbar
    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            show = true
            delay(3000)
            show = false
            delay(500)
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
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shadowElevation = 12.dp,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = data.visuals.message,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Switch personalizado animado que adapta colores a claro/oscuro.
 * Mantiene transiciones suaves y accesibles.
 */
@Composable
fun SmoothSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDark = isSystemInDarkTheme()

    val switchColors = if (isDark) {
        SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color.Black,
            uncheckedThumbColor = Color.LightGray,
            uncheckedTrackColor = Color.DarkGray
        )
    } else {
        SwitchDefaults.colors(
            checkedThumbColor = Color.Black,
            checkedTrackColor = Color.LightGray,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.Gray
        )
    }

    val transition = updateTransition(targetState = checked, label = "SwitchTransition")
    val thumbOffset by transition.animateDp(
        label = "ThumbOffset",
        transitionSpec = { tween(durationMillis = 400) }
    ) { state -> if (state) 20.dp else 0.dp }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = switchColors
    )
}

/**
 * Cabecera de pantalla animada, con imagen superior, título y subtítulo.
 * Ofrece un diseño moderno y profesional, coherente en toda la app.
 */
@Composable
fun ScreenHeader(
    @DrawableRes image: Int,
    title: String,
    subtitle: String? = null
) {
    AnimatedEntrance {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                subtitle?.let {
                    Text(
                        it,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}


