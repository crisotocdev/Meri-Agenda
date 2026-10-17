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
import com.cris.meriagenda.ui.screens.TodayScreen
import com.cris.meriagenda.ui.theme.MeriAgendaTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

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