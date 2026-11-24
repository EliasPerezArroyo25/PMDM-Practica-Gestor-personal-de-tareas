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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.R
import com.example.gestorpersonaldetareas.data.local.Tarea
import com.example.gestorpersonaldetareas.data.local.TareaDao
import com.example.gestorpersonaldetareas.data.preferences.PreferenciasManager
import kotlinx.coroutines.launch

sealed interface Pantalla {
    data object Listado : Pantalla
    data class Formulario(val tareaAEditar: Tarea? = null) : Pantalla
    data object TareasEjemplo : Pantalla
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    tareaDao: TareaDao,
    onCompartirTarea: (Tarea) -> Unit = {} // Se conectará en el Cap. 7
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 1. Instanciar PreferenciasManager y recoger su estado desde DataStore
    val preferenciasManager = remember { PreferenciasManager(context) }
    val ocultarCompletadas by preferenciasManager.ocultarCompletadasFlow.collectAsState(initial = false)

    // 2. Elegir dinámicamente el Flow de Room según el filtro
    val flujoTareas = remember(ocultarCompletadas) {
        if (ocultarCompletadas) {
            tareaDao.obtenerPendientes()
        } else {
            tareaDao.obtenerTodas()
        }
    }
    val listaTareas by flujoTareas.collectAsState(initial = emptyList())

    // Estados de navegación y diálogo de confirmación
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Listado) }
    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    if (pantallaActual is Pantalla.TareasEjemplo) {
        // PantallaTareasEjemplo ya incluye su propio Scaffold y TopAppBar
        PantallaTareasEjemplo(
            onImportarTarea = { tareaRemota ->
                scope.launch {
                    tareaDao.insertar(tareaRemota.aTareaLocal())
                    pantallaActual = Pantalla.Listado
                }
            },
            onVolver = {
                pantallaActual = Pantalla.Listado
            }
        )
    } else {
        Scaffold(
            topBar = {
                when (val pantalla = pantallaActual) {
                    is Pantalla.Listado -> {
                        TopAppBar(
                            title = { Text("Gestor de Tareas") },
                            actions = {
                                TextButton(onClick = { pantallaActual = Pantalla.TareasEjemplo }) {
                                    Text("Sugerencias")
                                }
                            }
                        )
                    }
                    is Pantalla.Formulario -> {
                        TopAppBar(
                            title = {
                                Text(if (pantalla.tareaAEditar == null) "Nueva Tarea" else "Editar Tarea")
                            },
                            navigationIcon = {
                                IconButton(onClick = { pantallaActual = Pantalla.Listado }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    }
                    else -> {}
                }
            },
            floatingActionButton = {
                if (pantallaActual is Pantalla.Listado) {
                    FloatingActionButton(
                        onClick = { pantallaActual = Pantalla.Formulario(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear tarea"
                        )
                    }
                }
            }
        ) { paddingValues ->
            when (val pantalla = pantallaActual) {
                is Pantalla.Listado -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // 3. Barra dedicada de filtro encima del LazyColumn
                        Surface(
                            tonalElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ocultar tareas completadas",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = ocultarCompletadas,
                                    onCheckedChange = { nuevoValor ->
                                        scope.launch {
                                            preferenciasManager.guardarOcultarCompletadas(nuevoValor)
                                        }
                                    }
                                )
                            }
                        }

                        // 4. Evaluar si listaTareas.isEmpty() justo debajo del Surface
                        if (listaTareas.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.empty_tareas),
                                    contentDescription = "Sin tareas",
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = if (ocultarCompletadas) {
                                        "No hay tareas pendientes con el filtro activo."
                                    } else {
                                        "No tienes tareas registradas. Pulsa + para crear una."
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(listaTareas, key = { it.id }) { tarea ->
                                    ItemTarea(
                                        tarea = tarea,
                                        onToggleCompletada = {
                                            scope.launch {
                                                tareaDao.actualizar(tarea.copy(completada = !tarea.completada))
                                            }
                                        },
                                        onEditar = {
                                            pantallaActual = Pantalla.Formulario(tarea)
                                        },
                                        onCompartir = { onCompartirTarea(tarea) },
                                        onEliminar = {
                                            tareaAEliminar = tarea
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                is Pantalla.Formulario -> {
                    FormularioTarea(
                        modifier = Modifier.padding(paddingValues),
                        tareaAEditar = pantalla.tareaAEditar,
                        onGuardar = { tarea ->
                            scope.launch {
                                if (tarea.id == 0) {
                                    tareaDao.insertar(tarea)
                                } else {
                                    tareaDao.actualizar(tarea)
                                }
                                pantallaActual = Pantalla.Listado
                            }
                        },
                        onCancelar = {
                            pantallaActual = Pantalla.Listado
                        }
                    )
                }

                else -> {}
            }
        }
    }

    // Diálogo de confirmación para eliminar tarea
    tareaAEliminar?.let { tarea ->
        AlertDialog(
            onDismissRequest = { tareaAEliminar = null },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que quieres eliminar \"${tarea.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { tareaDao.eliminar(tarea) }
                    tareaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}