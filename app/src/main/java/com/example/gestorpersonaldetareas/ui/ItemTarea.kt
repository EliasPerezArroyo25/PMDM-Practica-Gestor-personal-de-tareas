package com.example.gestorpersonaldetareas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gestorpersonaldetareas.data.local.Tarea
import com.example.gestorpersonaldetareas.model.Prioridad

@Composable
fun ItemTarea(
    tarea: Tarea,
    onToggleCompletada: () -> Unit,
    onEditar: () -> Unit,
    onCompartir: () -> Unit,
    onEliminar: () -> Unit
) {
    // Estado que controla si el menú desplegable está abierto o cerrado
    var menuExpandido by remember { mutableStateOf(false) }

    val colorPrioridad = when (tarea.prioridad) {
        Prioridad.ALTA -> Color(0xFFD32F2F)
        Prioridad.MEDIA -> Color(0xFFF57C00)
        Prioridad.BAJA -> Color(0xFF388E3C)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { onToggleCompletada() }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarea.completada) TextDecoration.LineThrough else null
                )
                if (tarea.descripcion.isNotBlank()) {
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = tarea.prioridad.etiqueta,
                                color = colorPrioridad,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    Text(
                        text = if (tarea.completada) "Completada" else "Pendiente",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Contenedor Box necesario para anclar el DropdownMenu al botón de 3 puntos
            Box {
                IconButton(onClick = { menuExpandido = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones"
                    )
                }

                DropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Editar") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        onClick = {
                            menuExpandido = false
                            onEditar()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Compartir") },
                        leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                        onClick = {
                            menuExpandido = false
                            onCompartir()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpandido = false
                            onEliminar()
                        }
                    )
                }
            }
        }
    }
}
