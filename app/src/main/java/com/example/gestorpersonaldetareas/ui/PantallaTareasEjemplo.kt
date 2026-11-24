package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.network.RetrofitClient
import com.example.gestorpersonaldetareas.network.TareaRemotaDto

private sealed interface EstadoRed {
    data object Cargando : EstadoRed
    data class Exito(val tareas: List<TareaRemotaDto>) : EstadoRed
    data class Error(val mensaje: String) : EstadoRed
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareasEjemplo(
    onImportarTarea: (TareaRemotaDto) -> Unit,
    onVolver: () -> Unit
) {
    var estado by remember { mutableStateOf<EstadoRed>(EstadoRed.Cargando) }
    var intentoCarga by remember { mutableIntStateOf(0) }

    // Se ejecuta al entrar y cada vez que se pulsa "Reintentar"
    LaunchedEffect(intentoCarga) {
        estado = EstadoRed.Cargando
        try {
            val respuesta = RetrofitClient.apiService.obtenerTareasEjemplo()
            // Opción A: Limitamos a las primeras 20 tareas
            estado = EstadoRed.Exito(respuesta.take(20))
        } catch (e: Exception) {
            estado = EstadoRed.Error("No se pudieron cargar las tareas. Revisa tu conexión a Internet.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas de ejemplo (API)") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val estadoActual = estado) {
                is EstadoRed.Cargando -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Cargando sugerencias...")
                    }
                }

                is EstadoRed.Error -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = estadoActual.mensaje,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { intentoCarga++ }) {
                            Text("Reintentar")
                        }
                    }
                }

                is EstadoRed.Exito -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estadoActual.tareas, key = { it.id }) { tareaRemota ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                        Text(
                                            text = tareaRemota.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (tareaRemota.completed) "Estado original: Completada" else "Estado original: Pendiente",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    FilledTonalButton(
                                        onClick = { onImportarTarea(tareaRemota) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Importar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}