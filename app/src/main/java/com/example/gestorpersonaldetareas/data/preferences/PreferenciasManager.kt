package com.example.gestorpersonaldetareas.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única de DataStore asociada al Context de la app
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_tareas")

class PreferenciasManager(private val context: Context) {

    companion object {
        val CLAVE_OCULTAR_COMPLETADAS = booleanPreferencesKey("ocultar_completadas")
    }

    // Flow reactivo que emite true o false (por defecto false: mostrar todas)
    val ocultarCompletadasFlow: Flow<Boolean> = context.dataStore.data
        .map { preferencias ->
            preferencias[CLAVE_OCULTAR_COMPLETADAS] ?: false
        }

    // Función suspendida para guardar el cambio cuando el usuario toca el Switch
    suspend fun guardarOcultarCompletadas(ocultar: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[CLAVE_OCULTAR_COMPLETADAS] = ocultar
        }
    }
}