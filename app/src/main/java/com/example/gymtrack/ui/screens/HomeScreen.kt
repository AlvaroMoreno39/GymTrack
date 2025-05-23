package com.example.gymtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

/**
 * HomeScreen.kt
 *
 * Pantalla principal de GymTrack. Actúa como un hub visual desde el que el usuario (normal o admin)
 * accede a todas las funcionalidades clave de la app. Presenta las opciones en forma de cards animadas.
 * El contenido se adapta dinámicamente según si el usuario es administrador o no.
 */

@Composable
fun HomeScreen(
    navController: NavHostController,   // Navegador para movernos entre pantallas
    authViewModel: AuthViewModel        // ViewModel para autenticación (por si necesitas datos de usuario)
) {
    // Comprueba si el usuario actual es admin, usando el email
    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    // Define las opciones (cards) a mostrar según el tipo de usuario (admin o no)
    val cards = if (isAdmin) {
        listOf(
            Triple(
                "Registrar nueva rutina predefinida",
                "Crea una rutina para todos los usuarios",
                R.drawable.register_routine
            ),
            Triple(
                "Ver rutinas predefinidas",
                "Explora y gestiona tus rutinas predefinidas",
                R.drawable.predefined_routine
            ),
            Triple(
                "Ajustes",
                "Gestiona tu cuenta, tema y más",
                R.drawable.settings
            )
        )
    } else {
        listOf(
            Triple(
                "Registrar nueva rutina",
                "Crea una nueva rutina personalizada",
                R.drawable.register_routine
            ),
            Triple(
                "Ver rutinas predefinidas",
                "Explora rutinas ya creadas y añádelas",
                R.drawable.predefined_routine
            ),
            Triple(
                "Ver mis rutinas",
                "Accede a todas tus rutinas guardadas",
                R.drawable.my_routines
            ),
            Triple(
                "Rutinas favoritas",
                "Consulta tus rutinas destacadas",
                R.drawable.favorite_routines
            ),
            Triple(
                "Temporizador",
                "Controla tu tiempo de entrenamiento",
                R.drawable.timer
            ),
            Triple(
                "Ajustes",
                "Gestiona tu cuenta, tema y más",
                R.drawable.settings
            )
        )
    }

    // Estructura principal de la pantalla (Scaffold = layout de Material Design)
    Scaffold { padding ->
        // Lista vertical animada con las cards de navegación
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Cabecera animada de la pantalla principal
            item {
                ScreenHeader(
                    image = R.drawable.home,
                    title = "Bienvenido",
                    subtitle = "¿Qué quieres hacer?"
                )
            }

            // Muestra cada card de opción con animación de entrada y acceso directo
            itemsIndexed(cards) { index, (title, desc, img) ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(300, delayMillis = index * 100)) +
                            slideInVertically(
                                initialOffsetY = { 200 },
                                animationSpec = tween(300, delayMillis = index * 100)
                            )
                ) {
                    RutinasCard(
                        title = title,
                        description = desc,
                        imageRes = img,
                        onClick = {
                            // Navegación a la pantalla correspondiente según la opción pulsada
                            when (title) {
                                "Registrar nueva rutina predefinida" -> navController.navigate(Screen.RegisterRoutine.route)
                                "Registrar nueva rutina" -> navController.navigate(Screen.RegisterRoutine.route)
                                "Ver rutinas predefinidas" -> navController.navigate("routineList?predefined=true")
                                "Ver mis rutinas" -> navController.navigate("routineList?predefined=false")
                                "Rutinas favoritas" -> navController.navigate("favoritas")
                                "Temporizador" -> navController.navigate(Screen.Timer.route)
                                "Ajustes" -> navController.navigate(Screen.Settings.route)
                            }
                        }
                    )
                }
            }

            // Espaciado extra al final para que no tape el menú
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

/**
 * RutinasCard
 *
 * Componente reutilizable que muestra una opción de la Home en formato Card visual.
 * Incluye imagen, título, descripción y un botón animado de acceso.
 */
@Composable
fun RutinasCard(
    title: String,            // Título principal de la opción
    description: String,      // Descripción breve de la opción
    imageRes: Int,            // Recurso de imagen asociado a la opción
    onClick: () -> Unit       // Acción a ejecutar al pulsar el botón "Acceder"
) {
    Card(
        shape = RoundedCornerShape(16.dp),   // Esquinas redondeadas
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen decorativa en la parte superior de la card
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Contenido textual y botón
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                // Botón de acceso animado, estilizado como el resto de la app
                AnimatedAccessButton(
                    buttonText = "Acceder",
                    color = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    fontSize = 15.sp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp),
                    onClick = onClick
                )
            }
        }
    }
}
