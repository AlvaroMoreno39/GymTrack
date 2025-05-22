package com.example.gymtrack.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gymtrack.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.content.ContextCompat

/**
 * MyFirebaseMessagingService.kt
 *
 * Servicio que gestiona la recepción de notificaciones push en la app GymTrack usando Firebase Cloud Messaging.
 * Esta clase extiende FirebaseMessagingService y define cómo se muestran las notificaciones en el dispositivo.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Este método se llama automáticamente cada vez que se recibe un mensaje FCM push (en segundo plano o primer plano)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Comprueba si el mensaje recibido contiene una notificación (puede contener también solo datos)
        remoteMessage.notification?.let {
            // Construye la notificación usando NotificationCompat para asegurar compatibilidad con todas las versiones
            val builder = NotificationCompat.Builder(this, "rutinas_channel")
                .setSmallIcon(R.drawable.ic_notification)     // Icono pequeño de la notificación (debe existir en drawable)
                .setContentTitle(it.title)                    // Título del mensaje (ej: "Nueva rutina disponible")
                .setContentText(it.body)                      // Cuerpo de la notificación (mensaje descriptivo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)// Alta prioridad para mostrar de inmediato
                .setAutoCancel(true)                          // Cierra la notificación al pulsar sobre ella

            // Solo muestra la notificación si:
            // 1. La versión de Android es anterior a TIRAMISU (Android 13) donde no se requiere permiso explícito
            // 2. O bien, si el usuario ya ha concedido el permiso de POST_NOTIFICATIONS
            if (
                Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                // Envía la notificación al sistema, usando un ID único basado en la hora actual (para que no se sobreescriban)
                NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }
}
