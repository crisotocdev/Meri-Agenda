package com.cris.meriagenda.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cris.meriagenda.data.model.Task
import com.cris.meriagenda.data.model.TaskCategory
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    tasks: List<Task>,
    onTaskChecked: (Task, Boolean) -> Unit
) {

    val today = LocalDate.now()

    var currentMonth by remember {
        mutableStateOf(
            YearMonth.from(today)
        )
    }

    var selectedDate by remember {
        mutableStateOf(today)
    }

    val tasksByDate = tasks.groupBy {
        it.dueDate
    }

    val selectedDateTasks =
        tasksByDate[selectedDate]
            .orEmpty()
            .sortedBy {
                it.dueTime ?: LocalTime.MAX
            }

    val monthFormatter =
        DateTimeFormatter.ofPattern(
            "MMMM yyyy",
            Locale("es", "ES")
        )

    val selectedDateFormatter =
        DateTimeFormatter.ofPattern(
            "dd/MM/yyyy"
        )

    val monthTitle =
        currentMonth
            .atDay(1)
            .format(monthFormatter)
            .replaceFirstChar {
                it.uppercase()
            }

    val calendarCells =
        buildCalendarCells(currentMonth)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(horizontal = 16.dp)
    ) {

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Calendario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Organiza las tareas de Meri por fecha 📅",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            TextButton(
                onClick = {

                    currentMonth =
                        currentMonth.minusMonths(1)

                    selectedDate =
                        currentMonth.atDay(1)
                }
            ) {
                Text("←")
            }

            Text(
                text = monthTitle,
                style =
                    MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {

                    currentMonth =
                        currentMonth.plusMonths(1)

                    selectedDate =
                        currentMonth.atDay(1)
                }
            ) {
                Text("→")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        WeekHeader()

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        calendarCells
            .chunked(7)
            .forEach { week ->

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    week.forEach { date ->

                        if (date == null) {

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                            )

                        } else {

                            val dayTasks =
                                tasksByDate[date]
                                    .orEmpty()

                            CalendarDay(
                                date = date,

                                tasks = dayTasks,

                                isToday =
                                    date == today,

                                isSelected =
                                    date == selectedDate,

                                onClick = {
                                    selectedDate = date
                                },

                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                            )
                        }
                    }
                }
            }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        CalendarLegend()

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        Text(
            text = when {

                selectedDate == today ->
                    "Hoy"

                selectedDate ==
                        today.plusDays(1) ->
                    "Mañana"

                else ->
                    selectedDate.format(
                        selectedDateFormatter
                    )
            },

            style =
                MaterialTheme.typography.titleLarge,

            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (selectedDateTasks.isEmpty()) {

            Text(
                text =
                    "No hay tareas para esta fecha 🌷",
                style =
                    MaterialTheme.typography.bodyLarge
            )

        } else {

            selectedDateTasks.forEach { task ->

                TaskCard(
                    task = task,

                    onCheckedChange = { checked ->

                        onTaskChecked(
                            task,
                            checked
                        )
                    }
                )

                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(100.dp)
        )
    }
}

@Composable
private fun WeekHeader() {

    val days =
        listOf(
            "Lun",
            "Mar",
            "Mié",
            "Jue",
            "Vie",
            "Sáb",
            "Dom"
        )

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        days.forEach { day ->

            Text(
                text = day,
                modifier =
                    Modifier.weight(1f),
                style =
                    MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    tasks: List<Task>,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val hasSchoolTasks =
        tasks.any {
            it.category ==
                    TaskCategory.COLEGIO
        }

    val hasUniversityTasks =
        tasks.any {
            it.category ==
                    TaskCategory.UNIVERSIDAD
        }

    val containerColor =
        if (isSelected) {

            MaterialTheme
                .colorScheme
                .primaryContainer

        } else {

            MaterialTheme
                .colorScheme
                .surfaceVariant
        }

    val border =
        when {

            isSelected ->
                BorderStroke(
                    2.dp,
                    MaterialTheme
                        .colorScheme
                        .primary
                )

            isToday ->
                BorderStroke(
                    2.dp,
                    MaterialTheme
                        .colorScheme
                        .tertiary
                )

            else ->
                null
        }

    Surface(
        modifier = modifier
            .clickable {
                onClick()
            },

        shape =
            MaterialTheme.shapes.medium,

        color = containerColor,

        border = border
    ) {

        Column(
            modifier =
                Modifier.padding(5.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally,

            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text =
                    date.dayOfMonth.toString(),

                fontWeight =
                    if (
                        isSelected ||
                        isToday
                    ) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    }
            )

            if (tasks.isNotEmpty()) {

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            3.dp
                        )
                ) {

                    if (hasSchoolTasks) {

                        Spacer(
                            modifier =
                                Modifier
                                    .size(6.dp)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .primary,
                                        CircleShape
                                    )
                        )
                    }

                    if (
                        hasUniversityTasks
                    ) {

                        Spacer(
                            modifier =
                                Modifier
                                    .size(6.dp)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .tertiary,
                                        CircleShape
                                    )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarLegend() {

    Row(
        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.spacedBy(16.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Spacer(
                modifier =
                    Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme
                                .colorScheme
                                .primary,
                            CircleShape
                        )
            )

            Text(
                text = "  Colegio",
                style =
                    MaterialTheme
                        .typography
                        .labelMedium
            )
        }

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Spacer(
                modifier =
                    Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme
                                .colorScheme
                                .tertiary,
                            CircleShape
                        )
            )

            Text(
                text = "  Universidad",
                style =
                    MaterialTheme
                        .typography
                        .labelMedium
            )
        }
    }
}

private fun buildCalendarCells(
    month: YearMonth
): List<LocalDate?> {

    val firstDay =
        month.atDay(1)

    /*
     * dayOfWeek.value:
     *
     * Lunes = 1
     * ...
     * Domingo = 7
     */
    val startOffset =
        firstDay.dayOfWeek.value - 1

    val daysInMonth =
        month.lengthOfMonth()

    val requiredCells =
        startOffset + daysInMonth

    val totalCells =
        ((requiredCells + 6) / 7) * 7

    return List(totalCells) { index ->

        val day =
            index - startOffset + 1

        if (
            day in 1..daysInMonth
        ) {

            month.atDay(day)

        } else {

            null
        }
    }
}