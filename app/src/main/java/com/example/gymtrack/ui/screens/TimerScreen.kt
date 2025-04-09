package com.example.gymtrack.ui.screens

import android.os.CountDownTimer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun TimerScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current

    // Tiempo total en milisegundos (por ejemplo: 1 minuto = 60.000 ms)
    val initialTimeMillis = 60_000L

    // Estado del tiempo restante
    var timeLeft by remember { mutableStateOf(initialTimeMillis) }

    // Estado para saber si está corriendo o pausado
    var isRunning by remember { mutableStateOf(false) }

    // Referencia al temporizador actual
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    // Función para iniciar o reanudar el temporizador
    fun startTimer() {
        timer?.cancel() // Cancela el anterior si existía
        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
            }

            override fun onFinish() {
                timeLeft = 0L
                isRunning = false
                Toast.makeText(context, "¡Tiempo terminado!", Toast.LENGTH_SHORT).show()
            }
        }.start()
        isRunning = true
    }

    // Función para pausar
    fun pauseTimer() {
        timer?.cancel()
        isRunning = false
    }

    // Función para reiniciar
    fun resetTimer() {
        timer?.cancel()
        timeLeft = initialTimeMillis
        isRunning = false
    }

    // Convierte milisegundos a formato MM:SS
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatTime(timeLeft),
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 64.sp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                if (isRunning) pauseTimer() else startTimer()
            }) {
                Text(if (isRunning) "Pausar" else "Iniciar")
            }

            Button(onClick = { resetTimer() }) {
                Text("Reiniciar")
            }
        }
    }
}