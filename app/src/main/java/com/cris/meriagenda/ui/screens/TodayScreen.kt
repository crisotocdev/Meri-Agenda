package com.cris.meriagenda.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import com.cris.meriagenda.data.model.TaskPriority
import java.time.format.DateTimeFormatter

@Composable
fun TodayScreen(
    tasks: List<Task>,
    onAddTaskClick: () -> Unit,
    onViewTasksClick: () -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick
            ) {
                Text("＋ Nueva tarea")
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "MERI AGENDA - CODIGO NUEVO 2026🌷",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Estas son tus tareas para hoy",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Text(
                    text = "Hoy",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = onViewTasksClick
                ) {
                    Text("Ver todas las tareas →")
                }
            }

            if (tasks.isEmpty()) {

                item {
                    Text(
                        text = "No tienes tareas pendientes para hoy 🎉",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            } else {

                items(
                    items = tasks,
                    key = { task ->
                        task.id
                    }
                ) { task ->

                    TaskCard(
                        task = task,
                        onCheckedChange = { checked ->
                            onTaskChecked(
                                task,
                                checked
                            )
                        }
                    )
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(100.dp)
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {

    val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    val cardColor = when (task.priority) {

        TaskPriority.NORMAL ->
            MaterialTheme.colorScheme.surfaceVariant

        TaskPriority.IMPORTANTE ->
            MaterialTheme.colorScheme.tertiaryContainer

        TaskPriority.URGENTE ->
            MaterialTheme.colorScheme.errorContainer
    }

    val borderColor = when (task.priority) {

        TaskPriority.NORMAL ->
            MaterialTheme.colorScheme.outlineVariant

        TaskPriority.IMPORTANTE ->
            MaterialTheme.colorScheme.tertiary

        TaskPriority.URGENTE ->
            MaterialTheme.colorScheme.error
    }

    val priorityTextColor = when (task.priority) {

        TaskPriority.NORMAL ->
            MaterialTheme.colorScheme.onSurfaceVariant

        TaskPriority.IMPORTANTE ->
            MaterialTheme.colorScheme.onTertiaryContainer

        TaskPriority.URGENTE ->
            MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),

        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),

        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {

                Text(
                    text = when (task.category) {

                        TaskCategory.COLEGIO ->
                            "🏫 Colegio"

                        TaskCategory.UNIVERSIDAD ->
                            "🎓 Universidad"
                    },
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                task.dueTime?.let { time ->

                    Text(
                        text = "🕐 ${time.format(timeFormatter)}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = when (task.priority) {

                        TaskPriority.NORMAL ->
                            "● Normal"

                        TaskPriority.IMPORTANTE ->
                            "★ Importante"

                        TaskPriority.URGENTE ->
                            "⚠ Urgente"
                    },

                    style = MaterialTheme.typography.labelMedium,

                    color = priorityTextColor,

                    fontWeight =
                        if (task.priority == TaskPriority.NORMAL) {
                            FontWeight.Normal
                        } else {
                            FontWeight.Bold
                        }
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                if (onEditClick != null) {

                    TextButton(
                        onClick = onEditClick
                    ) {
                        Text("Editar")
                    }
                }

                if (onDeleteClick != null) {

                    TextButton(
                        onClick = onDeleteClick
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}