package com.example.gymtrack.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.navigation.NavHostController
import com.example.gymtrack.navigation.AnimatedEntrance
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymtrack.navigation.AnimatedEntrance
import com.example.gymtrack.viewmodel.Exercise
import com.example.gymtrack.viewmodel.RoutineViewModel
import com.example.gymtrack.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.example.gymtrack.navigation.AnimatedAccessButton
import com.example.gymtrack.navigation.FancySnackbarHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var customMinutes by remember { mutableStateOf("") }
    var customSeconds by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf(60_000L) }
    var timeLeft by remember { mutableStateOf(selectedTime) }
    var isRunning by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    var finished by remember { mutableStateOf(false) }

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
            }

            override fun onFinish() {
                timeLeft = 0L
                isRunning = false
                finished = true

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern =
                        longArrayOf(0, 400, 300, 400, 300, 400) // espera, vibra, espera, vibra...
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(pattern, -1) // -1 = no repetir
                    )
                } else {
                    @Suppress("DEPRECATION")
                    repeat(3) {
                        vibrator.vibrate(400)
                        Thread.sleep(300)
                    }
                }

                scope.launch {
                    snackbarHostState.showSnackbar("¡Tiempo terminado!")
                }
            }

        }.start()
        isRunning = true
    }

    fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    fun resetTimer() {
        timer?.cancel()
        timeLeft = selectedTime
        isRunning = false
    }

    val presetTimes = listOf(
        60_000L to "1:00", 90_000L to "1:30",
        120_000L to "2:00", 150_000L to "2:30",
        180_000L to "3:00", 300_000L to "5:00"
    )

    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White) // ← Fondo blanco

        ) {

            item {
                TimerHeader()
            }


            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Tiempos rápidos",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(presetTimes) { (millis, _) ->
                        val totalSeconds = millis / 1000
                        val minutes = totalSeconds / 60
                        val seconds = totalSeconds % 60
                        val label = if (seconds == 0L) {
                            minutes.toString()
                        } else {
                            String.format("%d:%02d", minutes, seconds)
                        }

                        RoundBlackButton(label = label) {
                            selectedTime = millis
                            timeLeft = millis
                        }
                    }

                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Tiempo personalizado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    OutlinedTextField(
                        value = customMinutes,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 2) {
                                customMinutes = it
                            }
                        },
                        label = { Text("Min") },
                        modifier = Modifier
                            .height(60.dp)
                            .width(65.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )

                    OutlinedTextField(
                        value = customSeconds,
                        onValueChange = {
                            if (it.all { c -> c.isDigit() } && it.length <= 2) {
                                val value = it.toIntOrNull() ?: 0
                                if (value in 0..59) {
                                    customSeconds = it
                                }
                            }
                        },
                        label = { Text("Seg") },
                        modifier = Modifier
                            .height(60.dp)
                            .width(65.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                    )

                    // Botón perfectamente alineado con inputs
                    AnimatedAccessButton(
                        buttonText = "Establecer",
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .fillMaxWidth()
                    ) {
                        val min = customMinutes.toIntOrNull() ?: 0
                        val sec = customSeconds.toIntOrNull() ?: 0
                        val totalMillis = (min * 60 + sec) * 1000L
                        if (totalMillis > 0) {
                            selectedTime = totalMillis
                            timeLeft = totalMillis
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Introduce un tiempo válido")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    val progress = (timeLeft / selectedTime.toFloat()).coerceIn(0f, 1f)

                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(240.dp),
                            color = if (isRunning) Color.Black else Color.Gray,
                            strokeWidth = 8.dp,
                        )
                        Text(
                            text = formatTime(timeLeft),
                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 52.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        if (finished) {
                            // Solo se muestra el botón negro "Reiniciar"
                            AnimatedAccessButton(
                                buttonText = "Reiniciar",
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                resetTimer()
                                finished = false
                            }
                        } else {
                            // Modo normal: botones Iniciar/Pausar + Restablecer
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AnimatedAccessButton(buttonText = if (isRunning) "Pausar" else "Iniciar", modifier = Modifier.fillMaxWidth()) {
                                    if (isRunning) pauseTimer() else startTimer()
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            AnimatedAccessButton(
                                buttonText = "Restablecer",
                                color = Color.Red,
                                contentColor = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                selectedTime = 60_000L
                                timeLeft = 60_000L
                                isRunning = false
                                finished = false
                                timer?.cancel()
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(100.dp))

                }
            }
        }
    }
}

@Composable
fun RoundBlackButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color.Black,
        modifier = Modifier
            .size(80.dp)
            .clickable { onClick() },
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "MIN",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun TimerHeader() {
    AnimatedEntrance {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.timer),
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
                Text(
                    "Temporizador",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "Controla tu entrenamiento",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
        }
    }
}
