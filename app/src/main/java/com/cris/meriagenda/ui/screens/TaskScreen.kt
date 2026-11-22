package com.cris.meriagenda.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private enum class TaskFilter {
    TODAS,
    COLEGIO,
    UNIVERSIDAD
}

@Composable
fun TasksScreen(
    tasks: List<Task>,
    onBack: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskChecked: (Task, Boolean) -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit
) {

    var selectedFilter by remember {
        mutableStateOf(TaskFilter.TODAS)
    }

    var showCompleted by remember {
        mutableStateOf(false)
    }

    var taskToDelete by remember {
        mutableStateOf<Task?>(null)
    }

    val categoryFilteredTasks = when (selectedFilter) {

        TaskFilter.TODAS ->
            tasks

        TaskFilter.COLEGIO ->
            tasks.filter {
                it.category == TaskCategory.COLEGIO
            }

        TaskFilter.UNIVERSIDAD ->
            tasks.filter {
                it.category == TaskCategory.UNIVERSIDAD
            }
    }

    val filteredTasks = if (showCompleted) {

        categoryFilteredTasks

    } else {

        categoryFilteredTasks.filter {
            !it.isCompleted
        }
    }

    val groupedTasks = filteredTasks
        .sortedWith(
            compareBy<Task> { it.dueDate }
                .thenBy { it.dueTime }
        )
        .groupBy {
            it.dueDate
        }

    Scaffold(
        floatingActionButton = {

            ExtendedFloatingActionButton(
                onClick = onAddTaskClick
            ) {
                Text("＋ Nueva tarea")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            TextButton(
                onClick = onBack
            ) {
                Text("← Hoy")
            }

            Text(
                text = "Todas las tareas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Colegio y universidad en un solo lugar 🌷",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FilterChip(
                    selected = selectedFilter == TaskFilter.TODAS,
                    onClick = {
                        selectedFilter = TaskFilter.TODAS
                    },
                    label = {
                        Text("Todas")
                    }
                )

                FilterChip(
                    selected = selectedFilter == TaskFilter.COLEGIO,
                    onClick = {
                        selectedFilter = TaskFilter.COLEGIO
                    },
                    label = {
                        Text("🏫 Colegio")
                    }
                )

                FilterChip(
                    selected = selectedFilter == TaskFilter.UNIVERSIDAD,
                    onClick = {
                        selectedFilter = TaskFilter.UNIVERSIDAD
                    },
                    label = {
                        Text("🎓 Universidad")
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            FilterChip(
                selected = showCompleted,
                onClick = {
                    showCompleted = !showCompleted
                },
                label = {
                    Text(
                        text = if (showCompleted) {
                            "✓ Ocultar completadas"
                        } else {
                            "✓ Mostrar completadas"
                        }
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (filteredTasks.isEmpty()) {

                Text(
                    text = "No hay tareas en esta categoría.",
                    style = MaterialTheme.typography.bodyLarge
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    groupedTasks.forEach { (date, tasksForDate) ->

                        item {

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = getDateSectionTitle(date),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(
                            items = tasksForDate,
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
                                },

                                onEditClick = {
                                    onEditTask(task)
                                },

                                onDeleteClick = {
                                    taskToDelete = task
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
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                FilterChip(
                    selected = showCompleted,
                    onClick = {
                        showCompleted = !showCompleted
                    },
                    label = {
                        Text(
                            if (showCompleted) {
                                "✓ Ocultar completadas"
                            } else {
                                "✓ Mostrar completadas"
                            }
                        )
                    }
                )
            }
        }
    }

    taskToDelete?.let { task ->

        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },

            title = {
                Text("Eliminar tarea")
            },

            text = {
                Text(
                    "¿Quieres eliminar \"${task.title}\"? " +
                            "Esta acción no se puede deshacer."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        onDeleteTask(task)

                        taskToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun getDateSectionTitle(
    date: LocalDate
): String {

    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    return when {

        date == today ->
            "Hoy"

        date == tomorrow ->
            "Mañana"

        date.isBefore(today) ->
            "Atrasada · ${
                date.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }"

        else ->
            "Próxima · ${
                date.format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }"
    }
}