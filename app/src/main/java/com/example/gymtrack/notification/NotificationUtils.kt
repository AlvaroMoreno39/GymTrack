package com.example.gymtrack.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * createNotificationChannel
 *
 * Esta función crea un canal de notificaciones para la app GymTrack.
 * Los canales de notificaciones son obligatorios a partir de Android 8.0 (API 26, Oreo)
 * y permiten a los usuarios controlar las preferencias de notificación de la app.
 *
 * Se debe llamar a esta función una vez al iniciar la app (por ejemplo, en el onCreate de MainActivity).
 *
 * @param context Contexto de la aplicación necesario para acceder al servicio de notificaciones.
 */
fun createNotificationChannel(context: Context) {
    // Solo crea el canal si el dispositivo está en Android Oreo (API 26) o superior
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "GymTrack Notifications"                   // Nombre visible del canal para el usuario
        val descriptionText = "Canal para recordatorios y consejos" // Descripción del canal (opcional)
        val importance = NotificationManager.IMPORTANCE_DEFAULT      // Nivel de importancia (notificaciones normales)

        // Crea el objeto NotificationChannel con el ID, nombre e importancia
        val channel = NotificationChannel("gymtrack_channel", name, importance).apply {
            description = descriptionText
        }

        // Obtiene el servicio del sistema responsable de gestionar notificaciones
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Registra el canal en el sistema (si ya existe, no hace nada)
        notificationManager.createNotificationChannel(channel)
    }
}

