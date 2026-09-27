package com.cris.meriagenda.data.model

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
    val priority: TaskPriority = TaskPriority.NORMAL,
    val isCompleted: Boolean = false
)