package com.cris.meriagenda.data.model

import java.time.LocalDate
import java.time.LocalTime

enum class TaskCategory {
    COLEGIO,
    UNIVERSIDAD
}

enum class TaskPriority {
    NORMAL,
    IMPORTANTE,
    URGENTE
}

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: TaskCategory,
    val dueDate: LocalDate,
    val dueTime: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val isCompleted: Boolean = false
)