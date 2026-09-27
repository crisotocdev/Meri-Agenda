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

data class ExampleTask(
    val title: String,
    val category: String,
    val important: Boolean = false
)

@Composable
fun TodayScreen(tasks: List<Task>,
                onAddTaskClick: () -> Unit,
                onTaskChecked: (Task, Boolean) -> Unit) {

    val tasks = listOf(
        ExampleTask(
            title = "Preparar ficha de Matemática",
            category = "Colegio"
        ),
        ExampleTask(
            title = "Revisar cuadernos",
            category = "Colegio"
        ),
        ExampleTask(
            title = "Leer material para la clase",
            category = "Universidad"
        ),
        ExampleTask(
            title = "Entregar avance de investigación",
            category = "Universidad",
            important = true
        )
    )

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { }
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

            items(tasks) { task ->
                TaskCard(task)
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
fun TaskCard(task: ExampleTask) {

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
                checked = false,
                onCheckedChange = { }
            )

            Column(
                modifier = Modifier.padding(start = 8.dp)
            ) {

                Text(
                    text = if (task.category == "Colegio") {
                        "🏫 ${task.category}"
                    } else {
                        "🎓 ${task.category}"
                    },
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                if (task.important) {
                    Text(
                        text = "⚠ Importante",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}