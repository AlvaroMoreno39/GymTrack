package com.example.gymtrack.ui.screens.TimerScreen

import android.content.Context
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
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.gymtrack.R
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import com.example.gymtrack.ui.components.AnimatedAccessButton
import com.example.gymtrack.ui.components.FancySnackbarHost
import com.example.gymtrack.ui.components.ScreenHeader
import com.example.gymtrack.ui.theme.LightGray

/*
TimerScreen.kt

Pantalla de temporizador de GymTrack.
Permite seleccionar tiempos predefinidos, establecer tiempos personalizados, iniciar, pausar y restablecer el temporizador.
Incluye animación de progreso, vibración al terminar y feedback mediante Snackbars.
Todo el diseño es minimalista y coherente con el resto de la app.
*/

// Importante: ExperimentalMaterial3Api necesario por los componentes usados
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(navController: NavHostController) {
    // Contexto necesario para vibrar y mostrar Snackbars
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado de inputs y temporizador
    var customMinutes by remember { mutableStateOf("") }         // Minutos para input personalizado
    var customSeconds by remember { mutableStateOf("") }         // Segundos para input personalizado
    var selectedTime by remember { mutableStateOf(60_000L) }     // Tiempo seleccionado en milisegundos
    var timeLeft by remember { mutableStateOf(selectedTime) }    // Tiempo restante (se actualiza cada tick)
    var isRunning by remember { mutableStateOf(false) }          // ¿Está el temporizador corriendo?
    var timer: CountDownTimer? by remember { mutableStateOf(null) } // Referencia al temporizador actual
    var finished by remember { mutableStateOf(false) }           // ¿El temporizador ha terminado?

    // Vibrador del sistema para feedback al terminar
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    /**
     * Formatea el tiempo de milisegundos a mm:ss
     */
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Inicia el temporizador y gestiona los ticks/cuenta atrás
     * Al finalizar, vibra y muestra un mensaje por Snackbar
     */
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

                // Vibración adaptada según versión de Android
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pattern = longArrayOf(0, 400, 300, 400, 300, 400)
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(pattern, -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    repeat(3) {
                        vibrator.vibrate(400)
                        Thread.sleep(300)
                    }
                }

                // Feedback visual con Snackbar
                scope.launch {
                    snackbarHostState.showSnackbar("¡Tiempo terminado! ⏰")
                }
            }

        }.start()
        isRunning = true
    }

    /**
     * Pausa la cuenta atrás (sin reiniciar el tiempo)
     */
    fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    /**
     * Resetea el temporizador al tiempo seleccionado (no inicia)
     */
    fun resetTimer() {
        timer?.cancel()
        timeLeft = selectedTime
        isRunning = false
    }

    // Lista de tiempos rápidos/predefinidos (minutos y segundos en ms y formato string)
    val presetTimes = listOf(
        60_000L to "1:00", 90_000L to "1:30",
        120_000L to "2:00", 150_000L to "2:30",
        180_000L to "3:00", 300_000L to "5:00"
    )

    // --- UI principal de la pantalla ---
    Scaffold(snackbarHost = {
        FancySnackbarHost(snackbarHostState)
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // --- Cabecera visual con imagen y título ---
            item {
                ScreenHeader(
                    image = R.drawable.timer,
                    title = "Temporizador",
                    subtitle = "Controla tu entrenamiento"
                )
            }

            // --- Sección: tiempos rápidos ---
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
                // Grid de botones redondos para tiempos predefinidos
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

                        // Botón circular para seleccionar el tiempo
                        RoundBlackButton(label = label) {
                            selectedTime = millis
                            timeLeft = millis
                        }
                    }
                }
            }

            // --- Sección: tiempo personalizado ---
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
                // Inputs de minutos y segundos personalizados
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

                    // Botón para establecer el tiempo personalizado
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
                                snackbarHostState.showSnackbar("Introduce un tiempo válido ⚠️")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- Sección: indicador de progreso y control del temporizador ---
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    val progress = (timeLeft / selectedTime.toFloat()).coerceIn(0f, 1f)

                    // Indicador circular de progreso del temporizador
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(240.dp),
                            color = if (isRunning) MaterialTheme.colorScheme.onBackground else LightGray,
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
                            // Si el tiempo ha terminado, solo se muestra "Reiniciar"
                            AnimatedAccessButton(
                                buttonText = "Reiniciar",
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                resetTimer()
                                finished = false
                            }
                        } else {
                            // Si está corriendo: botón Pausar, si no: botón Iniciar
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AnimatedAccessButton(buttonText = if (isRunning) "Pausar" else "Iniciar", modifier = Modifier.fillMaxWidth()) {
                                    if (isRunning) pauseTimer() else startTimer()
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Botón de "Restablecer" (rojo)
                            AnimatedAccessButton(
                                buttonText = "Restablecer",
                                color = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.background,
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

