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

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationId = Random.nextInt(1000)

        val messages = listOf(
            Pair("¿Hoy entrenas?", "No olvides revisar tus rutinas favoritas"),
            Pair("¿Ya cronometraste tu descanso?", "Recuerda usar el temporizador para optimizar tus entrenos"),
            Pair("Consejo del día", listOf(
                "La constancia vence al talento",
                "Hoy puede ser un gran día para empezar una nueva rutina",
                "El progreso viene del hábito, no de la perfección"
            ).random())
        )

        val (title, message) = messages.random()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(context, "gymtrack_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }


        return Result.success()
    }
}
