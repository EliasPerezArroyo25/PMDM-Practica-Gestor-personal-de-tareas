package com.example.gestorpersonaldetareas.network

import com.example.gestorpersonaldetareas.data.local.Tarea
import com.example.gestorpersonaldetareas.model.Prioridad

data class TareaRemotaDto(
    val id: Int,
    val title: String,
    val completed: Boolean
) {
    // Convierte el DTO de internet a nuestra entidad local Room con id = 0 para evitar colisiones
    fun aTareaLocal(): Tarea {
        return Tarea(
            id = 0,
            titulo = title.trim().replaceFirstChar { it.uppercase() },
            descripcion = "Importada desde API externa",
            prioridad = Prioridad.MEDIA,
            completada = completed
        )
    }
}