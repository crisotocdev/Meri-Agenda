package com.cris.meriagenda.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.cris.meriagenda.data.model.Task
import java.time.LocalDateTime
import java.time.ZoneId

object ReminderScheduler {

    fun scheduleReminder(
        context: Context,
        task: Task
    ) {

        if (task.id == 0L) {
            return
        }

        if (
            task.isCompleted ||
            task.dueTime == null
        ) {

            cancelReminder(
                context,
                task.id
            )

            return
        }

        val taskDateTime =
            LocalDateTime.of(
                task.dueDate,
                task.dueTime
            )

        val triggerAtMillis =
            taskDateTime
                .atZone(
                    ZoneId.systemDefault()
                )
                .toInstant()
                .toEpochMilli()

        if (
            triggerAtMillis <=
            System.currentTimeMillis()
        ) {

            cancelReminder(
                context,
                task.id
            )

            return
        }

        val alarmManager =
            context.getSystemService(
                AlarmManager::class.java
            )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            return
        }

        val pendingIntent =
            createPendingIntent(
                context,
                task
            )

        alarmManager.cancel(
            pendingIntent
        )

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M
        ) {

            alarmManager
                .setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )

        } else {

            alarmManager
                .setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
        }
    }

    fun cancelReminder(
        context: Context,
        taskId: Long
    ) {

        val alarmManager =
            context.getSystemService(
                AlarmManager::class.java
            )

        val intent =
            Intent(
                context,
                TaskReminderReceiver::class.java
            ).apply {

                action =
                    "com.cris.meriagenda.REMINDER_$taskId"
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                taskId.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or
                        PendingIntent.FLAG_IMMUTABLE
            )

        if (pendingIntent != null) {

            alarmManager.cancel(
                pendingIntent
            )

            pendingIntent.cancel()
        }
    }

    private fun createPendingIntent(
        context: Context,
        task: Task
    ): PendingIntent {

        val intent =
            Intent(
                context,
                TaskReminderReceiver::class.java
            ).apply {

                action =
                    "com.cris.meriagenda.REMINDER_${task.id}"

                putExtra(
                    TaskReminderReceiver.EXTRA_TASK_ID,
                    task.id
                )

                putExtra(
                    TaskReminderReceiver.EXTRA_TITLE,
                    task.title
                )

                putExtra(
                    TaskReminderReceiver.EXTRA_CATEGORY,
                    task.category.name
                )

                putExtra(
                    TaskReminderReceiver.EXTRA_PRIORITY,
                    task.priority.name
                )
            }

        return PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
    }
}