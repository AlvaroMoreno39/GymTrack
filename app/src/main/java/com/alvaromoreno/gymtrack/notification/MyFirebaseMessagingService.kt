package com.alvaromoreno.gymtrack.notification

import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alvaromoreno.gymtrack.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import androidx.core.content.ContextCompat

/**
 * MyFirebaseMessagingService.kt
 *
 * Servicio personalizado para recibir notificaciones push en GymTrack usando Firebase Cloud Messaging (FCM).
 *
 * Escucha los mensajes entrantes enviados desde Firebase.
 * Construye y muestra una notificación local en el dispositivo al recibir un mensaje.
 * Usa el canal de notificación "gymtrack_channel" para asegurar compatibilidad con Android 8+.
 * Comprueba el permiso POST_NOTIFICATIONS en Android 13+ antes de mostrar la notificación.
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Este método se llama automáticamente cuando se recibe un mensaje push desde Firebase.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            // Construye la notificación visual usando NotificationCompat
            val builder = NotificationCompat.Builder(this, "gymtrack_channel")
                .setSmallIcon(R.drawable.ic_fitness_center)   // Icono de la notificación (pequeño)
                .setContentTitle(it.title)                    // Título del mensaje
                .setContentText(it.body)                     // Cuerpo del mensaje
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para notificaciones emergentes
                .setAutoCancel(true)                         // Se descarta al pulsar

            // Solo muestra la notificación si:
            // - Android es menor que 13 (TIRAMISU), donde no se requiere permiso explícito
            // - O, si es Android 13+, se tiene el permiso POST_NOTIFICATIONS concedido
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }
}

