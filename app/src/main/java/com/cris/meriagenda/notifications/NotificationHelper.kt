package com.cris.meriagenda.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cris.meriagenda.R

object NotificationHelper {

    const val CHANNEL_ID = "task_reminders"

    fun createNotificationChannel(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Recordatorios de las tareas de Meri Agenda"
            }

            val notificationManager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            notificationManager
                .createNotificationChannel(
                    channel
                )
        }
    }

    fun showTestNotification(
        context: Context
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_notification
                )
                .setContentTitle(
                    "Meri Agenda 💜"
                )
                .setContentText(
                    "Las notificaciones están funcionando correctamente."
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                1001,
                notification
            )
    }

    fun showTaskReminder(
        context: Context,
        taskId: Long,
        title: String,
        category: String,
        priority: String
    ) {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val categoryText =
            when (category) {

                "COLEGIO" ->
                    "🏫 Colegio"

                "UNIVERSIDAD" ->
                    "🎓 Universidad"

                else ->
                    category
            }

        val priorityText =
            when (priority) {

                "URGENTE" ->
                    "⚠ Urgente"

                "IMPORTANTE" ->
                    "★ Importante"

                else ->
                    "Normal"
            }

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_notification
                )
                .setContentTitle(title)
                .setContentText(
                    "$categoryText • $priorityText"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(
                taskId.hashCode(),
                notification
            )
    }
}