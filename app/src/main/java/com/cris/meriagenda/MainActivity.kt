package com.cris.meriagenda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cris.meriagenda.data.local.TaskDatabase
import com.cris.meriagenda.ui.screens.AddTaskScreen
import com.cris.meriagenda.ui.screens.CalendarScreen
import com.cris.meriagenda.ui.screens.EditTaskScreen
import com.cris.meriagenda.ui.screens.SettingsScreen
import com.cris.meriagenda.ui.screens.TasksScreen
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

                val navController =
                    rememberNavController()

                val backStackEntry by
                navController.currentBackStackEntryAsState()

                val currentRoute =
                    backStackEntry?.destination?.route

                val mainRoutes = setOf(
                    "today",
                    "tasks",
                    "calendar",
                    "settings"
                )

                Scaffold(
                    bottomBar = {

                        if (currentRoute in mainRoutes) {

                            NavigationBar {

                                val destinations = listOf(
                                    Triple(
                                        "today",
                                        "Hoy",
                                        "🏠"
                                    ),
                                    Triple(
                                        "tasks",
                                        "Tareas",
                                        "📋"
                                    ),
                                    Triple(
                                        "calendar",
                                        "Calendario",
                                        "📅"
                                    ),
                                    Triple(
                                        "settings",
                                        "Ajustes",
                                        "⚙️"
                                    )
                                )

                                destinations.forEach {
                                        (route, label, icon) ->

                                    NavigationBarItem(
                                        selected =
                                            currentRoute == route,

                                        onClick = {

                                            if (
                                                currentRoute != route
                                            ) {

                                                navController.navigate(
                                                    route
                                                ) {

                                                    popUpTo("today") {
                                                        saveState = true
                                                    }

                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },

                                        icon = {
                                            Text(icon)
                                        },

                                        label = {
                                            Text(label)
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    Box(
                        modifier = Modifier.padding(
                            innerPadding
                        )
                    ) {

                        NavHost(
                            navController = navController,
                            startDestination = "today"
                        ) {

                            composable("today") {

                                TodayScreen(
                                    tasks = tasks.filter {
                                        it.dueDate ==
                                                LocalDate.now()
                                    },

                                    onAddTaskClick = {
                                        navController.navigate(
                                            "add"
                                        )
                                    },

                                    onViewTasksClick = {
                                        navController.navigate(
                                            "tasks"
                                        ) {
                                            launchSingleTop = true
                                        }
                                    },

                                    onTaskChecked = {
                                            task,
                                            checked ->

                                        coroutineScope.launch {

                                            database
                                                .taskDao()
                                                .updateTask(
                                                    task.copy(
                                                        isCompleted =
                                                            checked
                                                    )
                                                )
                                        }
                                    }
                                )
                            }

                            composable("tasks") {

                                TasksScreen(
                                    tasks = tasks,

                                    onBack = {

                                        if (
                                            !navController
                                                .popBackStack()
                                        ) {

                                            navController.navigate(
                                                "today"
                                            )
                                        }
                                    },

                                    onAddTaskClick = {

                                        navController.navigate(
                                            "add"
                                        )
                                    },

                                    onTaskChecked = {
                                            task,
                                            checked ->

                                        coroutineScope.launch {

                                            database
                                                .taskDao()
                                                .updateTask(
                                                    task.copy(
                                                        isCompleted =
                                                            checked
                                                    )
                                                )
                                        }
                                    },

                                    onEditTask = { task ->

                                        navController.navigate(
                                            "edit/${task.id}"
                                        )
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

                            composable("calendar") {

                                CalendarScreen(
                                    tasks = tasks,

                                    onTaskChecked = {
                                            task,
                                            checked ->

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

                                    onAddTaskClick = { selectedDate ->

                                        navController.navigate(
                                            "add?date=$selectedDate"
                                        )
                                    }
                                )
                            }

                            composable("settings") {

                                SettingsScreen()
                            }

                            composable(
                                route = "add?date={date}",

                                arguments = listOf(
                                    navArgument("date") {
                                        type = NavType.StringType
                                        defaultValue = ""
                                    }
                                )
                            ) { entry ->

                                val dateText =
                                    entry.arguments
                                        ?.getString("date")
                                        .orEmpty()

                                val initialDate =
                                    try {

                                        if (dateText.isNotBlank()) {
                                            LocalDate.parse(dateText)
                                        } else {
                                            LocalDate.now()
                                        }

                                    } catch (_: Exception) {

                                        LocalDate.now()
                                    }

                                AddTaskScreen(
                                    initialDate = initialDate,

                                    onBack = {

                                        navController
                                            .popBackStack()
                                    },

                                    onSave = { task ->

                                        coroutineScope.launch {

                                            database
                                                .taskDao()
                                                .insertTask(task)

                                            navController
                                                .popBackStack()
                                        }
                                    }
                                )
                            }

                            composable(
                                route = "edit/{taskId}",

                                arguments = listOf(
                                    navArgument("taskId") {
                                        type =
                                            NavType.LongType
                                    }
                                )
                            ) { entry ->

                                val taskId =
                                    entry.arguments
                                        ?.getLong("taskId")

                                val taskToEdit =
                                    tasks.firstOrNull {
                                        it.id == taskId
                                    }

                                if (taskToEdit != null) {

                                    EditTaskScreen(
                                        task = taskToEdit,

                                        onBack = {

                                            navController
                                                .popBackStack()
                                        },

                                        onSave = {
                                                updatedTask ->

                                            coroutineScope.launch {

                                                database
                                                    .taskDao()
                                                    .updateTask(
                                                        updatedTask
                                                    )

                                                navController
                                                    .popBackStack()
                                            }
                                        }
                                    )

                                } else {

                                    Text(
                                        "Cargando tarea..."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}