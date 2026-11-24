package com.example.gestorpersonaldetareas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gestorpersonaldetareas.data.local.AppDatabase
import com.example.gestorpersonaldetareas.data.local.Tarea
import com.example.gestorpersonaldetareas.ui.PantallaPrincipal
import com.example.gestorpersonaldetareas.ui.theme.GestorPersonalDeTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Obtener la instancia de la base de datos y el DAO
        val database = AppDatabase.getDatabase(applicationContext)
        val tareaDao = database.tareaDao()

        setContent {
            GestorPersonalDeTareasTheme {
                // 2. Pasar el tareaDao al Composable raíz
                PantallaPrincipal(
                    tareaDao = tareaDao,
                    onCompartirTarea = { tarea ->
                        compartirTarea(tarea)
                    }
                )
            }
        }
    }

// Opción A: Lógica de Intent en MainActivity + Plantilla estructurada multilínea
private fun compartirTarea(tarea: Tarea) {
    val estadoTexto = if (tarea.completada) "Completada" else "Pendiente"

    // Construimos la plantilla multilínea omitiendo la descripción si está vacía
    val textoCompartir = buildString {
        appendLine("Tarea: ${tarea.titulo}")
        if (tarea.descripcion.isNotBlank()) {
            appendLine("Descripción: ${tarea.descripcion}")
        }
        appendLine("Prioridad: ${tarea.prioridad.etiqueta}")
        append("Estado: $estadoTexto")
    }

    val intentEnvio = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, tarea.titulo)
        putExtra(Intent.EXTRA_TEXT, textoCompartir)
    }

    val chooser = Intent.createChooser(intentEnvio, "Compartir tarea mediante...")
    startActivity(chooser)
}
}