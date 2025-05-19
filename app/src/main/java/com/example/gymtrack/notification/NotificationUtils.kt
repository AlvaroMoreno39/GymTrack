package com.example.gymtrack.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "GymTrack Notifications"
        val descriptionText = "Canal para recordatorios y consejos"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("gymtrack_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
