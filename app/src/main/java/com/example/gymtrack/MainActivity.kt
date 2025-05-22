package com.example.gymtrack

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gymtrack.navigation.GymTrackApp
import com.example.gymtrack.notification.NotificationWorker
import com.example.gymtrack.notification.createNotificationChannel
import com.example.gymtrack.ui.theme.GymTrackTheme
import com.example.gymtrack.viewmodel.ThemeViewModel
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Comprobar si ya se ha concedido el permiso
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no está concedido, lo solicita al usuario
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // 2. Crear el canal de notificaciones (obligatorio desde Android 8)
        createNotificationChannel(this)

        // 3. Suscribirse al topic "nuevas_rutinas" en Firebase Cloud Messaging
        FirebaseMessaging.getInstance().subscribeToTopic("nuevas_rutinas")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "✅ Suscrito al topic nuevas_rutinas")
                } else {
                    Log.e("FCM", "❌ Error al suscribirse", task.exception)
                }
            }

        // 4. Obtener el token de FCM del dispositivo (útil para debug y pruebas manuales)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "TOKEN: ${task.result}")
            } else {
                Log.e("FCM", "Error al obtener token", task.exception)
            }
        }

        // 5. Programar una notificación diaria (recordatorio/alerta) usando WorkManager
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        // Asegura que solo haya **una** notificación diaria programada (no se duplican)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_gymtrack_notification",
            ExistingPeriodicWorkPolicy.KEEP, // No la reemplaza si ya está programada
            workRequest
        )

        // 6. Arrancar la UI principal de la app con tema dinámico (oscuro/claro)
        enableEdgeToEdge() // Prepara la app para usar el espacio completo de la pantalla
        val themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]

        setContent {
            val darkMode by themeViewModel.darkMode.collectAsState()
            val isReady by themeViewModel.isReady.collectAsState()

            // Solo carga la app cuando el tema está listo (evita flashes raros)
            if (isReady) {
                GymTrackApp(themeViewModel, darkMode)
            }
        }
    }
}
