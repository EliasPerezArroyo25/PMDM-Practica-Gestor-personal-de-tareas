package com.example.gestorpersonaldetareas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gestorpersonaldetareas.model.Prioridad

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val prioridad: Prioridad,
    val completada: Boolean = false
)
