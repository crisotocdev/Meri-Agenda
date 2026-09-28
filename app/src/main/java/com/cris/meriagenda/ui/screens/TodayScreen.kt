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
                    text = "Buenos días, Meri 🌷",
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
    onCheckedChange: (Boolean) -> Unit
) {

    val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    Card(
        modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.padding(start = 8.dp)
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

                when (task.priority) {

                    TaskPriority.NORMAL -> Unit

                    TaskPriority.IMPORTANTE -> {
                        Text(
                            text = "★ Importante",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    TaskPriority.URGENTE -> {
                        Text(
                            text = "⚠ Urgente",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}