package com.cris.meriagenda.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val taskId =
            intent.getLongExtra(
                EXTRA_TASK_ID,
                0L
            )

        val title =
            intent.getStringExtra(
                EXTRA_TITLE
            ).orEmpty()

        val category =
            intent.getStringExtra(
                EXTRA_CATEGORY
            ).orEmpty()

        val priority =
            intent.getStringExtra(
                EXTRA_PRIORITY
            ).orEmpty()

        if (
            taskId == 0L ||
            title.isBlank()
        ) {
            return
        }

        NotificationHelper.showTaskReminder(
            context = context,
            taskId = taskId,
            title = title,
            category = category,
            priority = priority
        )
    }

    companion object {

        const val EXTRA_TASK_ID =
            "task_id"

        const val EXTRA_TITLE =
            "title"

        const val EXTRA_CATEGORY =
            "category"

        const val EXTRA_PRIORITY =
            "priority"
    }
}