package com.example.gymtrack

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔒 Solicitar permiso de notificaciones
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

        // 🔔 Crear canal de notificaciones
        createNotificationChannel(this)

        // ⏱️ Programar notificación diaria
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_gymtrack_notification",
            ExistingPeriodicWorkPolicy.KEEP, // No duplicar si ya estaba
            workRequest
        )

        // 🎨 UI
        enableEdgeToEdge()
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