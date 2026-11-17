package com.cris.meriagenda.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import com.cris.meriagenda.data.model.TaskPriority
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EditTaskScreen(
    task: Task,
    onBack: () -> Unit,
    onSave: (Task) -> Unit
) {

    val context = LocalContext.current

    var title by remember(task.id) {
        mutableStateOf(task.title)
    }

    var category by remember(task.id) {
        mutableStateOf(task.category)
    }

    var priority by remember(task.id) {
        mutableStateOf(task.priority)
    }

    var dueDate by remember(task.id) {
        mutableStateOf(task.dueDate)
    }

    var dueTime by remember(task.id) {
        mutableStateOf(task.dueTime)
    }

    val dateFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {
            Text("← Volver")
        }

        Text(
            text = "Editar tarea",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Modifica lo que necesites 🌷",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Título")
            }
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "Categoría",
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = category == TaskCategory.COLEGIO,
                onClick = {
                    category = TaskCategory.COLEGIO
                },
                label = {
                    Text("🏫 Colegio")
                }
            )

            FilterChip(
                selected = category == TaskCategory.UNIVERSIDAD,
                onClick = {
                    category = TaskCategory.UNIVERSIDAD
                },
                label = {
                    Text("🎓 Universidad")
                }
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Text(
            text = "Fecha",
            fontWeight = FontWeight.Bold
        )

        OutlinedButton(
            onClick = {

                DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        dueDate = dueDate
                            .withYear(year)
                            .withMonth(month + 1)
                            .withDayOfMonth(day)
                    },
                    dueDate.year,
                    dueDate.monthValue - 1,
                    dueDate.dayOfMonth
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                dueDate.format(dateFormatter)
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Hora",
            fontWeight = FontWeight.Bold
        )

        OutlinedButton(
            onClick = {

                val initialTime =
                    dueTime ?: LocalTime.now()

                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        dueTime = LocalTime.of(
                            hour,
                            minute
                        )
                    },
                    initialTime.hour,
                    initialTime.minute,
                    true
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                dueTime?.format(timeFormatter)
                    ?: "Sin hora específica"
            )
        }

        if (dueTime != null) {

            TextButton(
                onClick = {
                    dueTime = null
                }
            ) {
                Text("Quitar hora")
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Prioridad",
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = priority == TaskPriority.NORMAL,
                onClick = {
                    priority = TaskPriority.NORMAL
                },
                label = {
                    Text("Normal")
                }
            )

            FilterChip(
                selected = priority == TaskPriority.IMPORTANTE,
                onClick = {
                    priority = TaskPriority.IMPORTANTE
                },
                label = {
                    Text("Importante")
                }
            )
        }

        FilterChip(
            selected = priority == TaskPriority.URGENTE,
            onClick = {
                priority = TaskPriority.URGENTE
            },
            label = {
                Text("⚠ Urgente")
            }
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        Button(
            onClick = {

                onSave(
                    task.copy(
                        title = title.trim(),
                        category = category,
                        dueDate = dueDate,
                        dueTime = dueTime,
                        priority = priority
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {

            Text("Guardar cambios")
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}