package com.cris.meriagenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import com.cris.meriagenda.data.model.TaskPriority
import com.cris.meriagenda.ui.screens.AddTaskScreen
import com.cris.meriagenda.ui.screens.TodayScreen
import com.cris.meriagenda.ui.theme.MeriAgendaTheme
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            MeriAgendaTheme {

                var currentScreen by rememberSaveable {
                    mutableStateOf("today")
                }

                val tasks = remember {

                    mutableStateListOf(
                        Task(
                            title = "Preparar ficha de Matemática",
                            category = TaskCategory.COLEGIO,
                            dueDate = LocalDate.now()
                        ),
                        Task(
                            title = "Revisar cuadernos",
                            category = TaskCategory.COLEGIO,
                            dueDate = LocalDate.now(),
                            dueTime = LocalTime.of(15, 30)
                        ),
                        Task(
                            title = "Leer material para la clase",
                            category = TaskCategory.UNIVERSIDAD,
                            dueDate = LocalDate.now()
                        ),
                        Task(
                            title = "Entregar avance de investigación",
                            category = TaskCategory.UNIVERSIDAD,
                            dueDate = LocalDate.now(),
                            dueTime = LocalTime.of(23, 59),
                            priority = TaskPriority.IMPORTANTE
                        )
                    )
                }

                when (currentScreen) {

                    "today" -> {

                        TodayScreen(
                            tasks = tasks.filter {
                                it.dueDate == LocalDate.now()
                            },
                            onAddTaskClick = {
                                currentScreen = "add"
                            },
                            onTaskChecked = { task, checked ->

                                val index = tasks.indexOfFirst {
                                    it.id == task.id
                                }

                                if (index != -1) {

                                    tasks[index] =
                                        task.copy(
                                            isCompleted = checked
                                        )
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

                                tasks.add(task)

                                currentScreen = "today"
                            }
                        )
                    }
                }
            }
        }
    }
}