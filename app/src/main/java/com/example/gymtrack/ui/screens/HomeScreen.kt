package com.example.gymtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymtrack.R
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.navigation.Screen
import com.example.gymtrack.navigation.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray
import com.example.gymtrack.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {

    val isAdmin = FirebaseAuth.getInstance().currentUser?.email == "admin@gymtrack.com"

    val cards = if (isAdmin) {
        listOf(
            Triple("Registrar nueva rutina predefinida", "Crea una rutina para todos los usuarios", R.drawable.register_routine),
            Triple("Ver rutinas predefinidas", "Explora y gestiona tus rutinas predefinidas", R.drawable.predefined_routine),
            Triple("Ajustes", "Gestiona tu cuenta, tema y más", R.drawable.settings) // <- AÑADIDO
        )
    } else {
        listOf(
            Triple("Registrar nueva rutina", "Crea una nueva rutina personalizada", R.drawable.register_routine),
            Triple("Ver rutinas predefinidas", "Explora rutinas ya creadas y añádelas", R.drawable.predefined_routine),
            Triple("Ver mis rutinas", "Accede a todas tus rutinas guardadas", R.drawable.my_routines),
            Triple("Rutinas favoritas", "Consulta tus rutinas destacadas", R.drawable.favorite_routines),
            Triple("Temporizador", "Controla tu tiempo de entrenamiento", R.drawable.timer),
            Triple("Ajustes", "Gestiona tu cuenta, tema y más", R.drawable.settings) // <- AÑADIDO
        )
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {

                ScreenHeader(
                    image = R.drawable.homephoto,
                    title = "Bienvenido",
                    subtitle = "¿Qué quieres hacer?"
                )

            }

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
                            when (title) {
                                "Registrar nueva rutina predefinida" -> navController.navigate(Screen.RegisterRoutine.route)
                                "Registrar nueva rutina" -> navController.navigate(Screen.RegisterRoutine.route)
                                "Ver rutinas predefinidas" -> navController.navigate(Screen.PredefinedRoutines.route)
                                "Ver mis rutinas" -> navController.navigate(Screen.MyRoutines.route)
                                "Rutinas favoritas" -> navController.navigate("favoritas")
                                "Temporizador" -> navController.navigate(Screen.Timer.route)
                                "Ajustes" -> navController.navigate(Screen.Settings.route) // <- AÑADIDO
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun RutinasCard(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
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
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

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

                AnimatedAccessButton(
                    buttonText = "Acceder",
                    color = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    fontSize = 15.sp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.width(150.dp).height(40.dp),
                    onClick = onClick
                )
            }
        }
    }
}



