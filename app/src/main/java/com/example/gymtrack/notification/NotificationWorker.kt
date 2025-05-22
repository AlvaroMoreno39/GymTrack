package com.example.gymtrack.notification

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.gymtrack.MainActivity
import com.example.gymtrack.R
import kotlin.random.Random

/**
 * NotificationWorker.kt
 *
 * Esta clase define un Worker personalizado que permite enviar notificaciones locales
 * de manera periódica o programada utilizando WorkManager.
 * Ideal para recordatorios diarios, motivación o recomendaciones aunque la app no esté abierta.
 */

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    // Este método se ejecuta en segundo plano cuando WorkManager lanza el Worker
    override fun doWork(): Result {
        // ID aleatorio para que cada notificación sea única y no se sobreescriba otra anterior
        val notificationId = Random.nextInt(1000)

        // Lista de mensajes posibles para mostrar en la notificación (título y cuerpo)
        val messages = listOf(
            Pair("¿Hoy entrenas?", "No olvides revisar tus rutinas favoritas"),
            Pair("¿Ya cronometraste tu descanso?", "Recuerda usar el temporizador para optimizar tus entrenos"),
            Pair("Consejo del día", listOf(
                "La constancia vence al talento",
                "Hoy puede ser un gran día para empezar una nueva rutina",
                "El progreso viene del hábito, no de la perfección"
            ).random()) // El consejo se selecciona aleatoriamente
        )

        // Selecciona un mensaje al azar de la lista
        val (title, message) = messages.random()

        // Intent para lanzar la MainActivity cuando el usuario pulse la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Crea un PendingIntent que mantiene la pila de actividades para la navegación correcta
        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        // Construye la notificación con todos sus parámetros visuales y de comportamiento
        val builder = NotificationCompat.Builder(context, "gymtrack_channel")
            .setSmallIcon(R.drawable.ic_fitness_center)   // Icono de la notificación
            .setContentTitle(title)                     // Título dinámico
            .setContentText(message)                    // Mensaje dinámico
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad media
            .setContentIntent(pendingIntent)            // Acción al pulsar la notificación
            .setAutoCancel(true)                        // Se descarta al pulsar

        // Comprueba si la app tiene permiso para mostrar notificaciones (Android 13+)
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Publica la notificación
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }

        // Indica a WorkManager que el trabajo se completó correctamente
        return Result.success()
    }
}
