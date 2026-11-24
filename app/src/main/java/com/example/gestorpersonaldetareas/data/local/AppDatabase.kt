package com.example.gestorpersonaldetareas.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Tarea::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // 1. Conexión con  DAO
    abstract fun tareaDao(): TareaDao

    // 2. Patrón Singleton para evitar múltiples instancias
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tareas_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}