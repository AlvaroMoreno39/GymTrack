package com.alvaromoreno.gymtrack

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alvaromoreno.gymtrack.navigation.GymTrackApp
import com.alvaromoreno.gymtrack.notification.NotificationWorker
import com.alvaromoreno.gymtrack.notification.createNotificationChannel
import com.alvaromoreno.gymtrack.viewmodel.ThemeViewModel
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

/**
 * MainActivity.kt
 *
 * Esta es la actividad principal de la app GymTrack.
 * Aquí se inicializan los permisos, los canales de notificación, las suscripciones a Firebase Cloud Messaging,
 * y se configura el sistema de WorkManager para notificaciones periódicas.
 * Finalmente, carga la UI principal (GymTrackApp) usando Jetpack Compose y tema dinámico (oscuro/claro).
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1️ Permiso de notificaciones (solo Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // 2️ Crear canal de notificaciones (obligatorio Android 8+)
        createNotificationChannel(this)

        // 3️ Suscripción al topic "nuevas_rutinas" (Firebase Cloud Messaging)
        FirebaseMessaging.getInstance().subscribeToTopic("nuevas_rutinas")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "✅ Suscrito al topic nuevas_rutinas")
                } else {
                    Log.e("FCM", "❌ Error al suscribirse", task.exception)
                }
            }

        // 4️ Obtener token de dispositivo (útil para debug)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "TOKEN: ${task.result}")
            } else {
                Log.e("FCM", "Error al obtener token", task.exception)
            }
        }

        // 5️ Programar notificación diaria usando WorkManager
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_gymtrack_notification",
            ExistingPeriodicWorkPolicy.KEEP, // Evita duplicados si ya existe
            workRequest
        )

        // 6️ Configurar UI principal con tema dinámico
        enableEdgeToEdge() // Ajusta la app para aprovechar bordes/pantalla completa
        val themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]

        setContent {
            val darkMode by themeViewModel.darkMode.collectAsState()
            val isReady by themeViewModel.isReady.collectAsState()

            if (isReady) {
                GymTrackApp(themeViewModel, darkMode)
            }
        }
    }
}

