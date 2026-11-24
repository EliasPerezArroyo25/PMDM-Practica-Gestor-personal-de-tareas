package com.example.gestorpersonaldetareas.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    // Nueva consulta SQL para la Opción B
    @Query("SELECT * FROM tareas WHERE completada = 0 ORDER BY id DESC")
    fun obtenerPendientes(): Flow<List<Tarea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)
}
