package com.cris.meriagenda.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
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

@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    val category: TaskCategory,

    val dueDate: LocalDate,

    val dueTime: LocalTime? = null,

    val priority: TaskPriority = TaskPriority.NORMAL,

    val isCompleted: Boolean = false
)