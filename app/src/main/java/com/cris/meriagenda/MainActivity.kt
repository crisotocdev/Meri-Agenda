package com.cris.meriagenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.cris.meriagenda.data.local.TaskDatabase
import com.cris.meriagenda.ui.screens.AddTaskScreen
import com.cris.meriagenda.ui.screens.TasksScreen
import com.cris.meriagenda.ui.screens.TodayScreen
import com.cris.meriagenda.ui.theme.MeriAgendaTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.material3.Text
import com.cris.meriagenda.ui.screens.EditTaskScreen

class MainActivity : ComponentActivity() {

    private val database by lazy {
        TaskDatabase.getDatabase(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MeriAgendaTheme {

                val coroutineScope = rememberCoroutineScope()

                val tasks by database
                    .taskDao()
                    .getAllTasks()
                    .collectAsState(
                        initial = emptyList()
                    )

                var currentScreen by rememberSaveable {
                    mutableStateOf("today")
                }

                var editingTaskId by rememberSaveable {
                    mutableStateOf<Long?>(null)
                }

                when (currentScreen) {

                    "edit" -> {

                        val taskToEdit =
                            tasks.firstOrNull {
                                it.id == editingTaskId
                            }

                        if (taskToEdit != null) {

                            EditTaskScreen(
                                task = taskToEdit,

                                onBack = {
                                    editingTaskId = null
                                    currentScreen = "tasks"
                                },

                                onSave = { updatedTask ->

                                    coroutineScope.launch {

                                        database
                                            .taskDao()
                                            .updateTask(updatedTask)

                                        editingTaskId = null
                                        currentScreen = "tasks"
                                    }
                                }
                            )

                        } else {

                            Text("Cargando tarea...")
                        }
                    }

                    "today" -> {

                        TodayScreen(
                            tasks = tasks.filter {
                                it.dueDate == LocalDate.now()
                            },

                            onAddTaskClick = {
                                currentScreen = "add"
                            },

                            onViewTasksClick = {
                                currentScreen = "tasks"
                            },

                            onTaskChecked = { task, checked ->

                                coroutineScope.launch {

                                    database
                                        .taskDao()
                                        .updateTask(
                                            task.copy(
                                                isCompleted = checked
                                            )
                                        )
                                }
                            }
                        )
                    }

                    "tasks" -> {

                        TasksScreen(
                            tasks = tasks,

                            onBack = {
                                currentScreen = "today"
                            },

                            onAddTaskClick = {
                                currentScreen = "add"
                            },

                            onTaskChecked = { task, checked ->

                                coroutineScope.launch {

                                    database
                                        .taskDao()
                                        .updateTask(
                                            task.copy(
                                                isCompleted = checked
                                            )
                                        )
                                }
                            },

                            onEditTask = { task ->

                                editingTaskId = task.id
                                currentScreen = "edit"
                            },

                            onDeleteTask = { task ->

                                coroutineScope.launch {

                                    database
                                        .taskDao()
                                        .deleteTask(task)
                                }
                            }
                        )
                    }

                    "add" -> {

                        AddTaskScreen(
                            onBack = {
                                currentScreen = "today"
                            },

                            onSave = { task ->

                                coroutineScope.launch {

                                    database
                                        .taskDao()
                                        .insertTask(task)

                                    currentScreen = "today"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}