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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import com.cris.meriagenda.data.model.TaskPriority
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddTaskScreen(
    onBack: () -> Unit,
    onSave: (Task) -> Unit
) {

    val context = LocalContext.current

    var title by rememberSaveable {
        mutableStateOf("")
    }

    var category by rememberSaveable {
        mutableStateOf(TaskCategory.COLEGIO)
    }

    var priority by rememberSaveable {
        mutableStateOf(TaskPriority.NORMAL)
    }

    var dueDate by remember {
        mutableStateOf(LocalDate.now())
    }

    var dueTime by remember {
        mutableStateOf<LocalTime?>(null)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        TextButton(
            onClick = onBack
        ) {
            Text("← Volver")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Nueva tarea",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "¿Qué tienes que hacer?",
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
                Text("Escribe la tarea")
            },
            placeholder = {
                Text("Ej: Preparar ficha de Matemática")
            }
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "¿De dónde es?",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
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
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = "Fecha",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedButton(
            onClick = {

                DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        dueDate = LocalDate.of(
                            year,
                            month + 1,
                            day
                        )
                    },
                    dueDate.year,
                    dueDate.monthValue - 1,
                    dueDate.dayOfMonth
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (dueDate == LocalDate.now()) {
                    "Hoy - ${dueDate.format(dateFormatter)}"
                } else {
                    dueDate.format(dateFormatter)
                }
            )
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Text(
            text = "Hora",
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
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
                text = dueTime?.format(timeFormatter)
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

        Spacer(
            modifier = Modifier.height(8.dp)
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
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = {

                val newTask = Task(
                    title = title.trim(),
                    category = category,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    priority = priority
                )

                onSave(newTask)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {

            Text("Guardar tarea")
        }
    }
}